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
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

abstract class BaseFragment<State, Wish, News> : MviFragment<State, Wish, News>(),
    HasSupportFragmentInjector {

    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>
    private val disposables = CompositeDisposable()

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

    override fun onDestroyView() {
        disposables.clear()
        super.onDestroyView()
    }

    final override fun supportFragmentInjector(): AndroidInjector<Fragment> = childFragmentInjector

    protected fun <T> Observable<T>.safeSubscribe(onNext: Consumer<T>): Disposable =
        subscribe(onNext, Consumer { Timber.e(it) })
            .also { disposables.add(it) }
}


abstract class MviFragment<State, Wish, News> : Fragment() {
    val stateConsumer = Consumer<State> { consumeState(it) }
    val newsConsumer = Consumer<News> { consumeNews(it) }
    val wishProvider = PublishSubject.create<Wish>()

    protected abstract fun consumeState(state: State)

    protected open fun consumeNews(news: News) {
        /* no-op */
    }
}