package com.example.trashdetector.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
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
import com.example.trashdetector.ui.pages.InformationFragment
import com.example.trashdetector.ui.result.ResultDialogFragment
import kotlinx.android.synthetic.main.main_fragment.*
import java.nio.ByteBuffer


class MainFragment private constructor() : Fragment(), SurfaceListener {

    private lateinit var viewModel: MainViewModel

    private lateinit var cameraId: String
    private lateinit var cameraHandler: Handler
    private lateinit var handlerThread: HandlerThread
    private lateinit var captureRequest: CaptureRequest.Builder
    private lateinit var cameraDevice: CameraDevice

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
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
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
        val buffer: ByteBuffer = outputImage.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        val bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
        detectProgress.visibility = View.GONE
        cardCaptureEffect.visibility = View.GONE
        ResultDialogFragment.newInstance(bitmapImage, " Rác hữu cơ")
            .show(activity!!.supportFragmentManager, RESULT_TAG)
        buttonCapture.isEnabled = true
    }

    companion object {

        private const val THREAD_NAME = "Camera"
        private const val REQUEST_CODE = 8898
        private const val RESULT_TAG = "Result"
        private const val INFORMATION_TAG = "Information"

        fun newInstance() = MainFragment()
    }
}
