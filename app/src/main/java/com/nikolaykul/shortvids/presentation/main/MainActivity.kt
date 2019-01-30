package com.nikolaykul.shortvids.presentation.main

import android.os.Bundle
import com.nikolaykul.shortvids.R
import com.nikolaykul.shortvids.presentation.base.BaseActivity
import com.nikolaykul.shortvids.presentation.video.VideoListFragment

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setListFragment()
    }

    private fun setListFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.activityContainer, VideoListFragment.newInstance())
            .commit()
    }
}
