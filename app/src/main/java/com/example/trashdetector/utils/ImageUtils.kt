package com.example.trashdetector.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder


object ImageUtils {

    private const val BATCH_SIZE = 1
    private const val PIXEL_SIZE = 3

    private const val IMAGE_MEAN = 128f
    private const val IMAGE_STD = 128f

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

    fun resizeBitmap(size: Int, bitmap: Bitmap) =
        Bitmap.createScaledBitmap(bitmap, size, size, true)

    fun getBitmap(base64: String): Bitmap {
        val decodedString: ByteArray =
            Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer? {
        val newBitmap = resizeBitmap(299, bitmap)
        val byteBuffer =
            ByteBuffer.allocateDirect(4 * BATCH_SIZE * 299 * 299 * PIXEL_SIZE)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(299 * 299)
        newBitmap.getPixels(
            intValues,
            0,
            newBitmap.width,
            0,
            0,
            newBitmap.width,
            newBitmap.height
        )
        var pixel = 0
        for (i in 0 until 299) {
            for (j in 0 until 299) {
                val `val` = intValues[pixel++]
                byteBuffer.putFloat(((`val` shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                byteBuffer.putFloat(((`val` shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                byteBuffer.putFloat(((`val` and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
            }
        }
        return byteBuffer
    }
}
