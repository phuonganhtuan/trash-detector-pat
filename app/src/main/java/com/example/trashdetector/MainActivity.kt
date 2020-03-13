package com.example.trashdetector

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trashdetector.theme.DarkModeInterface
import com.example.trashdetector.theme.DarkModeUtil
import com.example.trashdetector.ui.main.MainFragment
import com.example.trashdetector.utils.Constants.APP_PATH
import com.example.trashdetector.utils.Constants.DARK_MODE_FILE_NAME
import com.example.trashdetector.utils.FileUtils
import java.io.File

class MainActivity : AppCompatActivity(), DarkModeInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (getDarkModeStatus()) enableDarkMode() else disableDarkMode()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        requestAppPermissions()
        if (savedInstanceState == null) openDetectorPage()
    }

    override fun disableDarkMode() {
        setTheme(R.style.AppTheme)
    }

    override fun enableDarkMode() {
        setTheme(R.style.AppThemeDark)
    }

    private fun requestAppPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permissions, PERMISSION_WRITE_CODE)
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(Manifest.permission.CAMERA)
            requestPermissions(permissions, PERMISSION_CAMERA_CODE)
        }
    }

    private fun getDarkModeStatus(): Boolean {
        var isDarkMode = false
        val dir = File(APP_PATH)
        if (dir.exists() && dir.isDirectory) {
            val darkModeStatus = FileUtils.readStringFromFile(APP_PATH + DARK_MODE_FILE_NAME)
            if (darkModeStatus == TRUE_STRING || darkModeStatus == FALSE_STRING) {
                isDarkMode = darkModeStatus.toBoolean()
            }
        }
        DarkModeUtil.isDarkMode = isDarkMode
        return isDarkMode
    }

    private fun openDetectorPage() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment())
            .commitNow()
    }

    companion object {

        const val PERMISSION_CAMERA_CODE = 110
        private const val TRUE_STRING = "true"
        private const val FALSE_STRING = "false"
        private const val PERMISSION_WRITE_CODE = 101
    }
}
