package com.example.trashdetector.ui.guide

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.trashdetector.MainActivity
import com.example.trashdetector.R
import com.example.trashdetector.data.model.Guider
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_user_guide.*

class UserGuideActivity : AppCompatActivity() {

    private val guiderAdapter = GuideAdapter()
    private val guiderList = mutableListOf<Guider>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_guide)
        setupViews()
        triggerPager()
        handleEvents()
    }

    private fun goToMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun handleEvents() {
        buttonStart.setOnClickListener { goToMainScreen() }
        buttonSkip.setOnClickListener { goToMainScreen() }
        buttonOut.setOnClickListener { finish() }
        buttonCancel.setOnClickListener { finish() }
    }

    private fun setupViews() {
        pagerGuide.adapter = guiderAdapter
        val guider1 = Guider(
            imageId = ContextCompat.getDrawable(this, R.drawable.main_guide),
            title = getString(R.string.title_main),
            content = getString(R.string.title_main_content)
        )
        val guider2 = Guider(
            imageId = ContextCompat.getDrawable(this, R.drawable.result_guide),
            title = getString(R.string.title_result_guide),
            content = getString(R.string.title_result_content)
        )
        val guider3 = Guider(
            imageId = ContextCompat.getDrawable(this, R.drawable.detail_guide),
            title = getString(R.string.title_detail_guide),
            content = getString(R.string.title_detail_content)
        )
        val guider4 = Guider(
            imageId = ContextCompat.getDrawable(this, R.drawable.history_guide),
            title = getString(R.string.title_his_guide),
            content = getString(R.string.title_his_content)
        )
        val guider5 = Guider(
            imageId = ContextCompat.getDrawable(this, R.drawable.done_guide),
            title = getString(R.string.app_name),
            content = getString(R.string.title_done)
        )
        guiderList.apply {
            add(guider1)
            add(guider2)
            add(guider3)
            add(guider4)
            add(guider5)
        }
        guiderAdapter.guideList = guiderList
        TabLayoutMediator(tabGuide, pagerGuide) { tab, position -> }.attach()
    }

    private fun triggerPager() {
        pagerGuide.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == guiderList.size - 1) {
                    buttonSkip.visibility = View.GONE
                    buttonCancel.visibility = View.GONE
                    buttonOut.visibility = View.VISIBLE
                    buttonStart.visibility = View.VISIBLE
                } else {
                    buttonOut.visibility = View.GONE
                    buttonStart.visibility = View.GONE
                    buttonSkip.visibility = View.VISIBLE
                    buttonCancel.visibility = View.VISIBLE
                }
            }
        })
    }

}