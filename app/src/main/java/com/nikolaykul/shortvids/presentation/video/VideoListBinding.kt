package com.nikolaykul.shortvids.presentation.video

import com.badoo.mvicore.android.AndroidBindings
import javax.inject.Inject

class VideoListBinding @Inject constructor(
    view: VideoListFragment,
    private val feature: VideoListFeature
) : AndroidBindings<VideoListFragment>(view) {
    override fun setup(view: VideoListFragment) {
        binder.bind(feature to view.stateConsumer)
        binder.bind(feature.news to view.newsConsumer)
        binder.bind(view.wishProvider to feature)
    }
}