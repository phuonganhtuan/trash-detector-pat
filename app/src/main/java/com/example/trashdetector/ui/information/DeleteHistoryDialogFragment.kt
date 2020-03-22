package com.example.trashdetector.ui.information

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.trashdetector.R
import com.example.trashdetector.base.callbacks.OnDialogActionsListener
import com.example.trashdetector.theme.DarkModeInterface
import com.example.trashdetector.theme.DarkModeUtil
import kotlinx.android.synthetic.main.alert_dialog_fragment.*

class DeleteHistoryDialogFragment private constructor() : DialogFragment(), DarkModeInterface {

    private var onDialogActionsListener: OnDialogActionsListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.alert_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (DarkModeUtil.isDarkMode) enableDarkMode() else disableDarkMode()
        setEvents()
    }

    override fun disableDarkMode() {
        layoutAlertDialog.background = context?.getDrawable(R.drawable.bg_rounded)
        buttonCancel.background = context?.getDrawable(R.drawable.bg_ripple_white)
        buttonDelete.background = context?.getDrawable(R.drawable.bg_ripple_white)
    }

    override fun enableDarkMode() {
        layoutAlertDialog.background = context?.getDrawable(R.drawable.bg_rounded_dark)
        buttonCancel.background = context?.getDrawable(R.drawable.bg_ripple_dark)
        buttonDelete.background = context?.getDrawable(R.drawable.bg_ripple_dark)
    }

    private fun setOnDialogActionsListener(onDialogActionsListener: OnDialogActionsListener) {
        this.onDialogActionsListener = onDialogActionsListener
    }

    private fun setEvents() {
        buttonDelete.setOnClickListener {
            onDialogActionsListener?.onDeleteConfirmed()
            dialog?.cancel()
        }
        buttonCancel.setOnClickListener { dialog?.cancel() }
    }

    companion object {

        fun newInstance(onDialogActionsListener: OnDialogActionsListener) =
            DeleteHistoryDialogFragment().apply {
                setOnDialogActionsListener(onDialogActionsListener)
            }
    }
}
