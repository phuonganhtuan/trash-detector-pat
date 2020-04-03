package com.example.trashdetector.base.objects

import com.example.trashdetector.data.model.Detection

object CurrentDetection {

    private lateinit var currentDetection: Detection
    var isNoRecent = true

    fun createCurrentDetection(detection: Detection) {
        currentDetection = Detection(
            image = detection.image,
            type = detection.type,
            percent = detection.percent,
            time = detection.time
        )
        isNoRecent = false
    }

    val detection get() = currentDetection
}
