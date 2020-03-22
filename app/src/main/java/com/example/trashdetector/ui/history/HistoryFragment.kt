package com.example.trashdetector.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.trashdetector.R
import com.example.trashdetector.data.model.History
import com.example.trashdetector.theme.DarkModeInterface
import com.example.trashdetector.theme.DarkModeUtil
import com.example.trashdetector.ui.detail.DetailDialogFragment
import com.example.trashdetector.utils.AnimationUtils
import com.example.trashdetector.utils.ImageUtils
import com.example.trashdetector.utils.TimeUtils
import kotlinx.android.synthetic.main.result_dialog_fragment.*

class HistoryFragment private constructor(private val history: History) : Fragment(),
    DarkModeInterface {

    private var onHistoryCancelListener: OnHistoryCancelListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.result_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (DarkModeUtil.isDarkMode) enableDarkMode() else disableDarkMode()
        imageOutput.setImageBitmap(ImageUtils.getBitmap(history.image))
        imageFullScreen.setImageBitmap(ImageUtils.getBitmap(history.image))
        textStatus.text = getString(R.string.title_history)
        textResult.text = history.type
        textResultFull.text = history.type
        textPercent.text =
            String.format(
                getString(R.string.title_percent),
                history.percent.toString(),
                PERCENT_CHAR
            )
        textTimeStamp.text = TimeUtils.timeToString(history.time.toLong())
        setEvents()
    }

    override fun enableDarkMode() {
        textResultFull.background = context?.getDrawable(R.drawable.bg_rounded_dark)
        buttonDetail.background = context?.getDrawable(R.drawable.bg_ripple_dark)
        buttonCancel.background = context?.getDrawable(R.drawable.bg_ripple_dark)
        layoutRootResult.background = context?.getDrawable(R.drawable.bg_rounded_dark)
    }

    override fun disableDarkMode() {
        textResultFull.background = context?.getDrawable(R.drawable.bg_rounded)
        buttonDetail.background = context?.getDrawable(R.drawable.bg_ripple_white)
        buttonCancel.background = context?.getDrawable(R.drawable.bg_ripple_white)
        layoutRootResult.background = context?.getDrawable(R.drawable.bg_rounded)
    }

    fun setOnHistoryCancelListener(onHistoryCancelListener: OnHistoryCancelListener) {
        this.onHistoryCancelListener = onHistoryCancelListener
    }

    private fun setEvents() {
        buttonDetail.setOnClickListener {
            openDetail()
        }
        imageOutput.setOnClickListener {
            AnimationUtils.zoomImageFromThumb(cardOutput, cardFullScreen, layoutBound)
        }
        imageFullScreen.setOnClickListener {
            AnimationUtils.zoomOutView(cardOutput, cardFullScreen, layoutBound)
        }
        buttonCancel.setOnClickListener {
            onHistoryCancelListener?.onHistoryCancel()
        }
    }

    private fun openDetail() {
        val type1 = getString(R.string.title_trash_1)
        val type2 = getString(R.string.title_trash_2)
        val type3 = getString(R.string.title_trash_3)
        val image = ImageUtils.getBitmap(history.image)
        when (history.type) {
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
        private const val PERCENT_CHAR = "%"

        fun newInstance(history: History) =
            HistoryFragment(history)
    }
}