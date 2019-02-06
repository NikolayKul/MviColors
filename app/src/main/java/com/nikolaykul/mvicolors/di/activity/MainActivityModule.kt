package com.nikolaykul.mvicolors.di.activity

import com.nikolaykul.mvicolors.di.fragment.ColorListFragmentModule
import com.nikolaykul.mvicolors.di.fragment.PerFragment
import com.nikolaykul.mvicolors.presentation.color.list.ColorListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MainActivityModule {
    @PerFragment
    @ContributesAndroidInjector(modules = [ColorListFragmentModule::class])
    fun colorListFragment(): ColorListFragment
}