package com.example.trashdetector.ui.information

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.trashdetector.R
import com.example.trashdetector.theme.DarkModeInterface
import com.example.trashdetector.theme.DarkModeUtil
import com.example.trashdetector.utils.Constants.WIKI_URL
import kotlinx.android.synthetic.main.information_web_fragment.*


class InformationWebFragment private constructor() : DialogFragment(), DarkModeInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.information_web_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.SlideAnimation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (DarkModeUtil.isDarkMode) enableDarkMode() else disableDarkMode()
        setupData()
        setEvents()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun disableDarkMode() {
        iconBack.background = context?.getDrawable(R.drawable.bg_ripple_white)
    }

    override fun enableDarkMode() {
        iconBack.background = context?.getDrawable(R.drawable.bg_ripple_black)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupData() {
        webInformation.apply {
            settings.javaScriptEnabled = true
            loadUrl(WIKI_URL)
        }
    }

    private fun setEvents() {
        iconBack.setOnClickListener { dialog?.cancel() }
    }

    companion object {

        fun newInstance() = InformationWebFragment()
    }
}
