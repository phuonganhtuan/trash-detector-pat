package com.example.trashdetector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trashdetector.theme.DarkModeInterface
import com.example.trashdetector.theme.DarkModeUtil
import com.example.trashdetector.ui.main.MainFragment

class MainActivity : AppCompatActivity(), DarkModeInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (DarkModeUtil.isDarkMode) enableDarkMode() else disableDarkMode()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) openDetectorPage()
    }

    override fun disableDarkMode() {
        setTheme(R.style.AppTheme)
    }

    override fun enableDarkMode() {
        setTheme(R.style.AppThemeDark)
    }

    private fun openDetectorPage() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment())
            .commitNow()
    }
}
