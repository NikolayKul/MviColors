package com.nikolaykul.shortvids.presentation.base

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {
    private val disposables = CompositeDisposable()

    override fun onCleared() {
        disposables.clear()
    }

    protected fun <T> Observable<T>.safeSubscribe(
        onComplete: () -> Unit = { /* no-op */ },
        onError: (Throwable) -> Unit = Timber::e,
        onNext: (T) -> Unit = { /* no-op */ }
    ): Disposable =
        subscribe(onNext, onError, onComplete)
            .also { disposables.add(it) }
}
