package com.halfeaten.vaycray.ui.profile.trustAndVerify

import com.halfeaten.vaycray.ui.base.BaseNavigator

interface TrustAndVerifyNavigator : BaseNavigator{

    fun show404Error(message : String )

    fun navigateToSplash()

    fun moveToPreviousScreen()
}