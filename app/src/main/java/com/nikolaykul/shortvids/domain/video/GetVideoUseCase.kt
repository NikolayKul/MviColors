package com.nikolaykul.shortvids.domain.video

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GetVideoUseCase @Inject constructor() {

    fun getVideo(): Single<List<VideoItem>> =
        Single.timer(2500L, TimeUnit.MILLISECONDS)
            .map { createVideos() }
            .subscribeOn(Schedulers.io())

    private fun createVideos(): List<VideoItem> =
        (0..100).map(::createSingleVideo)

    private fun createSingleVideo(i: Int) =
        VideoItem(
            title = "Title for $i",
            subTitle = "Some lorem ipsum for the $i item",
            videoPath = "Actually there's no any videoPath at the moment"
        )
}