package com.halfeaten.vaycray.ui.profile.manageAccount

import com.halfeaten.vaycray.ui.base.BaseNavigator


interface ManageAccountNavigator : BaseNavigator{
    fun navigateScreen(OpenScreen: ManageAccountViewModel.OpenScreen, vararg params: String?)
    fun closeDialog()
}