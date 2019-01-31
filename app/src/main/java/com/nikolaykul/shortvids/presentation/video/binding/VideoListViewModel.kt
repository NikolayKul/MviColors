package com.nikolaykul.shortvids.presentation.video.binding

import com.nikolaykul.shortvids.presentation.video.adapter.VideoListItem

data class VideoListViewModel(
    val isLoading: Boolean,
    val items: List<VideoListItem>?
)