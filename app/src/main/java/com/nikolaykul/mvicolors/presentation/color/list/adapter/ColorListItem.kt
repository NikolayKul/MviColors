package com.nikolaykul.mvicolors.presentation.color.list.adapter

import com.nikolaykul.mvicolors.presentation.base.adapter.DeepComparable

data class ColorListItem(
    val id: Long,
    val title: String,
    val subTitle: String,
    val color: Int
) : DeepComparable<ColorListItem> {

    override fun isTheSameItem(other: ColorListItem): Boolean =
        id == other.id

    override fun isTheSameContent(other: ColorListItem): Boolean =
        this == other
}