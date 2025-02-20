package com.halfeaten.vaycray.ui.host.step_two

import com.halfeaten.vaycray.ui.base.BaseNavigator

interface StepTwoNavigator : BaseNavigator {

    fun navigateToScreen(screen : StepTwoViewModel.NextScreen,vararg params: String?)

}