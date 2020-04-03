package com.example.trashdetector.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
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
        return rotateBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null))
    }

    fun getStringFromBitmap(bitmap: Bitmap): String {
        val outputBitmap = resizeBitmap(300, bitmap, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
    }

    fun getBitmap(base64: String): Bitmap {
        val decodedString: ByteArray =
            Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val size = 299
        val newBitmap = resizeBitmap(size, bitmap)
        val byteBuffer =
            ByteBuffer.allocateDirect(4 * BATCH_SIZE * size * size * PIXEL_SIZE)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(size * size)
        newBitmap.getPixels(
            intValues,
            0,
            newBitmap.width,
            0,
            0,
            newBitmap.width,
            newBitmap.width
        )
        var pixel = 0
        for (i in 0 until size) {
            for (j in 0 until size) {
                val `val` = intValues[pixel++]
                byteBuffer.putFloat(((`val` shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                byteBuffer.putFloat(((`val` shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                byteBuffer.putFloat(((`val` and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
            }
        }
        return byteBuffer
    }

    private fun rotateBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90f)
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    private fun resizeBitmap(size: Int, bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, size, size, true)
    }

    private fun resizeBitmap(size: Int, bitmap: Bitmap, isSquare: Boolean): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, size, size * bitmap.height / bitmap.width, true)
    }
}
