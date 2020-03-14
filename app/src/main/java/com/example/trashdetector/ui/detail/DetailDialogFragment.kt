package com.example.trashdetector.ui.detail

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.trashdetector.R
import kotlinx.android.synthetic.main.detail_dialog_fragment.*

class DetailDialogFragment(private val type: Int, private var image: Bitmap?) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.detail_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayDetail()
        setEvents()
        displayCancelButton()
    }

    private fun displayCancelButton() {
        Handler().postDelayed({ cardCancel.visibility = View.VISIBLE }, 1000)
    }

    private fun displayDetail() = when (type) {
        1 -> displayTrash1()
        2 -> displayTrash2()
        3 -> displayTrash3()
        else -> displayTrash1()
    }

    private fun displayTrash1() {
        val title = getString(R.string.title_trash_1)
        val def = getString(R.string.title_def_1)
        val from = getString(R.string.title_from_1)
        val handle = getString(R.string.title_do_1)
        val example = getString(R.string.title_ex_1)
        val bg = context?.getDrawable(R.drawable.bg_gradient_orange)
        if (image != null) {
            imageTrash.setImageBitmap(image)
            imageTrash.rotation = 90f
        } else {
            val image = context?.getDrawable(R.drawable.vo_co)
            imageTrash.setImageDrawable(image)
        }
        textTitle.text = title
        textDef1.text = def
        textFrom1.text = from
        textDo1.text = handle
        textExample1.text = example
        layoutDetail.background = bg
    }

    private fun displayTrash2() {
        val title = getString(R.string.title_trash_2)
        val def = getString(R.string.title_def_2)
        val from = getString(R.string.title_from_2)
        val handle = getString(R.string.title_do_2)
        val example = getString(R.string.title_ex_2)
        val bg = context?.getDrawable(R.drawable.bg_gradient_green)
        if (image != null) {
            imageTrash.setImageBitmap(image)
            imageTrash.rotation = 90f
        } else {
            val image = context?.getDrawable(R.drawable.huu_co)
            imageTrash.setImageDrawable(image)
        }
        textTitle.text = title
        textDef1.text = def
        textFrom1.text = from
        textDo1.text = handle
        textExample1.text = example
        layoutDetail.background = bg
    }

    private fun displayTrash3() {
        val title = getString(R.string.title_trash_3)
        val def = getString(R.string.title_def_3)
        val from = getString(R.string.title_from_3)
        val handle = getString(R.string.title_do_3)
        val example = getString(R.string.title_ex_3)
        val bg = context?.getDrawable(R.drawable.bg_gradient_purple)
        if (image != null) {
            imageTrash.setImageBitmap(image)
            imageTrash.rotation = 90f
        } else {
            val image = context?.getDrawable(R.drawable.tai_che)
            imageTrash.setImageDrawable(image)
        }
        textTitle.text = title
        textDef1.text = def
        textFrom1.text = from
        textDo1.text = handle
        textExample1.text = example
        layoutDetail.background = bg
    }

    private fun setEvents() {
        buttonCancel.setOnClickListener { dialog?.cancel() }
    }

}
