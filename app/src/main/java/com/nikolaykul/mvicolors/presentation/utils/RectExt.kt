package com.nikolaykul.mvicolors.presentation.utils

import android.graphics.Rect

fun Rect.update(
    left: Int = this.left,
    top: Int = this.top,
    right: Int = this.right,
    bottom: Int = this.bottom
) {
    set(left, top, right, bottom)
}
