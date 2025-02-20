package com.halfeaten.vaycray.ui

import com.halfeaten.vaycray.data.DataManager
import com.halfeaten.vaycray.ui.base.BaseNavigator
import com.halfeaten.vaycray.ui.base.BaseViewModel
import com.halfeaten.vaycray.util.resource.ResourceProvider
import com.halfeaten.vaycray.util.rx.Scheduler
import javax.inject.Inject

class AuthTokenViewModel @Inject constructor(
        dataManager: DataManager,
        val scheduler: Scheduler,
        val resourceProvider: ResourceProvider
) : BaseViewModel<BaseNavigator>(dataManager,resourceProvider) {

    init {
        dataManager.setUserAsLoggedOut()
    }
}