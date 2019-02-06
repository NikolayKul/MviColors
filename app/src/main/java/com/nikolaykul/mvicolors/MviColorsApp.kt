package com.nikolaykul.mvicolors

import android.app.Activity
import android.app.Application
import com.badoo.mvicore.consumer.middleware.LoggingMiddleware
import com.badoo.mvicore.consumer.middlewareconfig.MiddlewareConfiguration
import com.badoo.mvicore.consumer.middlewareconfig.Middlewares
import com.badoo.mvicore.consumer.middlewareconfig.WrappingCondition
import com.nikolaykul.mvicolors.di.application.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import javax.inject.Inject

class MviColorsApp : Application(), HasActivityInjector {

    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        initTimber()
        initDagger()
        initMiddleware()
        RxJavaPlugins.setErrorHandler { Timber.e(it) }
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    private fun initTimber() {
        // add other Trees (e.g. Crashlytics)
        Timber.plant(Timber.DebugTree())
    }

    private fun initDagger() {
        DaggerAppComponent.create().inject(this)
    }

    private fun initMiddleware() {
        Middlewares.configurations.add(
            MiddlewareConfiguration(
                condition = WrappingCondition.Always,
                factories = listOf(
                    { consumer -> LoggingMiddleware(consumer, { Timber.i(it) }) }
                )
            )
        )
    }
}