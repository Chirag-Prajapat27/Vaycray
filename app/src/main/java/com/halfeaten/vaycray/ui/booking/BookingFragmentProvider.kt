package com.halfeaten.vaycray.ui.booking

import com.halfeaten.vaycray.ui.reservation.ItineraryFragment
import com.halfeaten.vaycray.ui.reservation.ReceiptFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class BookingFragmentProvider {


    @ContributesAndroidInjector
    abstract fun provideStep4FragmentFactory(): Step4Fragment

    @ContributesAndroidInjector
    abstract fun provideItineraryFragmentFactory(): ItineraryFragment

    @ContributesAndroidInjector
    abstract fun provideReceiptFragmentFactory(): ReceiptFragment

}