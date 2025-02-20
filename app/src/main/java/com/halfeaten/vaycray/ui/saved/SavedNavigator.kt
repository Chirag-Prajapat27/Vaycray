package com.halfeaten.vaycray.ui.saved

import com.halfeaten.vaycray.ui.base.BaseNavigator

interface SavedNavigator : BaseNavigator {

    fun moveUpScreen()

    fun showEmptyMessageGroup()

    fun reloadExplore()
}