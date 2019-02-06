package com.nikolaykul.mvicolors.domain.navigation

import timber.log.Timber
import javax.inject.Inject

class DummyRouter @Inject constructor() {
    fun navigateToAddColor() {
        Timber.d("navigateToAddColor")
    }

    fun navigateToColorDetails(colorId: Long) {
        Timber.d("navigateToColorDetails(colorId=$colorId)")
    }
}