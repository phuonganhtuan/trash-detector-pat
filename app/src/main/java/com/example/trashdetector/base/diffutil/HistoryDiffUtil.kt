package com.example.trashdetector.base.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.example.trashdetector.data.model.History

class HistoryDiffUtil : DiffUtil.ItemCallback<History>() {

    override fun areItemsTheSame(oldItem: History, newItem: History) =
        oldItem.hisId == newItem.hisId

    override fun areContentsTheSame(oldItem: History, newItem: History) = oldItem == newItem
}
