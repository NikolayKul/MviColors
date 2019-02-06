package com.nikolaykul.mvicolors.di.fragment

import androidx.lifecycle.ViewModel
import com.nikolaykul.mvicolors.di.application.ViewModelKey
import com.nikolaykul.mvicolors.presentation.color.list.ColorListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ColorListFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(ColorListViewModel::class)
    fun viewModel(viewModel: ColorListViewModel): ViewModel

}