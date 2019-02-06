package com.nikolaykul.shortvids.presentation.utils

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

fun Disposable?.isActive() = this != null && !isDisposed

fun <T> Observable<T>.randomError(): Observable<T> =
    this.flatMap {
        if (System.currentTimeMillis() % 2 > 0) {
            Observable.just(it)
        } else {
            Observable.error(Throwable("Random error"))
        }
    }
