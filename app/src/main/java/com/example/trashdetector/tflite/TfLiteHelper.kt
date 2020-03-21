package com.example.trashdetector.tflite

import android.app.Activity
import com.example.trashdetector.base.objects.Constants
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

object TfLiteHelper {

    fun loadModelFile(activity: Activity): MappedByteBuffer {
        val modelAsset = activity.assets.openFd(Constants.MODEL_NAME)
        val descriptor = modelAsset.fileDescriptor
        val inputStream = FileInputStream(descriptor)
        val fileChannel: FileChannel = inputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            modelAsset.startOffset,
            modelAsset.declaredLength
        )
    }

}
