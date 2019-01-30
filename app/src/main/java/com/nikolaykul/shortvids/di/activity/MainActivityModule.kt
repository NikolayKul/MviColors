package com.nikolaykul.shortvids.di.activity

import com.nikolaykul.shortvids.di.fragment.PerFragment
import com.nikolaykul.shortvids.di.fragment.VideoListFragmentModule
import com.nikolaykul.shortvids.presentation.video.VideoListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MainActivityModule {

    @PerFragment
    @ContributesAndroidInjector(modules = [VideoListFragmentModule::class])
    fun videoListFragment(): VideoListFragment

}