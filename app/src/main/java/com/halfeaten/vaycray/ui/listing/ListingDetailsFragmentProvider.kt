package com.halfeaten.vaycray.ui.listing

import com.halfeaten.vaycray.ui.booking.Step4Fragment
import com.halfeaten.vaycray.ui.listing.amenities.AmenitiesBottomFragment
import com.halfeaten.vaycray.ui.listing.amenities.AmenitiesFragment
import com.halfeaten.vaycray.ui.listing.cancellation.CancellationFragment
import com.halfeaten.vaycray.ui.listing.contact_host.ContactHostFragment
import com.halfeaten.vaycray.ui.listing.desc.DescriptionFragment
import com.halfeaten.vaycray.ui.listing.guest.GuestFragment
import com.halfeaten.vaycray.ui.listing.map.MapFragment
import com.halfeaten.vaycray.ui.listing.photo_story.PhotoStoryFragment
import com.halfeaten.vaycray.ui.listing.pricebreakdown.PriceBreakDownFragment
import com.halfeaten.vaycray.ui.listing.report.ReportUserDialog
import com.halfeaten.vaycray.ui.listing.review.ReviewFragment
import com.halfeaten.vaycray.ui.saved.SavedBotomSheet
import com.halfeaten.vaycray.ui.user_profile.report_user.ReportUserFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ListingDetailsFragmentProvider {

    @ContributesAndroidInjector
    abstract fun provideAmenitiesFragmentFactory(): AmenitiesFragment

    @ContributesAndroidInjector
    abstract fun provideDescriptionFragmentFactory(): DescriptionFragment

    @ContributesAndroidInjector
    abstract fun provideMapFragmentFactory(): MapFragment

    @ContributesAndroidInjector
    abstract fun provideReviewFragmentFactory(): ReviewFragment

    @ContributesAndroidInjector
    abstract fun provideCancellationFragmentFactory(): CancellationFragment

    @ContributesAndroidInjector
    abstract fun providePhotoStoryFragmentFactory(): PhotoStoryFragment

    @ContributesAndroidInjector
    abstract fun providePriceBreakDownFragmentFactory(): PriceBreakDownFragment

    @ContributesAndroidInjector
    abstract fun provideContactHostFragmentFactory(): ContactHostFragment

    @ContributesAndroidInjector
    abstract fun provideGuestFragmentFactory(): GuestFragment

    @ContributesAndroidInjector
    abstract fun provideSavedBottomFactory(): SavedBotomSheet

    @ContributesAndroidInjector
    abstract fun provideAmenitiesBottomFragmentFactory(): AmenitiesBottomFragment

    @ContributesAndroidInjector
    abstract fun provideReportUserFragment(): ReportUserFragment

    @ContributesAndroidInjector
    abstract fun provideDeleteAccountDialogFactory(): ReportUserDialog

    @ContributesAndroidInjector
    abstract fun provideStep4FragmentFactory(): Step4Fragment
}