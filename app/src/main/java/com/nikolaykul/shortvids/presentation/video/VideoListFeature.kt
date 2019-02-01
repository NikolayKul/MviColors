package com.nikolaykul.shortvids.presentation.video

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Bootstrapper
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.BaseFeature
import com.nikolaykul.shortvids.domain.navigation.DummyRouter
import com.nikolaykul.shortvids.domain.video.GetVideoUseCase
import com.nikolaykul.shortvids.domain.video.VideoItem
import com.nikolaykul.shortvids.presentation.video.VideoListFeature.*
import com.nikolaykul.shortvids.presentation.video.adapter.VideoListItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class VideoListFeature @Inject constructor(
    getVideoUseCase: GetVideoUseCase,
    router: DummyRouter
) : BaseFeature<Wish, Action, Effect, State, News>(
    initialState = State(),
    bootstrapper = BootstrapperImpl(),
    wishToAction = { Action.WishWrapper(it) },
    actor = ActorImpl(getVideoUseCase, router),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl()
) {
    data class State(
        val isLoading: Boolean = false,
        val allItems: List<VideoListItem>? = null,
        val currentFilter: String? = null
    )

    sealed class Action {
        class WishWrapper(val wish: Wish) : Action()
        object LoadInitVideo : Action()
    }

    sealed class Wish {
        class LoadVideo(val filter: String? = null) : Wish()
        object ClearVideoFilter : Wish()
        object LoadMoreVideo : Wish()
        class NavigateToDetails(val item: VideoListItem) : Wish()
        object NavigateToAddNewVideo : Wish()
    }

    sealed class Effect {
        object Loading : Effect()
        class AllItemsLoaded(val allItems: List<VideoListItem>, val filter: String?) : Effect()
        class Error(val msg: String) : Effect()
    }

    sealed class News {
        class Error(val msg: String) : News()
    }
}


private class BootstrapperImpl : Bootstrapper<Action> {
    override fun invoke(): Observable<Action> =
        Observable.just(Action.LoadInitVideo as Action)
            .observeOn(AndroidSchedulers.mainThread())
}


private class ActorImpl(
    private val getVideoUseCase: GetVideoUseCase,
    private val router: DummyRouter
) : Actor<State, Action, Effect> {

    override fun invoke(state: State, action: Action): Observable<out Effect> = when (action) {
        is Action.WishWrapper -> executeWish(state, action.wish)
        is Action.LoadInitVideo -> loadVideos(null)
    }

    private fun executeWish(state: State, wish: Wish): Observable<out Effect> = when (wish) {
        is Wish.LoadVideo -> {
            if (state.isLoading) {
                Observable.empty()
            } else {
                loadVideos(wish.filter)
            }
        }
        is Wish.ClearVideoFilter -> loadVideos(null)
        is Wish.LoadMoreVideo -> {
            if (state.isLoading) {
                Observable.empty()
            } else {
                loadVideos(state.currentFilter)
            }
        }
        is Wish.NavigateToDetails -> {
            router.navigateToVideoDetails(wish.item.id)
            Observable.empty()
        }
        is Wish.NavigateToAddNewVideo -> {
            router.navigateToAddVideo()
            Observable.empty()
        }
    }

    private fun loadVideos(filter: String?): Observable<out Effect> =
        getVideoUseCase.getVideo(filter)
            .map {
                val items = mapToViewItems(it)
                Effect.AllItemsLoaded(items, filter) as Effect
            }
            .flatMapObservable {
                if (System.currentTimeMillis() % 2 > 0) {
                    Observable.just(it)
                } else {
                    Observable.error(Throwable("Random error"))
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .startWith(Effect.Loading)
            .onErrorReturn { Effect.Error(it.localizedMessage) }

    private fun mapToViewItems(videos: List<VideoItem>) =
        videos.map { VideoListItem(it.id, it.title, it.subTitle, it.videoPath) }
}


private class ReducerImpl : Reducer<State, Effect> {
    override fun invoke(state: State, effect: Effect): State = when (effect) {
        is Effect.Loading -> state.copy(isLoading = true)
        is Effect.AllItemsLoaded -> state.copy(
            isLoading = false,
            allItems = effect.allItems,
            currentFilter = effect.filter
        )
        else -> state.copy(isLoading = false)
    }
}


private class NewsPublisherImpl : NewsPublisher<Action, Effect, State, News> {
    override fun invoke(action: Action, effect: Effect, state: State): News? = when (effect) {
        is Effect.Error -> News.Error(effect.msg)
        else -> null
    }
}
