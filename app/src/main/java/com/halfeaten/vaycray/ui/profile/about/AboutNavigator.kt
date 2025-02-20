package com.halfeaten.vaycray.ui.profile.about

import com.halfeaten.vaycray.ui.base.BaseNavigator


interface AboutNavigator : BaseNavigator{

    fun navigateScreen(OpenScreen: AboutViewModel.OpenScreen, vararg params: String?)
}