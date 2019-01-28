package com.nikolaykul.shortvids

import android.app.Activity
import android.app.Application
import com.nikolaykul.shortvids.di.application.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class ShortVidsApp : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        initDagger()
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    private fun initDagger() {
        DaggerAppComponent.create().inject(this)
    }
}