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
import com.nikolaykul.shortvids.presentation.video.VideoListFeature.*
import com.nikolaykul.shortvids.presentation.video.adapter.VideoListAdapter
import com.nikolaykul.shortvids.presentation.video.adapter.VideoListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_video_list.*
import kotlinx.android.synthetic.main.fragment_video_list_toolbar.*
import kotlinx.android.synthetic.main.fragment_video_loader.*
import javax.inject.Inject

private const val LOAD_MORE_THRESHOLD = 5

class VideoListFragment : BaseFragment(), VideoListAdapter.Listener {

    override val layoutId = R.layout.fragment_video_list

    @Inject lateinit var feature: VideoListFeature
    private lateinit var adapter: VideoListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initListeners()

        binder.bind(feature to stateConsumer())
        binder.bind(feature.news to newsConsumer())
    }

    override fun onItemClicked(item: VideoListItem) {
        feature.accept(Wish.VideoItemClicked(item))
    }

    private fun stateConsumer() = Consumer<State> { state ->
        vgLoader.isVisible = state.isLoading
        state.allItems?.let { adapter.setItems(it) }
    }

    private fun newsConsumer() = Consumer<News> { news ->
        when (news) {
            is News.Error -> Toast.makeText(context, news.msg, Toast.LENGTH_SHORT).show()
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
                    feature.accept(Wish.ListEndReached)
                }
            }
        })
    }

    private fun initListeners() {
        etFilterOptions.textChanges()
            .skipInitialValue()
            .map { it.trim().toString() }
            .map { Wish.ChangeFilter(it) as Wish }
            .observeOn(AndroidSchedulers.mainThread())
            .safeSubscribe(feature)

        fab.setOnClickListener { feature.accept(Wish.AddNewVideoClicked) }

        btnClearFilter.setOnClickListener {
            etFilterOptions.text.clear()
            feature.accept(Wish.CancelFilter)
        }

        btnApplyFilter.setOnClickListener {
            val filter = etFilterOptions.text.toString()
            feature.accept(Wish.ChangeFilter(filter))
        }
    }

    companion object {
        fun newInstance(): VideoListFragment = VideoListFragment()
    }
}