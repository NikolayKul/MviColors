package com.nikolaykul.shortvids.domain.navigation

import timber.log.Timber
import javax.inject.Inject

class DummyRouter @Inject constructor() {
    fun navigateToAddVideo() {
        Timber.d("navigateToAddVideo")
    }

    fun navigateToVideoDetails(videoId: Long) {
        Timber.d("navigateToVideoDetails(videoId=$videoId)")
    }
}