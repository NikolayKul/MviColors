package com.nikolaykul.mvicolors.presentation.color.list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.widget.textChanges
import com.nikolaykul.mvicolors.R
import com.nikolaykul.mvicolors.presentation.base.BaseFragment
import com.nikolaykul.mvicolors.presentation.color.list.ColorListFeature.News
import com.nikolaykul.mvicolors.presentation.color.list.adapter.ColorListAdapter
import com.nikolaykul.mvicolors.presentation.color.list.adapter.ColorListItem
import com.nikolaykul.mvicolors.presentation.color.list.binding.ColorListBinding
import com.nikolaykul.mvicolors.presentation.color.list.binding.ColorListUiEvent
import com.nikolaykul.mvicolors.presentation.color.list.binding.ColorListViewModel
import com.nikolaykul.mvicolors.presentation.utils.rv.decorations.VerticalMarginDecorator
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_color_list.*
import kotlinx.android.synthetic.main.fragment_color_list_toolbar.*
import kotlinx.android.synthetic.main.fragment_color_loader.*
import javax.inject.Inject

private const val LOAD_MORE_THRESHOLD = 5

class ColorListFragment : BaseFragment<ColorListViewModel, ColorListUiEvent, News>(),
    ColorListAdapter.Listener {

    override val layoutId = R.layout.fragment_color_list

    @Inject lateinit var binding: ColorListBinding
    private lateinit var adapter: ColorListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initListeners()

        binding.setup(this)
    }

    override fun consumeViewModel(vm: ColorListViewModel) {
        vgLoader.isVisible = vm.isLoading
        adapter.items = vm.items
    }

    override fun consumeNews(news: News) {
        when (news) {
            is News.Error -> Toast.makeText(context, news.msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemClicked(item: ColorListItem) {
        uiEvents.accept(ColorListUiEvent.OnColorItemClicked(item))
    }

    private fun initList() {
        adapter = ColorListAdapter(this)
        val layoutManager = LinearLayoutManager(context)

        rvColors.adapter = adapter
        rvColors.layoutManager = layoutManager
        rvColors.addItemDecoration(VerticalMarginDecorator.withDimen(R.dimen.space_default))
        rvColors.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                if (dy > 0 && adapter.itemCount - lastVisiblePosition < LOAD_MORE_THRESHOLD) {
                    uiEvents.accept(ColorListUiEvent.OnListEndReached)
                }
            }
        })
    }

    private fun initListeners() {
        etFilterOptions.textChanges()
            .map { it.trim().toString() }
            .map { ColorListUiEvent.OnFilterChanged(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .safeSubscribe { uiEvents.accept(it) }

        fab.setOnClickListener { uiEvents.accept(ColorListUiEvent.OnAddNewColorClicked) }

        btnClearFilter.setOnClickListener {
            etFilterOptions.text.clear()
            uiEvents.accept(ColorListUiEvent.OnFilterCanceled)
        }

        btnApplyFilter.setOnClickListener {
            val filter = etFilterOptions.text.toString()
            uiEvents.accept(ColorListUiEvent.OnFilterChanged(filter))
        }
    }

    companion object {
        fun newInstance(): ColorListFragment = ColorListFragment()
    }
}