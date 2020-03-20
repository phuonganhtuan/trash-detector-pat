package com.example.trashdetector.tflite

import android.media.Image
import android.os.AsyncTask
import com.example.trashdetector.utils.ImageUtils
import org.tensorflow.lite.Interpreter

class ClassifyAsync(
    private val image: Image,
    private val tflite: Interpreter,
    private val onDone: (output: Array<FloatArray>) -> Unit
) :
    AsyncTask<Unit, Unit, Array<FloatArray>>() {

    override fun doInBackground(vararg params: Unit?): Array<FloatArray> {
        val output = Array(1) { FloatArray(3) }
        tflite.run(
            ImageUtils.convertBitmapToByteBuffer(ImageUtils.getBitmap(image)),
            output
        )
        return output
    }

    override fun onPostExecute(result: Array<FloatArray>?) {
        onDone(result!!)
    }
}
