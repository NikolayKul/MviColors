package com.nikolaykul.shortvids.presentation.video

import com.nikolaykul.shortvids.presentation.base.ViewState
import com.nikolaykul.shortvids.presentation.video.adapter.VideoListItem

sealed class VideoListState : ViewState {
    object Loading : VideoListState()
    class AllItems(val items: List<VideoListItem>) : VideoListState()
    class ExtraBottomItems(val items: List<VideoListItem>) : VideoListState()
    class Error(val msg: String?) : VideoListState()
}
