package com.halfeaten.vaycray.dagger.builder

import com.halfeaten.vaycray.firebase.MyFirebaseMessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ServiceBuilder {

    @ContributesAndroidInjector
    abstract fun bindFirebaseService(): MyFirebaseMessagingService

}