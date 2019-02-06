package com.nikolaykul.shortvids.presentation.video

import com.freeletics.rxredux.Reducer
import com.freeletics.rxredux.SideEffect
import com.freeletics.rxredux.StateAccessor
import com.freeletics.rxredux.reduxStore
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.nikolaykul.shortvids.domain.navigation.DummyRouter
import com.nikolaykul.shortvids.domain.video.GetVideoUseCase
import com.nikolaykul.shortvids.domain.video.VideoItem
import com.nikolaykul.shortvids.presentation.utils.randomError
import com.nikolaykul.shortvids.presentation.video.VideoListStateMachine.Action
import com.nikolaykul.shortvids.presentation.video.VideoListStateMachine.State
import com.nikolaykul.shortvids.presentation.video.adapter.VideoListItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class VideoListStateMachine @Inject constructor(
    getVideoUseCase: GetVideoUseCase,
    router: DummyRouter
) {
    private val sideEffectsProvider = SideEffectsProvider(getVideoUseCase, router)
    val input: Relay<Action> = PublishRelay.create()
    val state: Observable<State> = createState()

    private fun createState(): Observable<State> =
        input.doOnNext { Timber.d("Next input: $it") }
            .reduxStore(
                initialState = State(),
                sideEffects = sideEffectsProvider.sideEffects,
                reducer = ReducerImpl()
            )
            .distinctUntilChanged()
            .doOnNext { Timber.d("Next state: $it") }

    data class State(
        val isLoading: Boolean = false,
        val allItems: List<VideoListItem>? = null,
        val currentFilter: String? = null,
        val error: String? = null
    )

    sealed class Action {
        data class LoadVideo(val filter: String? = null) : Action()
        object ClearVideoFilter : Action()
        object LoadMoreVideo : Action()
        data class NavigateToDetails(val item: VideoListItem) : Action()
        object NavigateToAddNewVideo : Action()

        object Loading : Action()
        class AllItemsLoaded(val allItems: List<VideoListItem>, val filter: String?) : Action()
        class Error(val msg: String) : Action()
    }
}


private class SideEffectsProvider(
    private val getVideoUseCase: GetVideoUseCase,
    private val router: DummyRouter
) {
    val sideEffects: List<SideEffect<State, Action>>
        get() = listOf(
            ::loadInitVideo,
            ::loadVideo,
            ::clearFilter,
            ::loadMoreVideo,
            ::navigateToAddNewVideo,
            ::navigateToDetails
        )

    private fun loadInitVideo(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<out Action> =
        doLoadVideos(null)

    private fun loadVideo(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> =
        actions.ofType(Action.LoadVideo::class.java)
            .debounce(FILTER_THRESHOLD_MILLIS, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .switchMap { doLoadVideos(it.filter) }

    private fun clearFilter(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> =
        actions.ofType(Action.ClearVideoFilter::class.java)
            .flatMap { doLoadVideos(state().currentFilter) }

    private fun loadMoreVideo(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> =
        actions.ofType(Action.LoadMoreVideo::class.java)
            .filter { !state().isLoading }
            .flatMap { doLoadVideos(state().currentFilter) }

    private fun navigateToAddNewVideo(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> =
        actions.filter { it is Action.NavigateToAddNewVideo }
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                router.navigateToAddVideo()
                Observable.empty<Action>()
            }

    private fun navigateToDetails(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> =
        actions.ofType(Action.NavigateToDetails::class.java)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                router.navigateToVideoDetails(it.item.id)
                Observable.empty<Action>()
            }

    private fun doLoadVideos(filter: String?): Observable<out Action> =
        getVideoUseCase.getVideo(filter)
            .map {
                val items = mapToViewItems(it)
                Action.AllItemsLoaded(items, filter) as Action
            }
            .toObservable()
            .randomError()
            .observeOn(AndroidSchedulers.mainThread())
            .startWith(Action.Loading)
            .onErrorReturn { Action.Error(it.localizedMessage) }

    private fun mapToViewItems(videos: List<VideoItem>) =
        videos.map { VideoListItem(it.id, it.title, it.subTitle, it.videoPath) }

    companion object {
        private const val FILTER_THRESHOLD_MILLIS = 200L
    }
}


private class ReducerImpl : Reducer<State, Action> {
    override fun invoke(state: State, action: Action): State = when (action) {
        is Action.Loading -> state.copy(
            isLoading = true,
            error = null
        )
        is Action.AllItemsLoaded -> state.copy(
            isLoading = false,
            allItems = action.allItems,
            currentFilter = action.filter,
            error = null
        )
        is Action.Error -> state.copy(
            isLoading = false,
            error = action.msg
        )
        else -> state.copy(
            isLoading = false,
            error = null
        )
    }
}