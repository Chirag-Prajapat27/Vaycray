package com.halfeaten.vaycray.ui.splash

import com.halfeaten.vaycray.ui.profile.manageAccount.DeleteAccountDialog
import com.halfeaten.vaycray.ui.profile.manageAccount.ForceUpdateDialog
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class SplashFragmentProvider {

    @ContributesAndroidInjector
    abstract fun provideForceUpdateDialogFactory(): ForceUpdateDialog
}