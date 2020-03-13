package com.example.trashdetector.ui.main

import android.hardware.camera2.CameraDevice

class CameraStateCallback(private val startCameraPreview: (cameraDevice: CameraDevice) -> Unit) :
    CameraDevice.StateCallback() {

    override fun onDisconnected(camera: CameraDevice) = camera.close()
    override fun onError(camera: CameraDevice, error: Int) = camera.close()
    override fun onOpened(camera: CameraDevice) = startCameraPreview(camera)
}
