package com.nikolaykul.shortvids.presentation.video

import com.nikolaykul.shortvids.presentation.base.ViewState
import com.nikolaykul.shortvids.presentation.video.adapter.VideoListItem

data class VideoListState(
    val items: List<VideoListItem>? = null,
    val isLoading: Boolean? = null,
    val errorMsg: String? = null
) : ViewState