package com.nikolaykul.shortvids.presentation.main

import android.os.Bundle
import android.view.View
import com.nikolaykul.shortvids.R
import com.nikolaykul.shortvids.presentation.base.BaseFragment
import com.nikolaykul.shortvids.presentation.list.ListFragment

class MainFragment : BaseFragment() {
    override val layoutId = R.layout.fragment_main

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupList()
    }

    private fun setupList() {
        childFragmentManager.beginTransaction()
            .replace(R.id.container, ListFragment.newInstance())
            .commit()
    }
}