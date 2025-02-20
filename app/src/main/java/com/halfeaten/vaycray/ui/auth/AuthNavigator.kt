package com.halfeaten.vaycray.ui.auth

import com.halfeaten.vaycray.ui.base.BaseNavigator

interface AuthNavigator: BaseNavigator {

    fun navigateScreen(screen: AuthViewModel.Screen, vararg params: String?)

}