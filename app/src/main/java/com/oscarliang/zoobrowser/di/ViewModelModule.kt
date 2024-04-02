package com.oscarliang.zoobrowser.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarliang.zoobrowser.ui.area.AreaViewModel
import com.oscarliang.zoobrowser.ui.zoo.ZooViewModel
import com.oscarliang.zoobrowser.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ZooViewModel::class)
    abstract fun bindZooViewModel(viewModel: ZooViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AreaViewModel::class)
    abstract fun bindAreaViewModel(viewModel: AreaViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}
