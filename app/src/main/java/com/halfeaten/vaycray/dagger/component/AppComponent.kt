package com.halfeaten.vaycray.dagger.component

import android.app.Application
import com.halfeaten.vaycray.MainApp
import com.halfeaten.vaycray.dagger.builder.ActivityBuilder
import com.halfeaten.vaycray.dagger.builder.ServiceBuilder
import com.halfeaten.vaycray.dagger.module.AppModule
import com.halfeaten.vaycray.data.local.prefs.PreferencesHelper
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, AppModule::class, ActivityBuilder::class, ServiceBuilder::class])
interface AppComponent : AndroidInjector<MainApp> {

    override fun inject(app: MainApp)

    fun getAppPreferencesHelper(): PreferencesHelper

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}
