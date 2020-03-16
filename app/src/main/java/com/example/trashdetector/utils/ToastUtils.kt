package com.example.trashdetector.utils

import android.content.Context
import android.widget.Toast

object ToastUtils {

    fun showMessage(context: Context?, message: String) = context?.let {
        Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
    }
}
