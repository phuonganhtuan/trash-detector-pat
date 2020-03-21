package com.example.trashdetector.base.callbacks

import android.graphics.SurfaceTexture
import android.view.TextureView

interface SurfaceListener : TextureView.SurfaceTextureListener {
    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int)
    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean = false
    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {}
    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {}
}
