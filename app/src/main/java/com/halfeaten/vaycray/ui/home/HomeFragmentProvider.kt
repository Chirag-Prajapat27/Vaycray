package com.halfeaten.vaycray.ui.home

import com.halfeaten.vaycray.ui.explore.ExploreFragment
import com.halfeaten.vaycray.ui.explore.OneTotalPriceBottomSheet
import com.halfeaten.vaycray.ui.explore.filter.FilterFragment
import com.halfeaten.vaycray.ui.explore.guest.GuestFragment
import com.halfeaten.vaycray.ui.explore.map.ListingMapFragment
import com.halfeaten.vaycray.ui.explore.search.SearchLocationFragment
import com.halfeaten.vaycray.ui.inbox.InboxFragment
import com.halfeaten.vaycray.ui.profile.ProfileFragment
import com.halfeaten.vaycray.ui.saved.SavedBotomSheet
import com.halfeaten.vaycray.ui.saved.SavedDetailFragment
import com.halfeaten.vaycray.ui.saved.SavedFragment
import com.halfeaten.vaycray.ui.trips.TripsFragment
import com.halfeaten.vaycray.ui.trips.TripsListFragment
import com.halfeaten.vaycray.ui.trips.contactus.ContactSupport
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class HomeFragmentProvider {

    @ContributesAndroidInjector
    abstract fun provideProfileFragmentFactory(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun provideExploreFragmentFactory(): ExploreFragment

    @ContributesAndroidInjector
    abstract fun provideSearchFragmentFactory(): SearchLocationFragment

    @ContributesAndroidInjector
    abstract fun provideFilterFragmentFactory(): FilterFragment

    @ContributesAndroidInjector
    abstract fun provideGuestFragmentFactory(): GuestFragment

    @ContributesAndroidInjector
    abstract fun provideListingMapFragmentFactory(): ListingMapFragment

    @ContributesAndroidInjector
    abstract fun provideTripsFragmentFactory(): TripsFragment

    @ContributesAndroidInjector
    abstract fun provideTripsListFragmentFactory(): TripsListFragment

    @ContributesAndroidInjector
    abstract fun provideInboxFragmentFactory(): InboxFragment

    @ContributesAndroidInjector
    abstract fun provideSavedFragmentFactory(): SavedFragment

    @ContributesAndroidInjector
    abstract fun provideSavedDetailFragmentFactory(): SavedDetailFragment

    @ContributesAndroidInjector
    abstract fun provideSavedBotomSheetFragmentFactory(): SavedBotomSheet
    @ContributesAndroidInjector
    abstract fun provideOneTotalPriceBottomSheetFragmentFactory(): OneTotalPriceBottomSheet

    @ContributesAndroidInjector
    abstract fun provideContactSupportFragmentFactory(): ContactSupport

}