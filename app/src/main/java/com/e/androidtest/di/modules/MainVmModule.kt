package com.e.androidtest.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.e.androidtest.di.scopes.ApplicationScope
import com.e.androidtest.di.scopes.ViewModelKey
import com.e.androidtest.di.viewmodelfactory.ViewModelProviderFactory
import com.e.androidtest.ui.viewmodel.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainVmModule {


    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainVm(viewModel: MainViewModel): ViewModel

    @Binds
    @ApplicationScope
    abstract fun bindVmFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}
