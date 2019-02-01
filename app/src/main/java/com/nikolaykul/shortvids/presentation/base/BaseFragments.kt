package com.nikolaykul.shortvids.presentation.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

abstract class BaseFragment<ViewModel, UiEvent, News> : MviFragment<ViewModel, UiEvent, News>(),
    HasSupportFragmentInjector {

    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    @get:LayoutRes
    protected abstract val layoutId: Int

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(layoutId, container, false)

    final override fun supportFragmentInjector(): AndroidInjector<Fragment> = childFragmentInjector
}


abstract class MviFragment<ViewModel, UiEvent, News> : Fragment() {
    val viewModelConsumer = Consumer<ViewModel> { consumeViewModel(it) }
    val newsConsumer = Consumer<News> { consumeNews(it) }
    val uiEvents = PublishSubject.create<UiEvent>()

    protected abstract fun consumeViewModel(vm: ViewModel)

    protected open fun consumeNews(news: News) {
        /* no-op */
    }
}