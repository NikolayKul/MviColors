package com.nikolaykul.shortvids.di.activity

import com.nikolaykul.shortvids.di.fragment.MainFragmentModule
import com.nikolaykul.shortvids.presentation.main.MainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MainActivityModule {
    @ContributesAndroidInjector(modules = [MainFragmentModule::class])
    fun mainFragment(): MainFragment
}