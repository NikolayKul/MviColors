package com.nikolaykul.shortvids.presentation.details

import com.nikolaykul.shortvids.R
import com.nikolaykul.shortvids.presentation.base.BaseFragment

class DetailsFragment : BaseFragment() {
    override val layoutId = R.layout.fragment_details

    companion object {
        fun newInstance(): DetailsFragment = DetailsFragment()
    }
}