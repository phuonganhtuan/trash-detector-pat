package com.example.trashdetector.base.callbacks

import androidx.viewpager.widget.ViewPager

interface OnHistoriesPageChangeListener : ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageSelected(position: Int)
}
