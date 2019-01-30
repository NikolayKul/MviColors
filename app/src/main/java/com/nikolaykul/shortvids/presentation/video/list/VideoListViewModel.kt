package com.nikolaykul.shortvids.presentation.video.list

import com.nikolaykul.shortvids.domain.video.GetVideoUseCase
import com.nikolaykul.shortvids.domain.video.VideoItem
import com.nikolaykul.shortvids.presentation.base.BaseViewModel
import com.nikolaykul.shortvids.presentation.video.list.adapter.VideoListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val FILTER_THRESHOLD_MILLIS = 200L

class VideoListViewModel @Inject constructor(
    private val getVideoUseCase: GetVideoUseCase
) : BaseViewModel<VideoListState>(
    initState = VideoListState()
) {
    private var filterSubject = PublishSubject.create<String>()

    override fun onViewSubscribed() {
        loadInitVideos()
        observeFilter()
    }

    fun onFilterChanged(newFilter: String) {
        filterSubject.onNext(newFilter)
    }

    fun onFilterCancelled() {
        filterSubject.onNext("")
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

    private fun observeFilter() {
        filterSubject
            .debounce(FILTER_THRESHOLD_MILLIS, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .switchMap {
                getVideoUseCase.getVideo(it)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { nextState { VideoListState(isLoading = true) } }
                    .doOnError(this::onLoadingError)
                    .doOnSuccess(this::onLoadingComplete)
                    .toObservable()
            }
            .retry()
            .safeSubscribe()
    }

    private fun loadInitVideos() {
        getVideoUseCase.getVideo()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { nextState { VideoListState(isLoading = true) } }
            .safeSubscribe(
                onSuccess = ::onLoadingComplete,
                onError = ::onLoadingError
            )
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