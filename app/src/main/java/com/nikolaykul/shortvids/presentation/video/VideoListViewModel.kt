package com.nikolaykul.shortvids.presentation.video

import com.nikolaykul.shortvids.domain.video.GetVideoUseCase
import com.nikolaykul.shortvids.domain.video.VideoItem
import com.nikolaykul.shortvids.presentation.base.BaseViewModel
import com.nikolaykul.shortvids.presentation.utils.isActive
import com.nikolaykul.shortvids.presentation.video.adapter.VideoListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
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
    private var bottomItemsDisposable: Disposable? = null
    private var currentFilter: String? = null

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
        if (bottomItemsDisposable.isActive()) {
            return
        }

        bottomItemsDisposable = getVideoUseCase.getVideo(currentFilter)
            .map(this::mapToViewItems)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { nextState { VideoListState(isLoading = true) } }
            .safeSubscribe(
                onSuccess = { items ->
                    nextState { VideoListState(newBottomItems = items) }
                },
                onError = ::onLoadingError
            )
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
            .doOnNext { currentFilter = it }
            .switchMap {
                getVideoUseCase.getVideo(it)
                    .map(this::mapToViewItems)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { nextState { VideoListState(isLoading = true) } }
                    .doOnSuccess { items ->
                        nextState { VideoListState(allItems = items) }
                    }
                    .doOnError(this::onLoadingError)
                    .toObservable()
            }
            .retry()
            .safeSubscribe()
    }

    private fun loadInitVideos() {
        getVideoUseCase.getVideo()
            .map(this::mapToViewItems)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { nextState { VideoListState(isLoading = true) } }
            .safeSubscribe(
                onSuccess = { items ->
                    nextState { VideoListState(allItems = items) }
                },
                onError = ::onLoadingError
            )
    }

    private fun mapToViewItems(videos: List<VideoItem>) =
        videos.map { VideoListItem(it.title, it.subTitle, it.videoPath) }

    private fun onLoadingError(t: Throwable?) {
        nextState { VideoListState(errorMsg = t?.localizedMessage) }
    }
}