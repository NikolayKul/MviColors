package com.nikolaykul.shortvids.presentation.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.badoo.mvicore.android.lifecycle.CreateDestroyBinderLifecycle
import com.badoo.mvicore.binder.Binder
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import timber.log.Timber
import javax.inject.Inject

abstract class BaseFragment : Fragment(), HasSupportFragmentInjector {

    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>
    protected val binder: Binder by lazy { Binder(CreateDestroyBinderLifecycle(lifecycle)) }
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