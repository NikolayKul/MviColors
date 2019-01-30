package com.nikolaykul.shortvids.presentation.base

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

abstract class BaseViewModel<TState : ViewState>(initState: TState) : ViewModel() {
    private val stateRelay by lazy { BehaviorSubject.createDefault(initState) }
    private val disposables = CompositeDisposable()

    override fun onCleared() {
        disposables.clear()
    }

    fun observeState(): Observable<TState> = stateRelay
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe { onViewSubscribed() }
        .doOnDispose { onViewUnsubscribed() }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun nextState(reducer: (TState) -> TState) {
        stateRelay.value!!
            .let(reducer)
            .also(stateRelay::onNext)
    }

    protected open fun onViewSubscribed() {
        /* no-op */
    }

    protected open fun onViewUnsubscribed() {
        /* no-op */
    }

    protected fun <T> Single<T>.safeSubscribe(
        onError: (Throwable) -> Unit = Timber::e,
        onSuccess: (T) -> Unit
    ): Disposable =
        subscribe(onSuccess, onError)
            .also { disposables.add(it) }

    protected fun <T> Observable<T>.safeSubscribe(
        onComplete: () -> Unit = { /* no-op */ },
        onError: (Throwable) -> Unit = Timber::e,
        onNext: (T) -> Unit = { /* no-op */ }
    ): Disposable =
        subscribe(onNext, onError, onComplete)
            .also { disposables.add(it) }
}
