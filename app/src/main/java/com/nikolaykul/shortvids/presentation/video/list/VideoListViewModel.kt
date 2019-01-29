package com.nikolaykul.shortvids.presentation.video.list

import com.nikolaykul.shortvids.domain.video.GetVideoUseCase
import com.nikolaykul.shortvids.domain.video.VideoItem
import com.nikolaykul.shortvids.presentation.base.BaseViewModel
import com.nikolaykul.shortvids.presentation.video.list.adapter.VideoListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import javax.inject.Inject

class VideoListViewModel @Inject constructor(
    private val getVideoUseCase: GetVideoUseCase
) : BaseViewModel<VideoListState>(
    initState = VideoListState()
) {

    override fun onViewSubscribed() {
        getVideoUseCase.getVideo()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { nextState { VideoListState(isLoading = true) } }
            .safeSubscribe(
                onSuccess = ::onLoadingComplete,
                onError = ::onLoadingError
            )
    }

    fun onFilterChanged(newFilter: String) {
        Timber.d("onFilterChanged($newFilter)")
    }

    fun onFilterCancelled() {
        Timber.d("onFilterCancelled")
    }

    fun onListEndReached() {
        Timber.d("onListEndReached")
    }

    fun onVideoItemClicked(item: VideoListItem) {
        Timber.d("onVideoItemClicked($item)")
    }

    fun onAddNewVideoClicked() {
        Timber.d("onAddNewVideoClicked")
    }

    private fun onLoadingComplete(videos: List<VideoItem>) {
        val items = videos.map { VideoListItem(it.title, it.subTitle, it.videoPath) }
        nextState {
            VideoListState(
                items = items,
                isLoading = false
            )
        }
    }

    private fun onLoadingError(t: Throwable?) {
        nextState {
            VideoListState(
                isLoading = false,
                errorMsg = t?.localizedMessage
            )
        }
    }
}