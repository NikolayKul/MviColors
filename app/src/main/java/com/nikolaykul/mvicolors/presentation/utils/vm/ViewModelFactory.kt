package com.nikolaykul.mvicolors.presentation.utils.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Reusable
import javax.inject.Inject
import javax.inject.Provider

@Reusable
class ViewModelFactory @Inject constructor(
    private val providers: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(clazz: Class<T>): T {
        val provider = providers[clazz]
            ?: throw IllegalArgumentException("Unknown ViewModel class: $clazz")
        return provider.get() as T
    }
}
