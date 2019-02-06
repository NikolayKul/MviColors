package com.nikolaykul.mvicolors.presentation.utils.vm

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlin.reflect.KProperty

class ViewModelDelegate<T : ViewModel>(
    private val viewModelStore: Any,
    private val factory: () -> ViewModelProvider.Factory,
    private val clazz: Class<T>
) {
    @Volatile
    private var _value: T? = null

    operator fun getValue(thisRef: Any?, prop: KProperty<*>): T {
        val vm1 = _value
        if (vm1 != null) {
            return vm1
        }

        synchronized(this) {
            var vm2 = _value
            if (vm2 == null) {
                vm2 = getViewModelProvider().get(clazz)
                _value = vm2
            }
            return vm2
        }
    }

    private fun getViewModelProvider(): ViewModelProvider = when (viewModelStore) {
        // use smart-cast
        is Fragment -> ViewModelProviders.of(viewModelStore, factory())
        is FragmentActivity -> ViewModelProviders.of(viewModelStore, factory())
        else -> throw IllegalArgumentException("Unknown ViewModelStore: $viewModelStore")
    }
}
