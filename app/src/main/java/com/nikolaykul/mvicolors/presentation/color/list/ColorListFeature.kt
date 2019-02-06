package com.nikolaykul.mvicolors.presentation.color.list

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Bootstrapper
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.BaseFeature
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.nikolaykul.mvicolors.domain.color.ColorItem
import com.nikolaykul.mvicolors.domain.color.GetColorsUseCase
import com.nikolaykul.mvicolors.domain.navigation.DummyRouter
import com.nikolaykul.mvicolors.presentation.color.list.ColorListFeature.*
import com.nikolaykul.mvicolors.presentation.color.list.adapter.ColorListItem
import com.nikolaykul.mvicolors.presentation.utils.randomError
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ColorListFeature @Inject constructor(
    getColorsUseCase: GetColorsUseCase,
    router: DummyRouter
) : BaseFeature<Wish, Action, Effect, State, News>(
    initialState = State(),
    bootstrapper = BootstrapperImpl(),
    wishToAction = { Action.WishWrapper(it) },
    actor = ActorImpl(getColorsUseCase, router),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl()
) {
    data class State(
        val isLoading: Boolean = false,
        val allItems: List<ColorListItem>? = null,
        val currentFilter: String? = null
    )

    sealed class Action {
        data class WishWrapper(val wish: Wish) : Action()
        object LoadInitColors : Action()
    }

    sealed class Wish {
        data class LoadColors(val filter: String? = null) : Wish()
        object ClearColorFilter : Wish()
        object LoadMoreColors : Wish()
        data class NavigateToColorDetails(val item: ColorListItem) : Wish()
        object NavigateToAddNewColor : Wish()
    }

    sealed class Effect {
        object Loading : Effect()
        class AllItemsLoaded(val allItems: List<ColorListItem>, val filter: String?) : Effect()
        class Error(val msg: String) : Effect()
    }

    sealed class News {
        class Error(val msg: String) : News()
    }
}


private class BootstrapperImpl : Bootstrapper<Action> {
    override fun invoke(): Observable<Action> =
        Observable.just(Action.LoadInitColors as Action)
            .observeOn(AndroidSchedulers.mainThread())
}


private class ActorImpl(
    private val getColorsUseCase: GetColorsUseCase,
    private val router: DummyRouter
) : Actor<State, Action, Effect> {
    private val filterLoaderExecutor = FilterLoaderExecutor()

    override fun invoke(state: State, action: Action): Observable<out Effect> = when (action) {
        is Action.WishWrapper -> executeWish(state, action.wish)
        is Action.LoadInitColors -> doLoadColors(null)
    }

    private fun executeWish(state: State, wish: Wish): Observable<out Effect> = when (wish) {
        is Wish.LoadColors -> filterLoaderExecutor.execute(wish)
        is Wish.ClearColorFilter -> doLoadColors(null)
        is Wish.LoadMoreColors -> {
            if (state.isLoading) {
                Observable.empty()
            } else {
                doLoadColors(state.currentFilter)
            }
        }
        is Wish.NavigateToColorDetails -> {
            router.navigateToColorDetails(wish.item.id)
            Observable.empty()
        }
        is Wish.NavigateToAddNewColor -> {
            router.navigateToAddColor()
            Observable.empty()
        }
    }

    private fun doLoadColors(filter: String?): Observable<out Effect> =
        getColorsUseCase.getColors(filter)
            .map {
                val items = mapToViewItems(it)
                Effect.AllItemsLoaded(items, filter) as Effect
            }
            .toObservable()
            .randomError()
            .observeOn(AndroidSchedulers.mainThread())
            .startWith(Effect.Loading)
            .onErrorReturn { Effect.Error(it.localizedMessage) }

    // TODO: color
    private fun mapToViewItems(colors: List<ColorItem>) =
        colors.map { ColorListItem(it.id, it.title, it.subTitle, it.videoPath) }

    private inner class FilterLoaderExecutor {
        private val wishObserver: Relay<Wish.LoadColors> = PublishRelay.create()
        private val loadColorsObservable: Observable<Effect> = wishObserver
            .debounce(FILTER_THRESHOLD_MILLIS, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .switchMap { doLoadColors(it.filter) }
            .observeOn(AndroidSchedulers.mainThread())

        fun execute(wish: Wish.LoadColors): Observable<Effect> {
            wishObserver.accept(wish)
            return loadColorsObservable
        }
    }

    companion object {
        private const val FILTER_THRESHOLD_MILLIS = 200L
    }
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
