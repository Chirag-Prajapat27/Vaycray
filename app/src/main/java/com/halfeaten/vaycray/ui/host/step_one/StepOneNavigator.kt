package com.halfeaten.vaycray.ui.host.step_one

import com.halfeaten.vaycray.ui.base.BaseNavigator

interface StepOneNavigator : BaseNavigator {

    fun navigateScreen(NextScreen: StepOneViewModel.NextScreen)

    fun navigateBack(BackScreen : StepOneViewModel.BackScreen)

    fun show404Page()

}