package com.halfeaten.vaycray.ui.profile.confirmPhonenumber

import com.halfeaten.vaycray.ui.base.BaseNavigator

interface ConfirmPhnoNavigator : BaseNavigator {

    fun navigateScreen(PHScreen: ConfirmPhnoViewModel.PHScreen, vararg params: String?)

}