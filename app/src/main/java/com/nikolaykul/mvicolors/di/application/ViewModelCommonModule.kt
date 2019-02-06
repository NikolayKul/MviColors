package com.nikolaykul.mvicolors.di.application

import androidx.lifecycle.ViewModel
import dagger.MapKey
import dagger.Module
import dagger.multibindings.Multibinds
import kotlin.reflect.KClass

@Module
interface ViewModelCommonModule {
    /*
        Remove this stub after declaring at least one common ViewModel
     */
    @Multibinds
    fun viewModelStub(): Map<Class<out ViewModel>, ViewModel>
}

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ViewModelKey(val value: KClass<out ViewModel>)
