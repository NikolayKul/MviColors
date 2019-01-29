package com.nikolaykul.shortvids.di.fragment

import com.nikolaykul.shortvids.presentation.video.details.VideoDetailsFragment
import com.nikolaykul.shortvids.presentation.video.list.VideoListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MainFragmentModule {

    @PerFragment
    @ContributesAndroidInjector(modules = [VideoListFragmentModule::class])
    fun videoListFragment(): VideoListFragment

    @PerFragment
    @ContributesAndroidInjector
    fun videoDetailsFragment(): VideoDetailsFragment

}