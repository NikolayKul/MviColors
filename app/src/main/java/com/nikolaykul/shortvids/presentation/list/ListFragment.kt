package com.nikolaykul.shortvids.presentation.list

import com.nikolaykul.shortvids.R
import com.nikolaykul.shortvids.presentation.base.BaseFragment

class ListFragment : BaseFragment() {
    override val layoutId = R.layout.fragment_list

    companion object {
        fun newInstance(): ListFragment = ListFragment()
    }
}