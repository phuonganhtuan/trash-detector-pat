package com.example.trashdetector.utils

import android.os.Environment

object Constants {

    const val MODEL_NAME = "trash_graph.tflite"
    val APP_PATH = Environment.getExternalStorageDirectory().absolutePath + "/TrashDetector/"
    const val DARK_MODE_FILE_NAME = "td-st-dm"
}
