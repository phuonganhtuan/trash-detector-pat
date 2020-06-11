package com.example.trashdetector.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    protected var itemData: T? = null
    abstract fun onBindData(itemData: T, position: Int)
}
