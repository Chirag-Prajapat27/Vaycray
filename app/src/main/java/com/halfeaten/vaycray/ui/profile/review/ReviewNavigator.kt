package com.halfeaten.vaycray.ui.profile.review

import android.view.View
import com.halfeaten.vaycray.ui.base.BaseNavigator

interface ReviewNavigator : BaseNavigator{

    fun moveToScreen(screen: ReviewViewModel.ViewScreen)

    fun openWriteReview(reservationId: Int)

    fun show404Page()
}