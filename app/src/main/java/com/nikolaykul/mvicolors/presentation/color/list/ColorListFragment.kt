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
import com.nikolaykul.mvicolors.presentation.utils.rv.decorations.VerticalMarginDecorator
import com.nikolaykul.mvicolors.presentation.color.list.ColorListStateMachine.Action
import com.nikolaykul.mvicolors.presentation.color.list.ColorListStateMachine.State
import com.nikolaykul.mvicolors.presentation.color.list.adapter.ColorListAdapter
import com.nikolaykul.mvicolors.presentation.color.list.adapter.ColorListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_color_list.*
import kotlinx.android.synthetic.main.fragment_color_list_toolbar.*
import kotlinx.android.synthetic.main.fragment_color_loader.*
import javax.inject.Inject

private const val LOAD_MORE_THRESHOLD = 5

class ColorListFragment : BaseFragment<State>(), ColorListAdapter.Listener {

    override val layoutId = R.layout.fragment_color_list

    @Inject lateinit var stateMachine: ColorListStateMachine
    private lateinit var adapter: ColorListAdapter

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

    override fun onItemClicked(item: ColorListItem) {
        stateMachine.input.accept(Action.NavigateToDetails(item))
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
                    stateMachine.input.accept(Action.LoadMoreColors)
                }
            }
        })
    }

    private fun initListeners() {
        etFilterOptions.textChanges()
            .map { it.trim().toString() }
            .map { Action.LoadColors(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .safeSubscribe { stateMachine.input.accept(it) }

        fab.setOnClickListener { stateMachine.input.accept(Action.NavigateToAddNewColor) }

        btnClearFilter.setOnClickListener {
            etFilterOptions.text.clear()
            stateMachine.input.accept(Action.ClearColorsFilter)
        }

        btnApplyFilter.setOnClickListener {
            val filter = etFilterOptions.text.toString()
            stateMachine.input.accept(Action.LoadColors(filter))
        }
    }

    companion object {
        fun newInstance(): ColorListFragment = ColorListFragment()
    }
}