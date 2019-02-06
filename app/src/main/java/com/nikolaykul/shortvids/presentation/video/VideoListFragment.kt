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
import com.nikolaykul.shortvids.presentation.video.VideoListStateMachine.Action
import com.nikolaykul.shortvids.presentation.video.VideoListStateMachine.State
import com.nikolaykul.shortvids.presentation.video.adapter.VideoListAdapter
import com.nikolaykul.shortvids.presentation.video.adapter.VideoListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_video_list.*
import kotlinx.android.synthetic.main.fragment_video_list_toolbar.*
import kotlinx.android.synthetic.main.fragment_video_loader.*
import javax.inject.Inject

private const val LOAD_MORE_THRESHOLD = 5

class VideoListFragment : BaseFragment<State>(), VideoListAdapter.Listener {

    override val layoutId = R.layout.fragment_video_list

    @Inject lateinit var stateMachine: VideoListStateMachine
    private lateinit var adapter: VideoListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initListeners()

        stateMachine.state
            .observeOn(AndroidSchedulers.mainThread())
            .safeSubscribe { render(it) }
    }

    override fun render(state: State) {
        vgLoader.isVisible = state.isLoading
        state.allItems?.let { adapter.setItems(it) }
        state.error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    override fun onItemClicked(item: VideoListItem) {
        stateMachine.input.onNext(Action.NavigateToDetails(item))
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
                    stateMachine.input.onNext(Action.LoadMoreVideo)
                }
            }
        })
    }

    private fun initListeners() {
        etFilterOptions.textChanges()
            .map { it.trim().toString() }
            .map { Action.LoadVideo(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(stateMachine.input)

        fab.setOnClickListener { stateMachine.input.onNext(Action.NavigateToAddNewVideo) }

        btnClearFilter.setOnClickListener {
            etFilterOptions.text.clear()
            stateMachine.input.onNext(Action.ClearVideoFilter)
        }

        btnApplyFilter.setOnClickListener {
            val filter = etFilterOptions.text.toString()
            stateMachine.input.onNext(Action.LoadVideo(filter))
        }
    }

    companion object {
        fun newInstance(): VideoListFragment = VideoListFragment()
    }
}