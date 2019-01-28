package com.nikolaykul.shortvids.presentation.list

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.nikolaykul.shortvids.R
import com.nikolaykul.shortvids.presentation.base.BaseFragment
import com.nikolaykul.shortvids.presentation.list.adapter.ListAdapter
import com.nikolaykul.shortvids.presentation.list.adapter.ListItem
import com.nikolaykul.shortvids.presentation.utils.rv.decorations.VerticalMarginDecorator
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : BaseFragment() {
    private val adapter = ListAdapter()

    override val layoutId = R.layout.fragment_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        populateList()
    }

    private fun initList() {
        rvVids.adapter = adapter
        rvVids.layoutManager = LinearLayoutManager(context)
        rvVids.addItemDecoration(VerticalMarginDecorator.withDimen(R.dimen.space_default))
    }

    private fun populateList() {
        fun createItem(i: Int) = ListItem(
            title = "Title for $i",
            subTitle = "Some lorem ipsum for the $i item",
            videoPath = "Actually there's no any videoPath at the moment"
        )

        val items = (0..100).map(::createItem)
        adapter.addItems(items)
    }

    companion object {
        fun newInstance(): ListFragment = ListFragment()
    }
}