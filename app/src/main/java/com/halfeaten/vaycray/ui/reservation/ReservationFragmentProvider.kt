package com.halfeaten.vaycray.ui.reservation

import com.halfeaten.vaycray.ui.saved.SavedBotomSheet
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ReservationFragmentProvider {

    @ContributesAndroidInjector
    abstract fun provideItineraryFragmentFactory(): ItineraryFragment

    @ContributesAndroidInjector
    abstract fun provideReceiptFragmentFactory(): ReceiptFragment

    @ContributesAndroidInjector
    abstract fun provideSavedBotomSheetFactory(): SavedBotomSheet
}