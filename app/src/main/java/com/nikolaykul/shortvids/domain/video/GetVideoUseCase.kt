package com.nikolaykul.shortvids.domain.video

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GetVideoUseCase @Inject constructor() {

    fun getVideo(filter: String? = null): Single<List<VideoItem>> =
        Single.timer(1500L, TimeUnit.MILLISECONDS)
            .map { createVideos(filter) }
            .subscribeOn(Schedulers.io())

    private fun createVideos(filter: String? = null): List<VideoItem> =
        (0..100L).asSequence()
            .map(::createSingleVideo)
            .filter { filter.isNullOrBlank() || filter.length >= it.id }    // lol
            .toList()

    private fun createSingleVideo(i: Long) =
        VideoItem(
            id = i,
            title = "Title for $i",
            subTitle = "Some lorem ipsum for the $i item"
        )
}