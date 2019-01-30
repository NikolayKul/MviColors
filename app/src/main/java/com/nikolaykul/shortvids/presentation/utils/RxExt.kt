package com.nikolaykul.shortvids.presentation.utils

import io.reactivex.disposables.Disposable

fun Disposable?.isActive() = this != null && !isDisposed
