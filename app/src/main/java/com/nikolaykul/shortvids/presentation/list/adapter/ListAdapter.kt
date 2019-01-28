package com.nikolaykul.shortvids.presentation.list.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nikolaykul.shortvids.R
import com.nikolaykul.shortvids.presentation.utils.inflate
import kotlinx.android.synthetic.main.fragment_list_item.view.*
import kotlin.random.Random

class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private val items: MutableList<ListItem> = mutableListOf()

    fun addItems(newItems: List<ListItem>) {
        val start = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.fragment_list_item, parent)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ListItem) {
            itemView.apply {
                tvTitle.text = item.title
                tvSubTitle.text = item.subTitle
                videoLayer.setBackgroundColor(getRandomColor())
            }
        }

        private fun getRandomColor(): Int =
            Random(System.currentTimeMillis()).run {
                Color.rgb(nextInt(256), nextInt(256), nextInt(256))
            }
    }
}