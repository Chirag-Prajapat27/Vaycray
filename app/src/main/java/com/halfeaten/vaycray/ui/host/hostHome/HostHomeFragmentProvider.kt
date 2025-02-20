package com.halfeaten.vaycray.ui.host.hostHome

import com.halfeaten.vaycray.host.calendar.CalendarAvailabilityFragment
import com.halfeaten.vaycray.host.calendar.CalendarListingDialog
import com.halfeaten.vaycray.host.calendar.CalendarListingFragment
import com.halfeaten.vaycray.host.calendar.CalendarListingFragment1
import com.halfeaten.vaycray.ui.host.hostInbox.HostInboxFragment
import com.halfeaten.vaycray.ui.host.hostListing.HostListingFragment
import com.halfeaten.vaycray.ui.host.hostListing.HostListingListFragment
import com.halfeaten.vaycray.ui.host.hostReservation.HostTripsFragment
import com.halfeaten.vaycray.ui.host.hostReservation.HostTripsListFragment
import com.halfeaten.vaycray.ui.host.hostReservation.hostContactUs.HostContactSupport
import com.halfeaten.vaycray.ui.profile.ProfileFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class HostHomeFragmentProvider {

    @ContributesAndroidInjector
    abstract fun provideProfileFragmentFactory(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun provideHostInboxFragmentFactory(): HostInboxFragment

    @ContributesAndroidInjector
    abstract fun provideCalendarListingFragmentFactory(): CalendarListingFragment

    @ContributesAndroidInjector
    abstract fun provideHostListingFragment(): HostListingFragment

    @ContributesAndroidInjector
    abstract fun provideHostTripsFragment(): HostTripsFragment

    @ContributesAndroidInjector
    abstract fun provideHostTripsListFragment(): HostTripsListFragment


    @ContributesAndroidInjector
    abstract fun provideCalendarAvailabilityFragmentFactory(): CalendarAvailabilityFragment

    @ContributesAndroidInjector
    abstract fun provideCalendarAvailabilityFragmentFactory1(): CalendarListingFragment1

    @ContributesAndroidInjector
    abstract fun provideCalendarListingDialogFactory(): CalendarListingDialog

    @ContributesAndroidInjector
    abstract fun provideHostContactSupportFragmentFactory(): HostContactSupport

    @ContributesAndroidInjector
    abstract fun provideHostListingListFragmentFactory(): HostListingListFragment
}