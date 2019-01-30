package com.nikolaykul.shortvids.presentation.video.list.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nikolaykul.shortvids.R
import com.nikolaykul.shortvids.presentation.utils.inflate
import kotlinx.android.synthetic.main.fragment_video_list_item.view.*
import kotlin.random.Random

class VideoListAdapter(
    private val listener: Listener
) : RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {
    private val items: MutableList<VideoListItem> = mutableListOf()

    fun setItems(newItems: List<VideoListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addItems(newItems: List<VideoListItem>) {
        val start = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.fragment_video_list_item, parent)
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

        fun bind(item: VideoListItem) {
            itemView.apply {
                tvTitle.text = item.title
                tvSubTitle.text = item.subTitle
                videoLayer.setBackgroundColor(getRandomColor())

                setOnClickListener { listener.onItemClicked(item) }
            }
        }

        private fun getRandomColor(): Int =
            Random(System.currentTimeMillis()).run {
                Color.rgb(nextInt(256), nextInt(256), nextInt(256))
            }
    }

    interface Listener {
        fun onItemClicked(item: VideoListItem)
    }
}