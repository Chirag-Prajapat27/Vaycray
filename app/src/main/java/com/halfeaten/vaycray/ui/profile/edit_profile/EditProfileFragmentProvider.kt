package com.halfeaten.vaycray.ui.profile.edit_profile

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class EditProfileFragmentProvider {

    @ContributesAndroidInjector
    abstract fun provideEditProfileFragmentFactory(): EditProfileFragment

}