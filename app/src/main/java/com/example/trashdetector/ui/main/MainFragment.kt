package com.example.trashdetector.ui.main

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.trashdetector.MainActivity.Companion.PERMISSION_CAMERA_CODE
import com.example.trashdetector.MainActivity.Companion.PERMISSION_WRITE_CODE
import com.example.trashdetector.R
import com.example.trashdetector.base.ViewModelFactory
import com.example.trashdetector.base.callbacks.*
import com.example.trashdetector.data.model.Detection
import com.example.trashdetector.data.model.History
import com.example.trashdetector.data.repository.HistoryRepository
import com.example.trashdetector.data.room.AppDatabase
import com.example.trashdetector.tflite.ClassifyAsync
import com.example.trashdetector.tflite.TfLiteHelper
import com.example.trashdetector.theme.DarkModeInterface
import com.example.trashdetector.theme.DarkModeUtil
import com.example.trashdetector.ui.information.InformationFragment
import com.example.trashdetector.base.objects.CurrentDetection
import com.example.trashdetector.ui.result.ResultDialogFragment
import com.example.trashdetector.base.objects.Constants.APP_PATH
import com.example.trashdetector.base.objects.Constants.DARK_MODE_FILE_NAME
import com.example.trashdetector.utils.FileUtils
import com.example.trashdetector.utils.ImageUtils
import com.example.trashdetector.utils.ToastUtils
import kotlinx.android.synthetic.main.main_fragment.*
import org.tensorflow.lite.Interpreter
import java.io.File

