package com.example.trashdetector.base.diffutil

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    protected var itemData: T? = null

    init {
        itemView.setOnClickListener {
            itemData?.let { onHandleItemClick(it) }
        }
    }

    abstract fun onBindData(itemData: T)
    abstract fun onHandleItemClick(mainItem: T)

}
