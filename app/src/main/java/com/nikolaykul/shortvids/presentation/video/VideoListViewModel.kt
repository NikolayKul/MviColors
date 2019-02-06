package com.nikolaykul.shortvids.presentation.video

import com.nikolaykul.shortvids.presentation.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class VideoListViewModel @Inject constructor(
    videoListStateMachine: VideoListStateMachine
) : BaseViewModel() {

    val actions: Observer<VideoListStateMachine.Action> = videoListStateMachine.input

    val state: Observable<VideoListStateMachine.State> =
        videoListStateMachine.state
            .observeOn(AndroidSchedulers.mainThread())
}