package com.example.trashdetector.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

object ImageUtils {

    fun getBitmap(image: Image): Bitmap {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.position(0)
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
    }

    fun getStringFromBitmap(bitmap: Bitmap): String {
        val outputBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, true)
        val byteArrayOutputStream = ByteArrayOutputStream()
        outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
    }

    fun getBitmap(base64: String): Bitmap {
        val decodedString: ByteArray =
            Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

}
