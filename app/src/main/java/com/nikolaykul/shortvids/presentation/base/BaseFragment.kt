package com.nikolaykul.shortvids.presentation.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.nikolaykul.shortvids.presentation.utils.vm.ViewModelDelegate
import com.nikolaykul.shortvids.presentation.utils.vm.ViewModelFactory
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

abstract class BaseFragment : Fragment(), HasSupportFragmentInjector {

    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var factory: ViewModelFactory
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

    protected inline fun <reified T : ViewModel> Fragment.viewModelDelegate() =
        ViewModelDelegate(this, { factory }, T::class.java)

    protected fun <T> Observable<T>.safeSubscribe(
        onComplete: () -> Unit = { /* no-op */ },
        onError: (Throwable) -> Unit = Timber::e,
        onNext: (T) -> Unit
    ): Disposable =
        subscribe(onNext, onError, onComplete)
            .also { disposables.add(it) }
}