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
import com.nikolaykul.mvicolors.presentation.color.list.adapter.ColorListAdapter
import com.nikolaykul.mvicolors.presentation.color.list.adapter.ColorListItem
import com.nikolaykul.mvicolors.presentation.utils.rv.decorations.VerticalMarginDecorator
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_color_list.*
import kotlinx.android.synthetic.main.fragment_color_list_toolbar.*
import kotlinx.android.synthetic.main.fragment_color_loader.*
import timber.log.Timber

private const val LOAD_MORE_THRESHOLD = 5

class ColorListFragment : BaseFragment(), ColorListAdapter.Listener {

    override val layoutId = R.layout.fragment_color_list

    private lateinit var adapter: ColorListAdapter
    private val viewModel by viewModelDelegate<ColorListViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initListeners()

        viewModel.observeState()
            .safeSubscribe { handleState(it) }
    }

    override fun onItemClicked(item: ColorListItem) {
        viewModel.onColorItemClicked(item)
    }

    private fun handleState(state: ColorListState) {
        Timber.d("NextState -> $state")

        vgLoader.isVisible = state is ColorListState.Loading
        when (state) {
            is ColorListState.NewItems -> {
                adapter.setItems(state.items)
            }
            is ColorListState.ExtraBottomItems -> {
                adapter.addItems(state.items)
            }
            is ColorListState.Error -> {
                Toast.makeText(context, state.msg, Toast.LENGTH_SHORT).show()
            }
        }
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
                    viewModel.onListEndReached()
                }
            }
        })
    }

    private fun initListeners() {
        etFilterOptions.textChanges()
            .map { it.trim().toString() }
            .observeOn(AndroidSchedulers.mainThread())
            .safeSubscribe { viewModel.onFilterChanged(it) }

        fab.setOnClickListener { viewModel.onAddNewColorClicked() }

        btnClearFilter.setOnClickListener {
            etFilterOptions.text.clear()
            viewModel.onFilterCancelled()
        }

        btnApplyFilter.setOnClickListener {
            val filter = etFilterOptions.text.toString()
            viewModel.onFilterChanged(filter)
        }
    }

    companion object {
        fun newInstance(): ColorListFragment = ColorListFragment()
    }
}