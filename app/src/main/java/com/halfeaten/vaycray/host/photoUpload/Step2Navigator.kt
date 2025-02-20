package com.halfeaten.vaycray.host.photoUpload

import com.halfeaten.vaycray.ui.base.BaseNavigator

interface Step2Navigator : BaseNavigator {

    fun navigateToScreen(screen : Step2ViewModel.NextScreen, vararg params: String?)

    fun navigateBack(backScreen : Step2ViewModel.BackScreen)

    fun show404Page()


}