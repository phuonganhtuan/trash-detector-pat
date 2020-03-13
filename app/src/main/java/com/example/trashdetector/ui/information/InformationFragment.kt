package com.example.trashdetector.ui.information

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.trashdetector.R
import com.example.trashdetector.base.ViewModelFactory
import com.example.trashdetector.data.repository.HistoryRepository
import com.example.trashdetector.data.room.AppDatabase
import com.example.trashdetector.theme.DarkModeInterface
import com.example.trashdetector.theme.DarkModeUtil
import com.example.trashdetector.ui.about.AboutDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.information_fragment.*
import kotlinx.android.synthetic.main.trash_item.view.*

class InformationFragment private constructor() : BottomSheetDialogFragment(), DarkModeInterface {

    private lateinit var viewModel: InformationViewModel

    private val historyAdapter by lazy { HistoryAdapter() }

    private val historyRepository by lazy {
        context?.let { HistoryRepository(AppDatabase.invoke(it).historyDao()) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (DarkModeUtil.isDarkMode) enableDarkMode() else disableDarkMode()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.information_fragment, container, false)

    override fun setupDialog(dialog: Dialog, style: Int) {
        dialog.setOnShowListener {
            val bottomSheet = (it as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        setupData()
        setEvents()
        if (DarkModeUtil.isDarkMode) setDarkItems() else setLightItems()
    }

    override fun enableDarkMode() {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogDark)
    }

    override fun disableDarkMode() {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    private fun setDarkItems() {
        pages.background = context?.getDrawable(R.drawable.bg_dark)
        iconDelete.background = context?.getDrawable(R.drawable.bg_ripple_black)
        iconAbout.background = context?.getDrawable(R.drawable.bg_ripple_black)
    }

    private fun setLightItems() {
        pages.background = context?.getDrawable(R.drawable.bg_light)
        iconDelete.background = context?.getDrawable(R.drawable.bg_ripple_white)
        iconAbout.background = context?.getDrawable(R.drawable.bg_ripple_white)
    }

    private fun initViewModel() {
        historyRepository?.let {
            viewModel = ViewModelProviders.of(
                this,
                ViewModelFactory { InformationViewModel(it) }).get(InformationViewModel::class.java)
        }
    }

    private fun setupData() {
        val title1 = getString(R.string.title_trash_1)
        val title2 = getString(R.string.title_trash_2)
        val title3 = getString(R.string.title_trash_3)
        val bg1 = context!!.getDrawable(R.drawable.bg_gradient_orange)
        val bg2 = context!!.getDrawable(R.drawable.bg_gradient_green)
        val bg3 = context!!.getDrawable(R.drawable.bg_gradient_purple)

        layoutTrash1.run {
            textTitle.text = title1
            layoutTrash.background = bg1
            imageTrash.setImageResource(R.drawable.vo_co)
        }

        layoutTrash2.run {
            textTitle.text = title2
            layoutTrash.background = bg2
            imageTrash.setImageResource(R.drawable.huu_co)
        }

        layoutTrash3.run {
            textTitle.text = title3
            layoutTrash.background = bg3
            imageTrash.setImageResource(R.drawable.tai_che)
        }
        setupHistoryList()
        viewModel.getHistories()
        observeData()
    }

    private fun setupHistoryList() {
        recyclerHistory.adapter = historyAdapter
    }

    private fun observeData() = with(viewModel) {
        historyList.observe(viewLifecycleOwner, Observer {
            historyAdapter.submitList(it.asReversed())
            if (it.isEmpty()) textEmptyHistory.visibility = View.VISIBLE
        })
    }

    private fun setEvents() {
        cardTrash1.setOnClickListener {
            showComing()
        }
        cardTrash2.setOnClickListener {
            showComing()
        }
        cardTrash3.setOnClickListener {
            showComing()
        }
        iconAbout.setOnClickListener {
            AboutDialogFragment.newInstance().show(activity!!.supportFragmentManager, ABOUT_TAG)
        }
        iconDelete.setOnClickListener { resetHistory() }
    }

    private fun resetHistory() {
        if (historyAdapter.currentList.isEmpty()) {
            Toast.makeText(context, getString(R.string.title_empty_history), Toast.LENGTH_SHORT)
                .show()
            return
        }
        viewModel.resetHistories()
        historyAdapter.submitList(emptyList())
        textEmptyHistory.visibility = View.VISIBLE
        Toast.makeText(context, getString(R.string.title_reset), Toast.LENGTH_SHORT).show()
    }

    private fun showComing() {
        Toast.makeText(context, "Comming soon!", Toast.LENGTH_SHORT).show()
    }

    companion object {

        private const val ABOUT_TAG = "About"

        fun newInstance() = InformationFragment()
    }
}
