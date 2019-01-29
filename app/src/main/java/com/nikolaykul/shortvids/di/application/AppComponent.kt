package com.nikolaykul.shortvids.di.application

import com.nikolaykul.shortvids.ShortVidsApp
import com.nikolaykul.shortvids.di.activity.ActivityInjectionModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ActivityInjectionModule::class,
        ViewModelCommonModule::class
    ]
)
interface AppComponent {

    fun inject(app: ShortVidsApp)

    @Component.Builder
    interface Builder {
        fun build(): AppComponent
    }
}