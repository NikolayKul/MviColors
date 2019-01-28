package com.nikolaykul.shortvids.di.activity

import com.nikolaykul.shortvids.presentation.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@Module(includes = [AndroidSupportInjectionModule::class])
interface ActivityInjectionModule {

    @PerActivity
    @ContributesAndroidInjector
    fun mainActivity(): MainActivity

}