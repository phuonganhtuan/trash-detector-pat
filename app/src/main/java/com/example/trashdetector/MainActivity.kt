package com.example.trashdetector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trashdetector.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) openDetectorPage()
    }

    private fun openDetectorPage() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment.newInstance())
            .commitNow()
    }

}
