package com.halfeaten.vaycray.ui.host.hostListing

import com.halfeaten.vaycray.ui.base.BaseNavigator

interface HostListingNavigator : BaseNavigator {
    fun show404Screen()

    fun showListDetails()

    fun showNoListMessage()

    fun hideLoading()
}