package com.nikolaykul.mvicolors.presentation.utils.rv.decorations

import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView

class VerticalMarginDecorator private constructor(
    private var marginPx: Int? = null,
    @DimenRes private val marginRes: Int? = null
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildLayoutPosition(view)
        val lastPosition = parent.layoutManager!!.itemCount - 1
        val margin = if (position == 0 || position == lastPosition) 0 else getMargin(view) / 2
        outRect.update(top = margin, bottom = margin)
    }

    private fun getMargin(view: View): Int =
        when {
            marginPx != null -> marginPx!!
            marginRes != null -> view.resources.getDimensionPixelSize(marginRes)
                .also { marginPx = it }
            else -> throw IllegalStateException("Empty margin!")
        }

    private fun Rect.update(
        left: Int = this.left,
        top: Int = this.top,
        right: Int = this.right,
        bottom: Int = this.bottom
    ) {
        set(left, top, right, bottom)
    }


    companion object {
        fun withPixels(marginPx: Int) = VerticalMarginDecorator(marginPx = marginPx)
        fun withDimen(marginRes: Int) = VerticalMarginDecorator(marginRes = marginRes)
    }
}