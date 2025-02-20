package com.halfeaten.vaycray.host.calendar

import com.halfeaten.vaycray.ui.base.BaseNavigator

interface CalendarAvailabilityNavigator : BaseNavigator {

    fun moveBackToScreen()

    fun hideCalendar(flag: Boolean)

    fun hideWholeView(flag: Boolean)

    fun closeAvailability(flag: Boolean)
}