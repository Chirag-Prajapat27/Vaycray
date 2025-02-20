package com.halfeaten.vaycray.host.payout.addPayout

import com.halfeaten.vaycray.ui.base.BaseNavigator


interface AddPayoutNavigator: BaseNavigator {

    fun moveToScreen(screen: AddPayoutActivity.Screen)
}