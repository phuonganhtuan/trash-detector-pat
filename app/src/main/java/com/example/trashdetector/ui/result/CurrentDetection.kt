package com.example.trashdetector.ui.result

import android.graphics.Bitmap
import com.example.trashdetector.data.model.Detection

object CurrentDetection {

    private lateinit var currentDetection: Detection
    var isNoRecent = true

    fun createCurrentDetection(image: Bitmap, type: String, percent: Int) {
        currentDetection = Detection(
            image = image,
            type = type,
            percent = percent,
            time = System.currentTimeMillis()
        )
        isNoRecent = false
    }

    val detection get() = currentDetection
}
