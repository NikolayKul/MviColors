package com.nikolaykul.shortvids.presentation.video.list

import android.util.Log
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class VideoListViewModel @Inject constructor() : ViewModel() {

    fun sayHi() {
        Log.d("ViewModel", "hi!")
    }
}