package com.nikolaykul.mvicolors.presentation.color.list.binding

import com.badoo.mvicore.android.AndroidBindings
import com.badoo.mvicore.binder.using
import com.nikolaykul.mvicolors.presentation.color.list.ColorListFeature
import com.nikolaykul.mvicolors.presentation.color.list.ColorListFeature.State
import com.nikolaykul.mvicolors.presentation.color.list.ColorListFeature.Wish
import com.nikolaykul.mvicolors.presentation.color.list.ColorListFragment
import javax.inject.Inject

class ColorListBinding @Inject constructor(
    view: ColorListFragment,
    private val feature: ColorListFeature
) : AndroidBindings<ColorListFragment>(view) {
    override fun setup(view: ColorListFragment) {
        binder.bind(feature to view.viewModelConsumer using ViewModelMapper)
        binder.bind(feature.news to view.newsConsumer)
        binder.bind(view.uiEvents to feature using UiEventsMapper)
    }
}

private object ViewModelMapper : (State) -> ColorListViewModel {
    override fun invoke(state: State) = ColorListViewModel(
        isLoading = state.isLoading,
        items = state.allItems
    )
}

private object UiEventsMapper : (ColorListUiEvent) -> Wish {
    override fun invoke(event: ColorListUiEvent): Wish = when (event) {
        is ColorListUiEvent.OnFilterChanged -> Wish.LoadColors(event.filter)
        is ColorListUiEvent.OnFilterCanceled -> Wish.ClearColorFilter
        is ColorListUiEvent.OnListEndReached -> Wish.LoadMoreColors
        is ColorListUiEvent.OnColorItemClicked -> Wish.NavigateToColorDetails(event.item)
        is ColorListUiEvent.OnAddNewColorClicked -> Wish.NavigateToAddNewColor
    }
}