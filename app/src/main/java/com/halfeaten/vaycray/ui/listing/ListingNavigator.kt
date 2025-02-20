package com.halfeaten.vaycray.ui.listing

import com.halfeaten.vaycray.ui.base.BaseNavigator

interface ListingNavigator : BaseNavigator {

    fun openBillingActivity(isProfilePresent: Boolean)

    fun openPriceBreakdown()

    fun removeSubScreen()

    fun show404Screen()

    fun showReportScreen()
}