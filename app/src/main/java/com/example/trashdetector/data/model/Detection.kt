package com.example.trashdetector.data.model

import android.graphics.Bitmap

data class Detection(
    var image: Bitmap,
    var type: String,
    var percent: Int,
    var time: Long
)
