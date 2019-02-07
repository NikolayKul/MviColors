package com.nikolaykul.mvicolors.presentation.color.list

import android.graphics.Color
import com.freeletics.rxredux.Reducer
import com.freeletics.rxredux.SideEffect
import com.freeletics.rxredux.StateAccessor
import com.freeletics.rxredux.reduxStore
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.nikolaykul.mvicolors.domain.color.ColorItem
import com.nikolaykul.mvicolors.domain.color.GetColorsUseCase
import com.nikolaykul.mvicolors.domain.navigation.DummyRouter
import com.nikolaykul.mvicolors.presentation.color.list.ColorListStateMachine.Action
import com.nikolaykul.mvicolors.presentation.color.list.ColorListStateMachine.State
import com.nikolaykul.mvicolors.presentation.color.list.adapter.ColorListItem
import com.nikolaykul.mvicolors.presentation.utils.randomError
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random

class ColorListStateMachine @Inject constructor(
    getColorsUseCase: GetColorsUseCase,
    router: DummyRouter
) {
    private val sideEffectsProvider = SideEffectsProvider(getColorsUseCase, router)
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
        val items: List<ColorListItem> = emptyList(),
        val currentFilter: String? = null,
        val error: String? = null
    )

    sealed class Action {
        data class LoadColors(val filter: String? = null) : Action()
        object ClearColorsFilter : Action()
        object LoadMoreColors : Action()
        data class NavigateToDetails(val item: ColorListItem) : Action()
        object NavigateToAddNewColor : Action()

        object Loading : Action()
        class NewItemsLoaded(val items: List<ColorListItem>, val filter: String?) : Action()
        class ExtraItemsLoaded(val items: List<ColorListItem>) : Action()
        class Error(val msg: String) : Action()
    }
}


private class SideEffectsProvider(
    private val getColorsUseCase: GetColorsUseCase,
    private val router: DummyRouter
) {
    val sideEffects: List<SideEffect<State, Action>>
        get() = listOf(
            ::loadInitColors,
            ::loadColors,
            ::clearFilter,
            ::loadMoreColors,
            ::navigateToAddNewColors,
            ::navigateToDetails
        )

    private fun loadInitColors(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<out Action> =
        doLoadNewColors(null)

    private fun loadColors(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> =
        actions.ofType(Action.LoadColors::class.java)
            .debounce(FILTER_THRESHOLD_MILLIS, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .switchMap { doLoadNewColors(it.filter) }

    private fun clearFilter(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> =
        actions.ofType(Action.ClearColorsFilter::class.java)
            .flatMap { doLoadNewColors(state().currentFilter) }

    private fun loadMoreColors(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> =
        actions.ofType(Action.LoadMoreColors::class.java)
            .filter { !state().isLoading }
            .flatMap { doLoadExtraColors(state().currentFilter) }

    private fun navigateToAddNewColors(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> =
        actions.filter { it is Action.NavigateToAddNewColor }
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                router.navigateToAddColor()
                Observable.empty<Action>()
            }

    private fun navigateToDetails(
        actions: Observable<Action>,
        state: StateAccessor<State>
    ): Observable<Action> =
        actions.ofType(Action.NavigateToDetails::class.java)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                router.navigateToColorDetails(it.item.id)
                Observable.empty<Action>()
            }

    private fun doLoadExtraColors(filter: String?): Observable<out Action> =
        doLoadNewColors(filter)
            .map { action ->
                when (action) {
                    is Action.NewItemsLoaded -> Action.ExtraItemsLoaded(action.items)
                    else -> action
                }
            }

    private fun doLoadNewColors(filter: String?): Observable<out Action> =
        getColorsUseCase.getColors(filter)
            .map {
                val items = mapToViewItems(it)
                Action.NewItemsLoaded(items, filter) as Action
            }
            .toObservable()
            .randomError()
            .observeOn(AndroidSchedulers.mainThread())
            .startWith(Action.Loading)
            .onErrorReturn { Action.Error(it.localizedMessage) }

    private fun mapToViewItems(colors: List<ColorItem>): List<ColorListItem> =
        colors.map {
            val color = Random(it.id).run {
                Color.rgb(nextInt(256), nextInt(256), nextInt(256))
            }
            ColorListItem(it.id, it.title, it.subTitle, color)
        }

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
        is Action.NewItemsLoaded -> state.copy(
            isLoading = false,
            items = action.items,
            currentFilter = action.filter,
            error = null
        )
        is Action.ExtraItemsLoaded -> state.copy(
            isLoading = false,
            items = state.items + action.items,
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