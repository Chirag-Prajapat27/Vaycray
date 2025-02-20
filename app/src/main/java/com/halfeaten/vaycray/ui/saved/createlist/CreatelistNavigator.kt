package com.halfeaten.vaycray.ui.saved.createlist

import com.halfeaten.vaycray.ui.base.BaseNavigator

interface CreatelistNavigator: BaseNavigator {

    fun navigate(isLoadSaved: Boolean)

}