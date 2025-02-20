package com.halfeaten.vaycray.ui.payment

import com.halfeaten.vaycray.ui.base.BaseNavigator
import org.jetbrains.annotations.Nullable

interface PaymentNavigator: BaseNavigator {

    fun moveToReservation(id: Int)

    fun finishScreen()

    fun moveToPayPalWebView(redirectUrl: String)
}