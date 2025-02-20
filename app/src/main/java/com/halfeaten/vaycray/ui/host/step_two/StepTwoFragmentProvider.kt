package com.halfeaten.vaycray.ui.host.step_two

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class StepTwoFragmentProvider {


    @ContributesAndroidInjector
    abstract fun provideCoverPhotoFragment() : CoverPhotoFragment

    @ContributesAndroidInjector
    abstract fun provideAddListTitleFragment() : AddListTitleFragment

    @ContributesAndroidInjector
    abstract fun provideAddListDescFragment() : AddListDescFragment
}