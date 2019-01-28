package com.nikolaykul.shortvids.presentation.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun View.inflate(
    resource: Int,
    root: ViewGroup? = null,
    attachToRoot: Boolean = false
): View = LayoutInflater.from(context).inflate(resource, root, attachToRoot)
