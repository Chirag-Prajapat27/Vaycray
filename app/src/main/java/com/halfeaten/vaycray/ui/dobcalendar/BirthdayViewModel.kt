package com.halfeaten.vaycray.ui.dobcalendar

import android.annotation.SuppressLint
import com.halfeaten.vaycray.data.DataManager
import com.halfeaten.vaycray.ui.base.BaseNavigator
import com.halfeaten.vaycray.ui.base.BaseViewModel
import com.halfeaten.vaycray.util.resource.ResourceProvider
import com.halfeaten.vaycray.util.rx.Scheduler
import javax.inject.Inject

@SuppressLint("LogNotTimber")
class BirthdayViewModel @Inject constructor(
        dataManager: DataManager,
        private val scheduler: Scheduler,
        val resourceProvider: ResourceProvider
): BaseViewModel<BaseNavigator>(dataManager,resourceProvider) {

}