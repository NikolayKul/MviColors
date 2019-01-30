package com.nikolaykul.shortvids.presentation.video

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.widget.textChanges
import com.nikolaykul.shortvids.R
import com.nikolaykul.shortvids.presentation.base.BaseFragment
import com.nikolaykul.shortvids.presentation.utils.rv.decorations.VerticalMarginDecorator
import com.nikolaykul.shortvids.presentation.video.adapter.VideoListAdapter
import com.nikolaykul.shortvids.presentation.video.adapter.VideoListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_video_list.*
import kotlinx.android.synthetic.main.fragment_video_list_toolbar.*
import kotlinx.android.synthetic.main.fragment_video_loader.*
import timber.log.Timber

private const val LOAD_MORE_THRESHOLD = 5

class VideoListFragment : BaseFragment(), VideoListAdapter.Listener {

    override val layoutId = R.layout.fragment_video_list

    private lateinit var adapter: VideoListAdapter
    private val viewModel by viewModelDelegate<VideoListViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initListeners()

        viewModel.observeState()
            .safeSubscribe { handleState(it) }
    }

    override fun onItemClicked(item: VideoListItem) {
        viewModel.onVideoItemClicked(item)
    }

    private fun handleState(state: VideoListState) {
        Timber.d("NextState -> $state")

        vgLoader.isVisible = state is VideoListState.Loading
        when (state) {
            is VideoListState.AllItems -> {
                adapter.setItems(state.items)
            }
            is VideoListState.ExtraBottomItems -> {
                adapter.addItems(state.items)
            }
            is VideoListState.Error -> {
                Toast.makeText(context, state.msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initList() {
        adapter = VideoListAdapter(this)
        val layoutManager = LinearLayoutManager(context)

        rvVids.adapter = adapter
        rvVids.layoutManager = layoutManager
        rvVids.addItemDecoration(VerticalMarginDecorator.withDimen(R.dimen.space_default))
        rvVids.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                if (dy > 0 && adapter.itemCount - lastVisiblePosition < LOAD_MORE_THRESHOLD) {
                    viewModel.onListEndReached()
                }
            }
        })
    }

    private fun initListeners() {
        etFilterOptions.textChanges()
            .skipInitialValue()
            .map { it.trim().toString() }
            .observeOn(AndroidSchedulers.mainThread())
            .safeSubscribe { viewModel.onFilterChanged(it) }

        fab.setOnClickListener { viewModel.onAddNewVideoClicked() }

        btnClearFilter.setOnClickListener {
            etFilterOptions.text.clear()
            viewModel.onFilterCancelled()
        }

        btnApplyFilter.setOnClickListener {
            viewModel.onFilterChanged(etFilterOptions.text.toString())
        }
    }

    companion object {
        fun newInstance(): VideoListFragment = VideoListFragment()
    }
}