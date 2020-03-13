package com.example.trashdetector.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Base64
import java.nio.ByteBuffer

object ImageUtils {

    fun getBitmap(image: Image): Bitmap {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
    }

    fun getStringFromImage(image: Image): String {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
}
