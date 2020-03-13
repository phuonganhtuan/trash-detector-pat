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
import kotlinx.android.synthetic.main.result_dialog_fragment.*

class ResultDialogFragment
private constructor(
    private val image: Bitmap,
    private val type: String
) : DialogFragment() {

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
        imageOutput.setImageBitmap(image)
        imageOutput.rotation = 90f
        textResult.text = type
        setEvents()
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
    }

    companion object {

        fun newInstance(image: Bitmap, type: String) = ResultDialogFragment(image, type)
    }

}
