package com.nikolaykul.mvicolors.presentation.color.list

import com.nikolaykul.mvicolors.presentation.base.ViewState
import com.nikolaykul.mvicolors.presentation.color.list.adapter.ColorListItem

sealed class ColorListState : ViewState {
    object Loading : ColorListState()
    class AllItems(val items: List<ColorListItem>) : ColorListState()
    class ExtraBottomItems(val items: List<ColorListItem>) : ColorListState()
    class Error(val msg: String?) : ColorListState()
}
