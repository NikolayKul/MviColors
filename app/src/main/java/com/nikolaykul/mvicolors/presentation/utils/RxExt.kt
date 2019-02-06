package com.nikolaykul.mvicolors.presentation.utils

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

fun Disposable?.isActive() = this != null && !isDisposed

fun <T> Observable<T>.randomError(): Observable<T> =
    this.flatMap {
        if (System.currentTimeMillis() % 10 > 6) {
            Observable.error(Throwable("Random error"))
        } else {
            Observable.just(it)
        }
    }