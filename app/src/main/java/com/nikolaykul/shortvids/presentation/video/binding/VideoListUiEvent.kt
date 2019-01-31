package com.nikolaykul.shortvids.presentation.video.binding

import com.nikolaykul.shortvids.presentation.video.adapter.VideoListItem

sealed class VideoListUiEvent {
    class OnFilterChanged(val filter: String? = null) : VideoListUiEvent()
    object OnFilterCanceled : VideoListUiEvent()
    object OnListEndReached : VideoListUiEvent()
    class OnVideoItemClicked(val item: VideoListItem) : VideoListUiEvent()
    object OnAddNewVideoClicked : VideoListUiEvent()
}