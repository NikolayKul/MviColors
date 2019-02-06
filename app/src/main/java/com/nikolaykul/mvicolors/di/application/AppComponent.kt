package com.nikolaykul.mvicolors.di.application

import com.nikolaykul.mvicolors.MviColorsApp
import com.nikolaykul.mvicolors.di.activity.ActivityInjectionModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ActivityInjectionModule::class])
interface AppComponent {

    fun inject(app: MviColorsApp)

    @Component.Builder
    interface Builder {
        fun build(): AppComponent
    }
}