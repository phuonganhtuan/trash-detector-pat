package com.example.trashdetector.ui.information

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trashdetector.R
import com.example.trashdetector.base.BaseAdapter
import com.example.trashdetector.base.diffutil.BaseViewHolder
import com.example.trashdetector.base.diffutil.HistoryDiffUtil
import com.example.trashdetector.data.model.History
import com.example.trashdetector.utils.ImageUtils
import com.example.trashdetector.utils.TimeUtils
import kotlinx.android.synthetic.main.history_item.view.*
import kotlinx.android.synthetic.main.trash_item.view.*

class HistoryAdapter(diffCallback: HistoryDiffUtil = HistoryDiffUtil()) :
    BaseAdapter<History, HistoryAdapter.HistoryViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.onBindData(getItem(position))
    }

    class HistoryViewHolder(historyView: View) :
        BaseViewHolder<History>(historyView) {

        override fun onBindData(itemData: History) {
            this.itemData = itemData
            itemView.apply {
                textType.text = itemData.type
                textTimeStamp.text = TimeUtils.timeToString(itemData.time.toLong())
                imageHistory.setImageBitmap(ImageUtils.getBitmap(itemData.image))
                imageHistory.rotation = 90f
            }
        }

        override fun onHandleItemClick(mainItem: History) {}
    }
}
