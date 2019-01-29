package com.nikolaykul.shortvids.presentation.video.details

import com.nikolaykul.shortvids.R
import com.nikolaykul.shortvids.presentation.base.BaseFragment

class VideoDetailsFragment : BaseFragment() {
    override val layoutId = R.layout.fragment_video_details

    companion object {
        fun newInstance(): VideoDetailsFragment = VideoDetailsFragment()
    }
}