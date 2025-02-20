package com.halfeaten.vaycray.ui.host.step_three

import com.halfeaten.vaycray.ui.base.BaseNavigator

interface StepThreeNavigator : BaseNavigator {

    fun navigateToScreen(screen : StepThreeViewModel.NextStep)

    fun navigateBack(backScreen : StepThreeViewModel.BackScreen)

    fun show404Page()
}