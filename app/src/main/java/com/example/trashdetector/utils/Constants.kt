package com.example.trashdetector.utils

import android.os.Environment

object Constants {

    const val MODEL_NAME = "trash_graph.tflite"
    val APP_PATH = Environment.getExternalStorageDirectory().absolutePath + "/TrashDetector/"
    const val DARK_MODE_FILE_NAME = "td-st-dm"
    const val WIKI_URL = "https://vi.m.wikipedia.org/wiki/Ph%C3%A2n_lo%E1%BA%A1i_ch%E1%BA%A5t_th%E1%BA%A3i"
}
