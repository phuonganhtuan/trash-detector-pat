package com.example.trashdetector.ui.history

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.trashdetector.R
import com.example.trashdetector.base.callbacks.OnHistoriesPageChangeListener
import com.example.trashdetector.base.callbacks.OnHistoryCancelListener
import com.example.trashdetector.data.model.History
import com.example.trashdetector.theme.DarkModeInterface
import com.example.trashdetector.theme.DarkModeUtil
import kotlinx.android.synthetic.main.history_fragment.*

class HistoryPagerFragment private constructor(
    private val histories: List<History>,
    private val currentPosition: Int
) :
    DialogFragment(), DarkModeInterface,
    OnHistoriesPageChangeListener,
    OnHistoryCancelListener {

    private lateinit var historyAdapter: HistoryStateAdapter
    private var currentAdapterPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.history_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.PopupAnimation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (DarkModeUtil.isDarkMode) enableDarkMode() else disableDarkMode()
        setupPager()
        setEvents()
    }

    override fun disableDarkMode() {
        iconNext.background = context?.getDrawable(R.drawable.bg_ripple_white)
        iconPrevious.background = context?.getDrawable(R.drawable.bg_ripple_white)
        layoutHistory.background = context?.getDrawable(R.drawable.bg_rounded)
    }

    override fun enableDarkMode() {
        iconNext.background = context?.getDrawable(R.drawable.bg_ripple_dark)
        iconPrevious.background = context?.getDrawable(R.drawable.bg_ripple_dark)
        layoutHistory.background = context?.getDrawable(R.drawable.bg_rounded_dark)
    }

    override fun onPageSelected(position: Int) {
        currentAdapterPosition = position
        handleIcons()
    }

    override fun onHistoryCancel() {
        dialog?.cancel()
    }

    private fun handleIcons() {
        iconNext.visibility =
            if (currentAdapterPosition >= histories.size - 1) View.INVISIBLE else View.VISIBLE
        iconPrevious.visibility =
            if (currentAdapterPosition <= 0) View.INVISIBLE else View.VISIBLE
    }

    private fun setupPager() {
        historyAdapter = HistoryStateAdapter(childFragmentManager, histories, this)
        viewPagerHistory.apply {
            adapter = historyAdapter
            addOnPageChangeListener(this@HistoryPagerFragment)
            setCurrentItem(currentPosition, true)
            currentAdapterPosition = currentPosition
        }
        handleIcons()
    }

    private fun setEvents() {
        iconNext.setOnClickListener { displayNextHistory() }
        iconPrevious.setOnClickListener { displayPreviousHistory() }
    }

    private fun displayNextHistory() {
        if (currentAdapterPosition < histories.size - 1) {
            viewPagerHistory.setCurrentItem(currentAdapterPosition + 1, true)
        }
    }

    private fun displayPreviousHistory() {
        if (currentAdapterPosition > 0) {
            viewPagerHistory.setCurrentItem(currentAdapterPosition - 1, true)
        }
    }

    companion object {

        fun newInstance(histories: List<History>, currentPosition: Int) =
            HistoryPagerFragment(histories, currentPosition)
    }

}
