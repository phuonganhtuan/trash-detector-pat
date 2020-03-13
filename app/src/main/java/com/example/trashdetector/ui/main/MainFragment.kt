package com.example.trashdetector.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.trashdetector.R
import com.example.trashdetector.base.ViewModelFactory
import com.example.trashdetector.data.model.History
import com.example.trashdetector.data.repository.HistoryRepository
import com.example.trashdetector.data.room.AppDatabase
import com.example.trashdetector.ui.information.InformationFragment
import com.example.trashdetector.ui.result.ResultDialogFragment
import com.example.trashdetector.utils.ImageUtils
import kotlinx.android.synthetic.main.main_fragment.*


class MainFragment private constructor() : Fragment(), SurfaceListener {

    private lateinit var viewModel: MainViewModel

    private lateinit var cameraId: String
    private lateinit var cameraHandler: Handler
    private lateinit var handlerThread: HandlerThread
    private lateinit var captureRequest: CaptureRequest.Builder
    private lateinit var cameraDevice: CameraDevice

    private val historyRepository by lazy {
        context?.let { HistoryRepository(AppDatabase.invoke(it).historyDao()) }
    }

    private val stateCallback =
        CameraStateCallback { cameraDevice -> startCameraPreview(cameraDevice) }
    private val captureStateCallback =
        CaptureStateCallback { cameraSession -> updatePreview(cameraSession) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.main_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        prepareCamera()
        setEvents()
    }

    override fun onResume() {
        super.onResume()
        startCameraThread()
        if (cameraView.isAvailable) openCamera() else cameraView.surfaceTextureListener = this
    }

    override fun onPause() {
        stopCameraThread()
        super.onPause()
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        openCamera()
    }

    private fun initViewModel() {
        historyRepository?.let {
            viewModel = ViewModelProviders.of(
                this,
                ViewModelFactory { MainViewModel(it) }).get(MainViewModel::class.java)
        }
    }

    private fun prepareCamera() {
        cameraView.surfaceTextureListener = this
    }

    private fun openCamera() {
        val cameraManager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList[0]
        openCamera(cameraId, cameraManager)
    }

    private fun openCamera(cameraId: String, cameraManager: CameraManager) {
        context?.let { context ->
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity!!, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE
                )
            }
            cameraManager.openCamera(cameraId, stateCallback, null)
        }
    }

    private fun startCameraPreview(cameraDevice: CameraDevice) {
        this.cameraDevice = cameraDevice
        val surfaceTexture = cameraView.surfaceTexture
        val surface = Surface(surfaceTexture)
        captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequest.addTarget(surface)
        cameraDevice.createCaptureSession(listOf(surface), captureStateCallback, null)
    }

    private fun updatePreview(session: CameraCaptureSession) {
        captureRequest.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        session.setRepeatingRequest(captureRequest.build(), null, cameraHandler)
    }

    private fun startCameraThread() {
        handlerThread = HandlerThread(THREAD_NAME)
        handlerThread.start()
        cameraHandler = Handler(handlerThread.looper)
    }

    private fun stopCameraThread() {
        handlerThread.quitSafely()
        handlerThread.join()
    }

    private fun setEvents() {
        iconInformation.setOnClickListener {
            InformationFragment.newInstance()
                .show(activity!!.supportFragmentManager, INFORMATION_TAG)
        }
        buttonCapture.setOnClickListener {
            onCaptureClick()
        }
        cardViewCamera.setOnClickListener {
            onCaptureClick()
        }
    }

    private fun onCaptureClick() {
        buttonCapture.isEnabled = false
        cardCaptureEffect.visibility = View.VISIBLE
        detectProgress.visibility = View.VISIBLE
        detectTrash()
    }

    private fun detectTrash() {
        val cameraManager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val characteristics = cameraManager.getCameraCharacteristics(cameraDevice.id)
        val outputSize = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            ?.getOutputSizes(ImageFormat.JPEG)
        val width = outputSize!![0].width
        val height = outputSize[0].height
        val imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)
        val outputSurfaces =
            mutableListOf<Surface>(imageReader.surface)
        val captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureRequest.addTarget(imageReader.surface)
        captureRequest.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        val imageAvailableListener = ImageAvailableListener { reader ->
            beginDetection(reader.acquireLatestImage())
            startCameraPreview(cameraDevice)
        }
        imageReader.setOnImageAvailableListener(imageAvailableListener, null)
        cameraDevice.createCaptureSession(outputSurfaces, CaptureStateCallback { cameraSession ->
            cameraSession.capture(captureRequest.build(), CameraCaptureListener(), cameraHandler)
        }, cameraHandler)
    }

    private fun beginDetection(outputImage: Image) {
        val bitmapOutput = ImageUtils.getBitmap(outputImage)
        detectProgress.visibility = View.GONE
        cardCaptureEffect.visibility = View.GONE
        ResultDialogFragment.newInstance(bitmapOutput, " Rác hữu cơ")
            .show(activity!!.supportFragmentManager, RESULT_TAG)
        buttonCapture.isEnabled = true
        saveHistory(outputImage, "Rác hữu cơ")
    }

    private fun saveHistory(image: Image, type: String) {
        val history = History(
            type = type,
            time = System.currentTimeMillis().toString(),
            image = ImageUtils.getStringFromBitmap(ImageUtils.getBitmap(image))
        )
        viewModel.insertHistory(history)
    }

    companion object {

        private const val THREAD_NAME = "Camera"
        private const val REQUEST_CODE = 8898
        private const val RESULT_TAG = "Result"
        private const val INFORMATION_TAG = "Information"

        fun newInstance() = MainFragment()
    }
}
