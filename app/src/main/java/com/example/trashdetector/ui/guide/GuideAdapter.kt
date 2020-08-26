package com.example.trashdetector.ui.guide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trashdetector.R
import com.example.trashdetector.data.model.Guider

class GuideAdapter : RecyclerView.Adapter<GuideAdapter.GuideViewHolder>() {

    var guideList = emptyList<Guider>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.guide_item, parent, false)
        return GuideViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        holder.bindData(guideList[position])
    }

    override fun getItemCount() = guideList.size

    class GuideViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindData(guider: Guider) {

        }
    }

}