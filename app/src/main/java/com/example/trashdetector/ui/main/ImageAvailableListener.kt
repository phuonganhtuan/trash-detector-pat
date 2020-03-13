package com.example.trashdetector.ui.main

import android.media.ImageReader

class ImageAvailableListener(private val getOutputImage: (reader: ImageReader) -> Unit) :
    ImageReader.OnImageAvailableListener {

    override fun onImageAvailable(reader: ImageReader?) = getOutputImage(reader!!)
}
