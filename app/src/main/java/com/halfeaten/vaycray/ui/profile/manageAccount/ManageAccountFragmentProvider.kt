package com.halfeaten.vaycray.ui.profile.manageAccount

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ManageAccountFragmentProvider {

    @ContributesAndroidInjector
    abstract fun provideDeleteAccountDialogFactory(): DeleteAccountDialog
}