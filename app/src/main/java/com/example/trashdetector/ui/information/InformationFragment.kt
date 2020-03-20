package com.example.trashdetector.ui.information

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.example.trashdetector.ui.detail.DetailDialogFragment
import com.example.trashdetector.ui.main.OnDialogActionsListener
import com.example.trashdetector.utils.InternetUtils
import com.example.trashdetector.utils.ToastUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.information_fragment.*
import kotlinx.android.synthetic.main.trash_item.view.*


class InformationFragment private constructor() : BottomSheetDialogFragment(), DarkModeInterface {

    private lateinit var viewModel: InformationViewModel

    private val historyAdapter by lazy { HistoryAdapter() }

    private var onDialogActionsListener: OnDialogActionsListener? = null

    private lateinit var closeCameraHandler: Handler
    private lateinit var fetchDataHandler: Handler

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
        pauseCamera()
        displayHistory()
    }

    private fun displayHistory() {
        fetchDataHandler = Handler()
        fetchDataHandler.postDelayed({
            viewModel.getHistories()
            observeData()
        }, 1000)
    }

    private fun pauseCamera() {
        closeCameraHandler = Handler()
        closeCameraHandler.postDelayed({
            onDialogActionsListener?.onDelayCreated()
        }, 3000)
    }

    override fun enableDarkMode() {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogDark)
    }

    override fun disableDarkMode() {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onDestroy() {
        onDialogActionsListener?.onDialogCanceled()
        closeCameraHandler.removeCallbacksAndMessages(null)
        fetchDataHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    fun setOnDialogCancelListener(onDialogActionsListener: OnDialogActionsListener) {
        this.onDialogActionsListener = onDialogActionsListener
    }

    private fun setDarkItems() {
        val backRipple = context?.getDrawable(R.drawable.bg_ripple_black)
        pages.background = context?.getDrawable(R.drawable.bg_dark)
        iconDelete.background = backRipple
        iconAbout.background = backRipple
        textMore.background = context?.getDrawable(R.drawable.bg_ripple_orange_outlined_dark)
    }

    private fun setLightItems() {
        val whiteRipple = context?.getDrawable(R.drawable.bg_ripple_white)
        pages.background = context?.getDrawable(R.drawable.bg_light)
        iconDelete.background = whiteRipple
        iconAbout.background = whiteRipple
        textMore.background = context?.getDrawable(R.drawable.bg_ripple_orange_outlined)
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
        val description1 = getString(R.string.des_1)
        val description2 = getString(R.string.des_2)
        val description3 = getString(R.string.des_3)
        val bg1 = context?.getDrawable(R.drawable.bg_gradient_orange)
        val bg2 = context?.getDrawable(R.drawable.bg_gradient_green)
        val bg3 = context?.getDrawable(R.drawable.bg_gradient_purple)

        layoutTrash1.run {
            textTitle.text = title1
            textDescription.text = description1
            layoutTrash.background = bg1
            imageTrash.setImageResource(R.drawable.vo_co)
        }

        layoutTrash2.run {
            textTitle.text = title2
            textDescription.text = description2
            layoutTrash.background = bg2
            imageTrash.setImageResource(R.drawable.huu_co)
        }

        layoutTrash3.run {
            textTitle.text = title3
            textDescription.text = description3
            layoutTrash.background = bg3
            imageTrash.setImageResource(R.drawable.tai_che)
        }
        setupHistoryList()
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
            DetailDialogFragment(1, null).show(activity!!.supportFragmentManager, DETAIL_TAG)
        }
        cardTrash2.setOnClickListener {
            DetailDialogFragment(2, null).show(activity!!.supportFragmentManager, DETAIL_TAG)
        }
        cardTrash3.setOnClickListener {
            DetailDialogFragment(3, null).show(activity!!.supportFragmentManager, DETAIL_TAG)
        }
        iconDelete.setOnClickListener { resetHistory() }
        iconAbout.setOnClickListener {
            AboutDialogFragment.newInstance().show(activity!!.supportFragmentManager, ABOUT_TAG)
        }
        textMore.setOnClickListener { openWiki() }
    }

    private fun openWiki() = context?.run {
        if (InternetUtils.isInternetAvailable(this)) {
            InformationWebFragment.newInstance().show(activity!!.supportFragmentManager, WEB_TAG)
        } else {
            ToastUtils.showMessage(this, getString(R.string.msg_no_internet))
        }
    }

    private fun resetHistory() {
        if (historyAdapter.currentList.isEmpty()) {
            ToastUtils.showMessage(context, getString(R.string.title_empty_history))
            return
        }
        viewModel.resetHistories()
        historyAdapter.submitList(emptyList())
        textEmptyHistory.visibility = View.VISIBLE
        ToastUtils.showMessage(context, getString(R.string.title_reset))
    }

    companion object {

        private const val ABOUT_TAG = "About"
        private const val DETAIL_TAG = "Detail"
        private const val WEB_TAG = "Web"

        fun newInstance() = InformationFragment()
    }
}
