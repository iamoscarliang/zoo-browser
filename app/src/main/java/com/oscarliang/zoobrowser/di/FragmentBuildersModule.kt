package com.oscarliang.zoobrowser.di

import com.oscarliang.zoobrowser.ui.area.AreaFragment
import com.oscarliang.zoobrowser.ui.zoo.ZooFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeZooFragment(): ZooFragment

    @ContributesAndroidInjector
    abstract fun contributeAreaFragment(): AreaFragment

}