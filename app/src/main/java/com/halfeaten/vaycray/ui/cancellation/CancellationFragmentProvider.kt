package com.halfeaten.vaycray.ui.cancellation

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class CancellationFragmentProvider {

    @ContributesAndroidInjector
    abstract fun provideCancellationFragmentFactory(): CancellationPolicy

}