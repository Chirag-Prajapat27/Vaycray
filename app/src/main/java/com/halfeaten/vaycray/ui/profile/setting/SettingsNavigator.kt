package com.halfeaten.vaycray.ui.profile.setting

import com.halfeaten.vaycray.ui.base.BaseNavigator

interface SettingsNavigator : BaseNavigator  {

    fun openSplashScreen()

    fun navigateToSplash()

    fun setLocale(key: String)

    fun finishActivity()

}