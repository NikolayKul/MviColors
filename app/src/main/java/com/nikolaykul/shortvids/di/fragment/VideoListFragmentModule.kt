package com.nikolaykul.shortvids.di.fragment

import androidx.lifecycle.ViewModel
import com.nikolaykul.shortvids.di.application.ViewModelKey
import com.nikolaykul.shortvids.presentation.video.list.VideoListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface VideoListFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(VideoListViewModel::class)
    fun viewModel(viewModel: VideoListViewModel): ViewModel

}