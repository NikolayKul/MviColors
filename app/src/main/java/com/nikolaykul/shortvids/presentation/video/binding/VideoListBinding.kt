package com.nikolaykul.shortvids.presentation.video.binding

import com.badoo.mvicore.android.AndroidBindings
import com.badoo.mvicore.binder.using
import com.nikolaykul.shortvids.presentation.video.VideoListFeature
import com.nikolaykul.shortvids.presentation.video.VideoListFeature.Wish
import com.nikolaykul.shortvids.presentation.video.VideoListFragment
import javax.inject.Inject

class VideoListBinding @Inject constructor(
    view: VideoListFragment,
    private val feature: VideoListFeature
) : AndroidBindings<VideoListFragment>(view) {
    override fun setup(view: VideoListFragment) {
        binder.bind(feature to view.stateConsumer)
        binder.bind(feature.news to view.newsConsumer)
        binder.bind(view.uiEvents to feature using UiEventsMapper)
    }
}

private object UiEventsMapper : (VideoListUiEvent) -> VideoListFeature.Wish {
    override fun invoke(event: VideoListUiEvent): VideoListFeature.Wish = when (event) {
        is VideoListUiEvent.OnFilterChanged -> Wish.LoadVideo(event.filter)
        is VideoListUiEvent.OnFilterCanceled -> Wish.ClearVideoFilter
        is VideoListUiEvent.OnListEndReached -> Wish.LoadMoreVideo
        is VideoListUiEvent.OnVideoItemClicked -> Wish.NavigateToDetails(event.item)
        is VideoListUiEvent.OnAddNewVideoClicked -> Wish.NavigateToAddNewVideo
    }
}