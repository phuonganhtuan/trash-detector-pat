package com.example.trashdetector.ui.about

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.trashdetector.R
import com.example.trashdetector.theme.DarkModeInterface
import com.example.trashdetector.theme.DarkModeUtil
import kotlinx.android.synthetic.main.about_dialog_fragment.*

class AboutDialogFragment private constructor() : DialogFragment(), DarkModeInterface {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.about_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (DarkModeUtil.isDarkMode) enableDarkMode() else disableDarkMode()
    }

    override fun enableDarkMode() {
        layoutAbout.background = context?.getDrawable(R.drawable.bg_rounded_dark)
    }

    override fun disableDarkMode() {
        layoutAbout.background = context?.getDrawable(R.drawable.bg_rounded)
    }

    companion object {

        fun newInstance() = AboutDialogFragment()
    }
}
