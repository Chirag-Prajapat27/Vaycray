package com.halfeaten.vaycray.dagger.builder

import com.halfeaten.vaycray.host.payout.addPayout.AddPayoutActivity
import com.halfeaten.vaycray.host.payout.addPayout.AddPayoutFragmentProvider
import com.halfeaten.vaycray.host.payout.editpayout.EditPayoutActivity
import com.halfeaten.vaycray.host.payout.editpayout.EditPayoutFragmentProvider
import com.halfeaten.vaycray.host.photoUpload.Step2FragmentProvider
import com.halfeaten.vaycray.host.photoUpload.UploadPhotoActivity
import com.halfeaten.vaycray.ui.AuthTokenExpireActivity
import com.halfeaten.vaycray.ui.WebViewActivity
import com.halfeaten.vaycray.ui.auth.AuthActivity
import com.halfeaten.vaycray.ui.auth.AuthFragmentProvider
import com.halfeaten.vaycray.ui.booking.BookingActivity
import com.halfeaten.vaycray.ui.booking.BookingFragmentProvider
import com.halfeaten.vaycray.ui.cancellation.CancellationActivity
import com.halfeaten.vaycray.ui.cancellation.CancellationFragmentProvider
import com.halfeaten.vaycray.ui.entry.EntryActivity
import com.halfeaten.vaycray.ui.home.HomeActivity
import com.halfeaten.vaycray.ui.home.HomeFragmentProvider
import com.halfeaten.vaycray.ui.host.HostFinalActivity
import com.halfeaten.vaycray.ui.host.hostHome.HostHomeActivity
import com.halfeaten.vaycray.ui.host.hostHome.HostHomeFragmentProvider
import com.halfeaten.vaycray.ui.host.hostInbox.host_msg_detail.HostNewInboxMsgActivity
import com.halfeaten.vaycray.ui.host.step_one.StepOneActivity
import com.halfeaten.vaycray.ui.host.step_one.StepOneFragmentProvider
import com.halfeaten.vaycray.ui.host.step_three.StepThreeActivity
import com.halfeaten.vaycray.ui.host.step_three.StepThreeFragmentProvider
import com.halfeaten.vaycray.ui.host.step_two.StepTwoFragmentProvider
import com.halfeaten.vaycray.ui.inbox.msg_detail.NewInboxMsgActivity
import com.halfeaten.vaycray.ui.listing.ListingDetails
import com.halfeaten.vaycray.ui.listing.ListingDetailsFragmentProvider
import com.halfeaten.vaycray.ui.payment.PaymentTypeActivity
import com.halfeaten.vaycray.ui.payment.PaymentTypeFragmentProvider
import com.halfeaten.vaycray.ui.profile.about.AboutActivity
import com.halfeaten.vaycray.ui.profile.about.StaticPageActivity
import com.halfeaten.vaycray.ui.profile.about.why_Host.WhyHostActivity
import com.halfeaten.vaycray.ui.profile.confirmPhonenumber.ConfirmPhnoActivity
import com.halfeaten.vaycray.ui.profile.confirmPhonenumber.ConfirmPhnoFragmentProvider
import com.halfeaten.vaycray.ui.profile.edit_profile.EditProfileActivity
import com.halfeaten.vaycray.ui.profile.edit_profile.EditProfileFragmentProvider
import com.halfeaten.vaycray.ui.profile.feedback.FeedbackActivity
import com.halfeaten.vaycray.ui.profile.manageAccount.ManageAccountActivity
import com.halfeaten.vaycray.ui.profile.manageAccount.ManageAccountFragmentProvider
import com.halfeaten.vaycray.ui.profile.review.ReviewActivity
import com.halfeaten.vaycray.ui.profile.review.ReviewFragmentProvider
import com.halfeaten.vaycray.ui.profile.setting.ChangePasswordActivity
import com.halfeaten.vaycray.ui.profile.setting.SettingActivity
import com.halfeaten.vaycray.ui.profile.setting.SettingFragmentProvider
import com.halfeaten.vaycray.ui.profile.trustAndVerify.TrustAndVerifyActivity
import com.halfeaten.vaycray.ui.reservation.ReservationActivity
import com.halfeaten.vaycray.ui.reservation.ReservationFragmentProvider
import com.halfeaten.vaycray.ui.saved.createlist.CreateListActivity
import com.halfeaten.vaycray.ui.splash.SplashActivity
import com.halfeaten.vaycray.ui.splash.SplashFragmentProvider
import com.halfeaten.vaycray.ui.user_profile.UserProfileActivity
import com.halfeaten.vaycray.ui.user_profile.UserProfileFragmentProvider
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract fun bindEntryActivity(): EntryActivity

    @ContributesAndroidInjector(modules = [AuthFragmentProvider::class])
    abstract fun bindAuthActivity(): AuthActivity

    @ContributesAndroidInjector(modules = [SplashFragmentProvider::class])
    abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [HomeFragmentProvider::class])
    abstract fun bindHomeActivity(): HomeActivity

    @ContributesAndroidInjector(modules = [EditProfileFragmentProvider::class])
    abstract fun bindEditProfileActivity(): EditProfileActivity

    @ContributesAndroidInjector(modules = [ListingDetailsFragmentProvider::class])
    abstract fun bindListingDetailActivity(): ListingDetails

    @ContributesAndroidInjector(modules = [CancellationFragmentProvider::class])
    abstract fun bindCancellationActivity(): CancellationActivity

    @ContributesAndroidInjector(modules = [BookingFragmentProvider::class])
    abstract fun bindBookingActivity(): BookingActivity

    @ContributesAndroidInjector(modules = [ReservationFragmentProvider::class])
    abstract fun bindReservationActivity(): ReservationActivity


    @ContributesAndroidInjector(modules = [UserProfileFragmentProvider::class])
    abstract fun bindUserProfileActivity(): UserProfileActivity

    @ContributesAndroidInjector
    abstract fun bindAuthTokenExpireActivity(): AuthTokenExpireActivity

    @ContributesAndroidInjector
    abstract fun bindCreateListActivity(): CreateListActivity

    @ContributesAndroidInjector(modules = [ConfirmPhnoFragmentProvider::class])
    abstract fun bindConfirmPhnoActivity(): ConfirmPhnoActivity

    @ContributesAndroidInjector
    abstract fun bindTrustAndVerifyActivity() : TrustAndVerifyActivity

    @ContributesAndroidInjector(modules = [StepOneFragmentProvider::class])
    abstract fun bindStep_one_Activity(): StepOneActivity

    @ContributesAndroidInjector
    abstract fun bindHostFinalActivity(): HostFinalActivity

    @ContributesAndroidInjector(modules = [HostHomeFragmentProvider::class])
    abstract fun bindHostHomeActivity(): HostHomeActivity

    @ContributesAndroidInjector
    abstract fun bindNewInboxMsgActivity() : NewInboxMsgActivity

    @ContributesAndroidInjector
    abstract fun bindHostNewInboxMsgActivity() : HostNewInboxMsgActivity

    @ContributesAndroidInjector(modules = [StepThreeFragmentProvider::class])
    abstract fun bindStepThreeActivity() : StepThreeActivity

    @ContributesAndroidInjector(modules = [EditPayoutFragmentProvider::class])
    abstract fun bindEditPayoutActivity(): EditPayoutActivity

    @ContributesAndroidInjector(modules = [AddPayoutFragmentProvider::class])
    abstract fun bindAddPayoutActivity(): AddPayoutActivity

    @ContributesAndroidInjector(modules = [Step2FragmentProvider::class])
    abstract fun bindUploadPhotoActivity(): UploadPhotoActivity

    @ContributesAndroidInjector(modules = [SettingFragmentProvider::class])
    abstract fun bindSettingActivity(): SettingActivity

    @ContributesAndroidInjector
    abstract fun bindChangePasswordActivity(): ChangePasswordActivity

    @ContributesAndroidInjector
    abstract fun bindFeedbackActivity(): FeedbackActivity


    @ContributesAndroidInjector
    abstract fun bindAboutActivity(): AboutActivity

    @ContributesAndroidInjector
    abstract fun bindWhyHostFragment(): WhyHostActivity

    @ContributesAndroidInjector(modules = [ReviewFragmentProvider::class])
    abstract fun bindReviewActivity(): ReviewActivity


    @ContributesAndroidInjector(modules = [PaymentTypeFragmentProvider::class])
    abstract fun bindPaymentActivity(): PaymentTypeActivity

    @ContributesAndroidInjector
    abstract fun bindWebViewActivity(): WebViewActivity

    @ContributesAndroidInjector
    abstract fun bindStaticContentActivity(): StaticPageActivity

    @ContributesAndroidInjector(modules = [ManageAccountFragmentProvider::class])
    abstract fun bindManageAccountActivity(): ManageAccountActivity
}
