package com.example.trashdetector.ui.result

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.trashdetector.R
import com.example.trashdetector.theme.DarkModeInterface
import com.example.trashdetector.theme.DarkModeUtil
import kotlinx.android.synthetic.main.result_dialog_fragment.*

class ResultDialogFragment
private constructor(
    private val image: Bitmap,
    private val type: String
) : DialogFragment(), DarkModeInterface {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.result_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (DarkModeUtil.isDarkMode) enableDarkMode() else disableDarkMode()
        imageOutput.setImageBitmap(image)
        imageFullScreen.setImageBitmap(image)
        imageOutput.rotation = 90f
        imageFullScreen.rotation = 90f
        textResult.text = type
        setEvents()
    }

    override fun enableDarkMode() {
        layoutRootResult.background = context?.getDrawable(R.drawable.bg_rounded_black)
        buttonDetail.background = context?.getDrawable(R.drawable.bg_ripple_black)
        buttonCancel.background = context?.getDrawable(R.drawable.bg_ripple_black)
    }

    override fun disableDarkMode() {
        layoutRootResult.background = context?.getDrawable(R.drawable.bg_rounded)
        buttonDetail.background = context?.getDrawable(R.drawable.bg_ripple_white)
        buttonCancel.background = context?.getDrawable(R.drawable.bg_ripple_white)
    }

    private fun setEvents() {
        buttonCancel.setOnClickListener { dialog?.cancel() }
        buttonDetail.setOnClickListener {
            Toast.makeText(
                context,
                "Comming soon!",
                Toast.LENGTH_SHORT
            ).show()
        }
        imageOutput.setOnClickListener {
            cardFullScreen.visibility = View.VISIBLE
        }
        imageFullScreen.setOnClickListener {
            cardFullScreen.visibility = View.GONE
        }
    }

    companion object {

        fun newInstance(image: Bitmap, type: String) = ResultDialogFragment(image, type)
    }

}
