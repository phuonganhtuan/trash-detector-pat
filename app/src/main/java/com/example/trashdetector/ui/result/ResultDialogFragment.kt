package com.example.trashdetector.ui.result

import android.graphics.Bitmap
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
import com.example.trashdetector.ui.detail.DetailDialogFragment
import com.example.trashdetector.ui.main.OnDialogActionsListener
import kotlinx.android.synthetic.main.result_dialog_fragment.*

class ResultDialogFragment
private constructor(
    private val image: Bitmap,
    private val type: String
) : DialogFragment(), DarkModeInterface {

    private var onDialogActionsListener: OnDialogActionsListener? = null

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
        textResultFull.text = type
        setEvents()
    }

    override fun enableDarkMode() {
        layoutRootResult.background = context?.getDrawable(R.drawable.bg_rounded_black)
        textResultFull.background = context?.getDrawable(R.drawable.bg_rounded_black)
        buttonDetail.background = context?.getDrawable(R.drawable.bg_ripple_black)
        buttonCancel.background = context?.getDrawable(R.drawable.bg_ripple_black)
    }

    override fun disableDarkMode() {
        layoutRootResult.background = context?.getDrawable(R.drawable.bg_rounded)
        textResultFull.background = context?.getDrawable(R.drawable.bg_rounded)
        buttonDetail.background = context?.getDrawable(R.drawable.bg_ripple_white)
        buttonCancel.background = context?.getDrawable(R.drawable.bg_ripple_white)
    }

    override fun onDestroy() {
        onDialogActionsListener?.onDialogCanceled()
        super.onDestroy()
    }

    fun setOnDialogCancelListener(onDialogActionsListener: OnDialogActionsListener) {
        this.onDialogActionsListener = onDialogActionsListener
    }

    private fun setEvents() {
        buttonCancel.setOnClickListener { dialog?.cancel() }
        buttonDetail.setOnClickListener {
            openDetail()
        }
        imageOutput.setOnClickListener {
            cardFullScreen.visibility = View.VISIBLE
        }
        imageFullScreen.setOnClickListener {
            cardFullScreen.visibility = View.GONE
        }
    }

    private fun openDetail() {
        val type1 = getString(R.string.title_trash_1)
        val type2 = getString(R.string.title_trash_2)
        val type3 = getString(R.string.title_trash_3)
        when (type) {
            type1 -> DetailDialogFragment(1, image).show(
                activity!!.supportFragmentManager,
                DETAIL_TAG
            )
            type2 -> DetailDialogFragment(2, image).show(
                activity!!.supportFragmentManager,
                DETAIL_TAG
            )
            type3 -> DetailDialogFragment(3, image).show(
                activity!!.supportFragmentManager,
                DETAIL_TAG
            )
            else -> DetailDialogFragment(1, image).show(
                activity!!.supportFragmentManager,
                DETAIL_TAG
            )
        }
    }

    companion object {

        private const val DETAIL_TAG = "Detail"

        fun newInstance(image: Bitmap, type: String) = ResultDialogFragment(image, type)
    }

}
