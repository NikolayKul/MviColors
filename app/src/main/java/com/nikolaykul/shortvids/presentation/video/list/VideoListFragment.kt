package com.nikolaykul.shortvids.presentation.video.list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.nikolaykul.shortvids.R
import com.nikolaykul.shortvids.presentation.base.BaseFragment
import com.nikolaykul.shortvids.presentation.utils.rv.decorations.VerticalMarginDecorator
import com.nikolaykul.shortvids.presentation.video.list.adapter.VideoListAdapter
import kotlinx.android.synthetic.main.fragment_video_list.*

class VideoListFragment : BaseFragment() {
    override val layoutId = R.layout.fragment_video_list

    private val adapter = VideoListAdapter()
    private val viewModel by viewModelDelegate<VideoListViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()

        viewModel.observeState()
            .safeSubscribe { handleState(it) }
    }

    private fun handleState(state: VideoListState) {
        state.errorMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        state.isLoading?.let {
            Toast.makeText(context, "IsLoading = $it", Toast.LENGTH_SHORT).show()
        }

        state.items?.let {
            adapter.addItems(it)
        }
    }

    private fun initList() {
        rvVids.adapter = adapter
        rvVids.layoutManager = LinearLayoutManager(context)
        rvVids.addItemDecoration(VerticalMarginDecorator.withDimen(R.dimen.space_default))
    }

    companion object {
        fun newInstance(): VideoListFragment = VideoListFragment()
    }
}