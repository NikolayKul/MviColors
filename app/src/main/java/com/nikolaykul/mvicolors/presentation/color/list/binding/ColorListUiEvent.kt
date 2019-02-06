package com.nikolaykul.mvicolors.presentation.color.list.binding

import com.nikolaykul.mvicolors.presentation.color.list.adapter.ColorListItem

sealed class ColorListUiEvent {
    class OnFilterChanged(val filter: String? = null) : ColorListUiEvent()
    object OnFilterCanceled : ColorListUiEvent()
    object OnListEndReached : ColorListUiEvent()
    class OnColorItemClicked(val item: ColorListItem) : ColorListUiEvent()
    object OnAddNewColorClicked : ColorListUiEvent()
}