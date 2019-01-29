package com.nikolaykul.shortvids.presentation.video.list

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.nikolaykul.shortvids.R
import com.nikolaykul.shortvids.presentation.base.BaseFragment
import com.nikolaykul.shortvids.presentation.utils.rv.decorations.VerticalMarginDecorator
import com.nikolaykul.shortvids.presentation.video.list.adapter.VideoListAdapter
import com.nikolaykul.shortvids.presentation.video.list.adapter.VideoListItem
import kotlinx.android.synthetic.main.fragment_video_list.*

class VideoListFragment : BaseFragment() {
    override val layoutId = R.layout.fragment_video_list

    private val adapter = VideoListAdapter()
    private val viewModel by viewModelDelegate<VideoListViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()

        viewModel.sayHi()

        populateList()
    }

    private fun initList() {
        rvVids.adapter = adapter
        rvVids.layoutManager = LinearLayoutManager(context)
        rvVids.addItemDecoration(VerticalMarginDecorator.withDimen(R.dimen.space_default))
    }

    private fun populateList() {
        fun createItem(i: Int) = VideoListItem(
            title = "Title for $i",
            subTitle = "Some lorem ipsum for the $i item",
            videoPath = "Actually there's no any videoPath at the moment"
        )

        val items = (0..100).map(::createItem)
        adapter.addItems(items)
    }

    companion object {
        fun newInstance(): VideoListFragment = VideoListFragment()
    }
}