class MainFragment : Fragment(),
    SurfaceListener, DarkModeInterface,
    OnDialogActionsListener {

    private lateinit var viewModel: MainViewModel

    private lateinit var cameraId: String
    private lateinit var cameraHandler: Handler
    private lateinit var handlerThread: HandlerThread
    private lateinit var captureRequest: CaptureRequest.Builder
    private lateinit var cameraDevice: CameraDevice
    private lateinit var cameraCaptureSession: CameraCaptureSession

    private val tflite by lazy { Interpreter(TfLiteHelper.loadModelFile(activity!!)) }

    private val historyRepository by lazy {
        context?.let { HistoryRepository(AppDatabase.invoke(it).historyDao()) }
    }

    private val stateCallback =
        CameraStateCallback { cameraDevice ->
            startCameraPreview(
                cameraDevice
            )
        }

    private val captureStateCallback =
        CaptureStateCallback { cameraSession ->
            updatePreview(
                cameraSession
            )
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.main_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (DarkModeUtil.isDarkMode) enableDarkMode() else disableDarkMode()
        handleRecentIconVisible()
        initViewModel()
        prepareCamera()
        setEvents()
    }

    override fun onResume() {
        super.onResume()
        context?.let {
            if (ActivityCompat.checkSelfPermission(
                    it,
                    CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startCameraThread()
                if (cameraView.isAvailable) openCamera() else cameraView.surfaceTextureListener =
                    this
            } else {
                activity?.let { activityContext ->
                    ActivityCompat.requestPermissions(
                        activityContext, arrayOf(CAMERA), PERMISSION_CAMERA_CODE
                    )
                }
            }
        }
    }

    override fun onPause() {
        stopCameraThread()
        super.onPause()
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        openCamera()
    }

    override fun enableDarkMode() {
        context?.let {
            layoutTitle.background = it.getDrawable(R.drawable.bg_dark)
            layoutBottom.background = it.getDrawable(R.drawable.bg_dark)
            iconDarkMode.background = it.getDrawable(R.drawable.bg_ripple_dark)
            iconRecent.background = it.getDrawable(R.drawable.bg_ripple_dark)
        }
        iconDarkMode.setImageResource(R.drawable.ic_light_mode)
        iconRecent.setImageResource(R.drawable.ic_recent)
    }

    override fun disableDarkMode() {
        context?.let {
            layoutTitle.background = it.getDrawable(R.drawable.bg_light)
            layoutBottom.background = it.getDrawable(R.drawable.bg_light)
            iconDarkMode.background = it.getDrawable(R.drawable.bg_ripple_grey)
            iconRecent.background = it.getDrawable(R.drawable.bg_ripple_grey)
        }
        iconDarkMode.setImageResource(R.drawable.ic_dark_mode)
        iconRecent.setImageResource(R.drawable.ic_recent_dark)
    }

    override fun onDialogCanceled() {
        if (this::cameraDevice.isInitialized) {
            startCameraPreview(cameraDevice)
        }
        enableButtons()
    }

    override fun onDelayCreated() {
        if (this::cameraCaptureSession.isInitialized) {
            cameraCaptureSession.close()
        }
        disableButtons()
    }

    private fun detectByModel(image: Image) {
        ClassifyAsync(image, tflite) { output ->
            handleDetectResult(output, image)
        }.execute()
    }

    private fun handleDetectResult(output: Array<FloatArray>, image: Image) {
        val result = output[0]
        val maxIndex = result.indices.maxBy { result[it] } ?: -1
        val detection = Detection(
            image = ImageUtils.getBitmap(image),
            type = typeArray[maxIndex],
            percent = (result[maxIndex] * 100).toInt(),
            time = System.currentTimeMillis()
        )
        displayResult(detection)
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
                    CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity!!, arrayOf(CAMERA), PERMISSION_CAMERA_CODE
                )
            }
            if (ActivityCompat.checkSelfPermission(
                    context,
                    CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                cameraManager.openCamera(cameraId, stateCallback, null)
            }
        }
    }

    private fun startCameraPreview(cameraDevice: CameraDevice) = context?.let {
        if (ActivityCompat.checkSelfPermission(
                it,
                CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            this.cameraDevice = cameraDevice
            val surfaceTexture = cameraView.surfaceTexture
            val surface = Surface(surfaceTexture)
            captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequest.addTarget(surface)
            cameraDevice.createCaptureSession(listOf(surface), captureStateCallback, null)
        }
    }

    private fun updatePreview(session: CameraCaptureSession) = context?.let {
        if (ActivityCompat.checkSelfPermission(
                it,
                CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            captureRequest.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
            session.setRepeatingRequest(captureRequest.build(), null, cameraHandler)
            cameraCaptureSession = session
        }
    }

    private fun startCameraThread() {
        handlerThread = HandlerThread(THREAD_NAME)
        handlerThread.start()
        cameraHandler = Handler(handlerThread.looper)
    }

    private fun stopCameraThread() {
        if (this::handlerThread.isInitialized) {
            handlerThread.quitSafely()
            handlerThread.join()
        }
    }

    private fun setEvents() {
        iconInformation.setOnClickListener {
            disableButtons()
            openInformationPage()
        }
        buttonCapture.setOnClickListener {
            disableButtons()
            onCaptureClick()
        }
        cardViewCamera.setOnClickListener {
            disableButtons()
            onCaptureClick()
        }
        iconDarkMode.setOnClickListener {
            disableButtons()
            handleDarkMode()
        }
        iconRecent.setOnClickListener { showRecent() }
    }

    private fun showRecent() {
        CurrentDetection.run {
            if (isNoRecent) {
                ToastUtils.showMessage(context, getString(R.string.title_no_recent))
            } else {
                disableButtons()
                showResult(detection.image, detection.type, detection.percent, detection.time)
                cameraCaptureSession.close()
            }
        }
    }

    private fun showResult(image: Bitmap, type: String, percent: Int, time: Long) {
        ResultDialogFragment.newInstance(image, type, percent, time).apply {
            setOnDialogCancelListener(this@MainFragment)
        }.show(activity!!.supportFragmentManager, RESULT_TAG)
    }

    private fun openInformationPage() {
        InformationFragment.newInstance().apply { setOnDialogCancelListener(this@MainFragment) }
            .show(activity!!.supportFragmentManager, INFORMATION_TAG)
    }

    private fun enableButtons() {
        iconRecent?.isEnabled = true
        buttonCapture?.isEnabled = true
        iconInformation?.isEnabled = true
        iconDarkMode?.isEnabled = true
    }

    private fun disableButtons() {
        iconRecent?.isEnabled = false
        buttonCapture?.isEnabled = false
        iconInformation?.isEnabled = false
        iconDarkMode?.isEnabled = false
    }

    private fun handleDarkMode() = with(DarkModeUtil) {
        if (ActivityCompat.checkSelfPermission(
                context!!,
                WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(WRITE_EXTERNAL_STORAGE)
            requestPermissions(permissions, PERMISSION_WRITE_CODE)
        }
        if (ActivityCompat.checkSelfPermission(
                context!!,
                WRITE_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            isDarkMode = !isDarkMode
            updateLocalDarkMode()
        } else {
            ToastUtils.showMessage(context, getString(R.string.title_permission))
            enableButtons()
        }
    }

    private fun updateLocalDarkMode() {
        FileUtils.createDirectory(APP_PATH)
        val dir = File(APP_PATH)
        if (dir.isDirectory) {
            val file = File(APP_PATH + DARK_MODE_FILE_NAME)
            if (!file.exists()) {
                file.createNewFile()
            }
            FileUtils.writeStringToFile(
                APP_PATH + DARK_MODE_FILE_NAME,
                DarkModeUtil.isDarkMode.toString()
            )
        }
        activity?.recreate()
    }

    private fun onCaptureClick() = context?.let { context ->
        if (ActivityCompat.checkSelfPermission(
                context,
                CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(CAMERA)
            requestPermissions(permissions, PERMISSION_CAMERA_CODE)
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            cardCaptureEffect.visibility = View.VISIBLE
            detectProgress.visibility = View.VISIBLE
            detectTrash()
        } else {
            ToastUtils.showMessage(context, getString(R.string.title_permission))
            enableButtons()
        }
    }

    private fun detectTrash() {
        val cameraManager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val characteristics = cameraManager.getCameraCharacteristics(cameraDevice.id)
        val outputSize =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?.getOutputSizes(ImageFormat.JPEG)
        val width = outputSize!![0].width
        val height = outputSize[0].height
        val imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)
        val outputSurfaces =
            mutableListOf<Surface>(imageReader.surface)
        val captureRequest =
            cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureRequest.addTarget(imageReader.surface)
        captureRequest.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        val imageAvailableListener =
            ImageAvailableListener { reader ->
                detectByModel(reader.acquireLatestImage())
            }
        imageReader.setOnImageAvailableListener(imageAvailableListener, null)
        cameraDevice.createCaptureSession(
            outputSurfaces,
            CaptureStateCallback { cameraSession ->
                cameraSession.capture(
                    captureRequest.build(),
                    CameraCaptureListener(),
                    cameraHandler
                )
            },
            cameraHandler
        )
    }

    private fun displayResult(detection: Detection) {
        detectProgress.visibility = View.GONE
        cardCaptureEffect.visibility = View.GONE
        with(detection) {
            showResult(image, type, percent, time)
            saveHistory(this)
        }
    }

    private fun saveHistory(detection: Detection) {
        val history = History(
            type = detection.type,
            time = detection.time.toString(),
            image = ImageUtils.getStringFromBitmap(detection.image),
            percent = detection.percent
        )
        viewModel.insertHistory(history)
        CurrentDetection.createCurrentDetection(detection)
        handleRecentIconVisible()
    }

    private fun handleRecentIconVisible() {
        if (!CurrentDetection.isNoRecent) iconRecent.visibility = View.VISIBLE
    }

    companion object {

        val typeArray = arrayOf("Rác thải hữu cơ", "Rác thải tái chế", "Rác thải vô cơ")
        private const val THREAD_NAME = "Camera"
        private const val RESULT_TAG = "Result"
        private const val INFORMATION_TAG = "Information"
    }
}
