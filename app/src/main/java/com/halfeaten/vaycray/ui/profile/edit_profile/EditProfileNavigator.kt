package com.halfeaten.vaycray.ui.profile.edit_profile

import com.halfeaten.vaycray.ui.base.BaseNavigator


interface EditProfileNavigator: BaseNavigator {

    fun openEditScreen()

    fun openSplashScreen()

    fun moveToBackScreen()

    fun showLayout()

    fun setLocale(key: String)
}