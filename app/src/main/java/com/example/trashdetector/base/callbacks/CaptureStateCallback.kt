package com.example.trashdetector.base.callbacks

import android.hardware.camera2.CameraCaptureSession

class CaptureStateCallback(private val callback: (cameraSession: CameraCaptureSession) -> Unit) :
    CameraCaptureSession.StateCallback() {

    override fun onConfigureFailed(session: CameraCaptureSession) {}
    override fun onConfigured(session: CameraCaptureSession) = callback(session)
}
