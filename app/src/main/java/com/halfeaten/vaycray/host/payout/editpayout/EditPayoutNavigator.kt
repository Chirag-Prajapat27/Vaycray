package com.halfeaten.vaycray.host.payout.editpayout

import com.halfeaten.vaycray.ui.base.BaseNavigator

interface EditPayoutNavigator: BaseNavigator {

    fun disableCountrySearch(flag: Boolean)
    fun moveToScreen(screen: EditPayoutActivity.Screen)
}