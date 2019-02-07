package com.nikolaykul.mvicolors.presentation.base.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

abstract class DiffAdapter<Item : DeepComparable<Item>, VH : RecyclerView.ViewHolder> :
    RecyclerView.Adapter<VH>() {

    var items: List<Item> by Delegates.observable(emptyList(), ::updateDispatcher)

    override fun getItemCount(): Int = items.size

    private fun updateDispatcher(
        prop: KProperty<*>,
        old: List<Item>,
        new: List<Item>
    ) {
        val cb = DiffCallback(old, new)
        DiffUtil.calculateDiff(cb)
            .dispatchUpdatesTo(this)
    }
}


private class DiffCallback<T : DeepComparable<T>>(
    private val oldList: List<T>,
    private val newList: List<T>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] isTheSameItem newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] isTheSameContent newList[newItemPosition]
}