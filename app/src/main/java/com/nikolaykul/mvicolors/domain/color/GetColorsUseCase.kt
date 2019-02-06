package com.nikolaykul.mvicolors.domain.color

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GetColorsUseCase @Inject constructor() {

    fun getColors(filter: String? = null): Single<List<ColorItem>> =
        Single.timer(1500L, TimeUnit.MILLISECONDS)
            .map { createColors(filter) }
            .subscribeOn(Schedulers.io())

    private fun createColors(filter: String? = null): List<ColorItem> =
        (0..100).asSequence()
            .map(::createSingleColor)
            .filter { filter.isNullOrBlank() || filter.length >= it.id.length }
            .toList()

    private fun createSingleColor(i: Int) =
        ColorItem(
            id = "$i",
            title = "Title for $i",
            subTitle = "Some lorem ipsum for the $i item",
            videoPath = "Actually there's no any videoPath at the moment"
        )
}