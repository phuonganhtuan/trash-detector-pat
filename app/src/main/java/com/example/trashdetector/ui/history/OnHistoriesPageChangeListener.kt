package com.example.trashdetector.ui.history

import androidx.viewpager.widget.ViewPager

interface OnHistoriesPageChangeListener : ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageSelected(position: Int)
}
