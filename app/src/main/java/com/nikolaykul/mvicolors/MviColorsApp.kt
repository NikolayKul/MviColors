package com.nikolaykul.mvicolors

import android.app.Activity
import android.app.Application
import com.nikolaykul.mvicolors.di.application.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

class MviColorsApp : Application(), HasActivityInjector {

    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        initTimber()
        initDagger()
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    private fun initTimber() {
        // add other Trees (e.g. Crashlytics)
        Timber.plant(Timber.DebugTree())
    }

    private fun initDagger() {
        DaggerAppComponent.create().inject(this)
    }
}