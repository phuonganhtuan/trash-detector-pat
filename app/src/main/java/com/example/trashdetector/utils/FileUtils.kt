package com.example.trashdetector.utils

import android.util.Log
import java.io.*

object FileUtils {

    fun createDirectory(dirPath: String) {
        val directory = File(dirPath)
        if (!directory.exists()) {
            directory.mkdirs()
            Log.i("filea", directory.absolutePath)
        }
    }

    fun writeStringToFile(filePath: String, string: String) {
        val file = File(filePath)
        if (file.exists()) {
            val bufferedWriter = BufferedWriter(FileWriter(file))
            bufferedWriter.write(string)
            bufferedWriter.close()
        }
        Log.i("filea", file.absolutePath)
    }

    fun readStringFromFile(filePath: String): String? {
        val file = File(filePath)
        if (file.exists()) {
            val bufferedReader = BufferedReader(FileReader(file))
            val password = bufferedReader.readLine()
            if (password != null) return password
        }
        return null
    }
}
