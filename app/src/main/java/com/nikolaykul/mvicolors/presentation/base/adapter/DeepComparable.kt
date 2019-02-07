package com.nikolaykul.mvicolors.presentation.base.adapter

interface DeepComparable<in T> {
    infix fun isTheSameItem(other: T): Boolean

    infix fun isTheSameContent(other: T): Boolean
}