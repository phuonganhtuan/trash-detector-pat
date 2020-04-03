package com.example.trashdetector.ui.information

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trashdetector.R
import com.example.trashdetector.base.BaseAdapter
import com.example.trashdetector.base.BaseViewHolder
import com.example.trashdetector.base.callbacks.OnHistoryClickListener
import com.example.trashdetector.base.diffutil.HistoryDiffUtil
import com.example.trashdetector.data.model.History
import com.example.trashdetector.utils.ImageUtils
import com.example.trashdetector.utils.TimeUtils
import kotlinx.android.synthetic.main.history_bound_item.view.*
import kotlinx.android.synthetic.main.history_item.view.*

class HistoryAdapter(diffCallback: HistoryDiffUtil = HistoryDiffUtil()) :
    BaseAdapter<History, HistoryAdapter.HistoryViewHolder>(diffCallback) {

    var onHistoryClickListener: OnHistoryClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.history_bound_item, parent, false)
        return HistoryViewHolder(itemView, onHistoryClickListener)
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.onBindData(getItem(position), position)
    }

    class HistoryViewHolder(
        historyView: View,
        private val onHistoryClickListener: OnHistoryClickListener?
    ) :
        BaseViewHolder<History>(historyView) {

        private var currentPosition = 0

        init {
            itemView.cardHistory.setOnClickListener { onHandleItemClick() }
        }

        override fun onBindData(itemData: History, position: Int) {
            this.itemData = itemData
            itemView.layoutHistory.apply {
                textType.text = itemData.type
                textTimeStamp.text = TimeUtils.timeToString(itemData.time.toLong())
                imageHistory.setImageBitmap(ImageUtils.getBitmap(itemData.image))
            }
            currentPosition = position
        }

        private fun onHandleItemClick() {
            onHistoryClickListener?.onHistoryItemCLicked(currentPosition)
        }
    }
}
