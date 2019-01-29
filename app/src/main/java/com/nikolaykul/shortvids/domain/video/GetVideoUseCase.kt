package com.nikolaykul.shortvids.domain.video

import javax.inject.Inject

class GetVideoUseCase @Inject constructor() {

    fun getVideo(): List<VideoItem> = (0..100).map(::createDummyVideo)

    private fun createDummyVideo(i: Int) =
        VideoItem(
            title = "Title for $i",
            subTitle = "Some lorem ipsum for the $i item",
            videoPath = "Actually there's no any videoPath at the moment"
        )
}