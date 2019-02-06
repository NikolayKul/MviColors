package com.nikolaykul.mvicolors.di.activity

import com.nikolaykul.mvicolors.presentation.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@Module(includes = [AndroidSupportInjectionModule::class])
interface ActivityInjectionModule {
    @PerActivity
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    fun mainActivity(): MainActivity
}