package com.nikolaykul.mvicolors.presentation.color.list.binding

import com.nikolaykul.mvicolors.presentation.color.list.adapter.ColorListItem

data class ColorListViewModel(
    val isLoading: Boolean,
    val items: List<ColorListItem>?
)