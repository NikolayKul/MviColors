package com.nikolaykul.mvicolors.presentation.color.list.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nikolaykul.mvicolors.R
import com.nikolaykul.mvicolors.presentation.base.adapter.DiffAdapter
import com.nikolaykul.mvicolors.presentation.utils.inflate
import kotlinx.android.synthetic.main.fragment_color_list_item.view.*

class ColorListAdapter(
    private val listener: Listener
) : DiffAdapter<ColorListItem, ColorListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.fragment_color_list_item, parent)
        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(view: View, private val listener: Listener) : RecyclerView.ViewHolder(view) {
        fun bind(item: ColorListItem) {
            itemView.apply {
                tvTitle.text = item.title
                tvSubTitle.text = item.subTitle
                rootView.setBackgroundColor(item.color)

                setOnClickListener { listener.onItemClicked(item) }
            }
        }
    }

    interface Listener {
        fun onItemClicked(item: ColorListItem)
    }
}