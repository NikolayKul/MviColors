package com.nikolaykul.mvicolors.presentation.color.list.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nikolaykul.mvicolors.R
import com.nikolaykul.mvicolors.presentation.utils.inflate
import kotlinx.android.synthetic.main.fragment_color_list_item.view.*
import kotlin.random.Random

class ColorListAdapter(
    private val listener: Listener
) : RecyclerView.Adapter<ColorListAdapter.ViewHolder>() {
    private val items: MutableList<ColorListItem> = mutableListOf()

    fun setItems(newItems: List<ColorListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addItems(newItems: List<ColorListItem>) {
        val start = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.fragment_color_list_item, parent)
        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(
        view: View,
        private val listener: Listener
    ) : RecyclerView.ViewHolder(view) {

        fun bind(item: ColorListItem) {
            itemView.apply {
                tvTitle.text = item.title
                tvSubTitle.text = item.subTitle
                rootView.setBackgroundColor(getRandomColor())

                setOnClickListener { listener.onItemClicked(item) }
            }
        }

        private fun getRandomColor(): Int =
            Random(System.currentTimeMillis()).run {
                Color.rgb(nextInt(256), nextInt(256), nextInt(256))
            }
    }

    interface Listener {
        fun onItemClicked(item: ColorListItem)
    }
}