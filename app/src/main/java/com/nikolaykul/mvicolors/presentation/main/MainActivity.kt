package com.nikolaykul.mvicolors.presentation.main

import android.os.Bundle
import com.nikolaykul.mvicolors.R
import com.nikolaykul.mvicolors.presentation.base.BaseActivity
import com.nikolaykul.mvicolors.presentation.color.list.ColorListFragment

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setListFragment()
    }

    private fun setListFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.activityContainer, ColorListFragment.newInstance())
            .commit()
    }
}
