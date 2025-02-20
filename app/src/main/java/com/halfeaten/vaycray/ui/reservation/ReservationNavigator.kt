package com.halfeaten.vaycray.ui.reservation

import com.halfeaten.vaycray.ui.base.BaseNavigator

interface ReservationNavigator: BaseNavigator {

    fun navigateToScreen(screen : Int)

    fun show404Page()

}