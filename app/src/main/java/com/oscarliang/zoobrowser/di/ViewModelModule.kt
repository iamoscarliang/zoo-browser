package com.oscarliang.zoobrowser.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarliang.zoobrowser.ui.animal.AnimalViewModel
import com.oscarliang.zoobrowser.ui.area.AreaViewModel
import com.oscarliang.zoobrowser.ui.bookmarks.BookmarksViewModel
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
    @ViewModelKey(BookmarksViewModel::class)
    abstract fun bindBookmarksViewModel(viewModel: BookmarksViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AreaViewModel::class)
    abstract fun bindAreaViewModel(viewModel: AreaViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AnimalViewModel::class)
    abstract fun bindAnimalViewModel(viewModel: AnimalViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}
