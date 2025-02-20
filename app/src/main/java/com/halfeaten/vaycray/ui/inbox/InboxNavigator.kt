package com.halfeaten.vaycray.ui.inbox

import com.halfeaten.vaycray.SendMessageMutation
import com.halfeaten.vaycray.ui.base.BaseNavigator

interface InboxNavigator: BaseNavigator {

    fun moveToBackScreen()

    fun addMessage(text: SendMessageMutation.Results)

    fun openBillingActivity()

    fun openListingDetails()

    fun hideTopView(msg:String)
}