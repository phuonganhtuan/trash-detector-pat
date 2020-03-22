package com.example.trashdetector.ui.history

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.trashdetector.data.model.History

class HistoryStateAdapter internal constructor(
    fragmentManager: FragmentManager,
    private val histories: List<History>,
    private val onHistoryCancelListener: OnHistoryCancelListener
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount() = histories.size

    override fun getItem(position: Int) =
        HistoryFragment.newInstance(histories[position]).apply {
            setOnHistoryCancelListener(onHistoryCancelListener)
        }
}
