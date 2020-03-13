package com.example.trashdetector.ui.pages

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.trashdetector.R
import com.example.trashdetector.ui.about.AboutDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.information_fragment.*
import kotlinx.android.synthetic.main.trash_item.view.*

class InformationFragment private constructor() : BottomSheetDialogFragment() {

    private lateinit var viewModel: InformationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
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
        setupData()
        setEvents()
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
    }

    private fun showComing() {
        Toast.makeText(context, "Comming soon!", Toast.LENGTH_SHORT).show()
    }

    companion object {

        private const val ABOUT_TAG = "About"

        fun newInstance() = InformationFragment()
    }
}
