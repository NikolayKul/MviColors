package com.nikolaykul.mvicolors.presentation.utils

import io.reactivex.Single
import io.reactivex.disposables.Disposable

fun Disposable?.isActive() = this != null && !isDisposed

fun <T> Single<T>.randomError(): Single<T> =
    this.flatMap {
        if (System.currentTimeMillis() % 10 > 6) {
            Single.error(Throwable("Random error"))
        } else {
            Single.just(it)
        }
    }
