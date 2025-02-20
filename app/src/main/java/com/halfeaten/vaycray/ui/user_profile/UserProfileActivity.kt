package com.halfeaten.vaycray.ui.user_profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.halfeaten.vaycray.BR
import com.halfeaten.vaycray.Constants
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.ShowUserProfileQuery
import com.halfeaten.vaycray.databinding.ActivityUserprofileBinding
import com.halfeaten.vaycray.ui.auth.AuthActivity
import com.halfeaten.vaycray.ui.base.BaseActivity
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.ui.user_profile.report_user.ReportUserFragment
import com.halfeaten.vaycray.ui.user_profile.review.ReviewFragment
import com.halfeaten.vaycray.util.Utils
import com.halfeaten.vaycray.util.gone
import com.halfeaten.vaycray.util.onClick
import com.halfeaten.vaycray.util.visible
import com.halfeaten.vaycray.viewholderDividerPaddingTop
import com.halfeaten.vaycray.viewholderListingDetailsListShowmore
import com.halfeaten.vaycray.viewholderUserHeadingSmall
import com.halfeaten.vaycray.viewholderUserImage
import com.halfeaten.vaycray.viewholderUserName
import com.halfeaten.vaycray.viewholderUserNormalText
import com.halfeaten.vaycray.viewholderUserNormalTextBlue
import com.halfeaten.vaycray.viewholderUserVerifiedStatus
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

class UserProfileActivity : BaseActivity<ActivityUserprofileBinding, UserProfileViewModel>() {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var mBinding: ActivityUserprofileBinding
    private var totalReviews: Int = 0
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_userprofile
    override val viewModel: UserProfileViewModel
        get() = ViewModelProvider(this, mViewModelFactory).get(UserProfileViewModel::class.java)

    companion object {
        @JvmStatic
        fun openProfileActivity(context: Context, profileId: Int) {
            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra("profileId", profileId)
            context.startActivity(intent)
        }

        @JvmStatic
        fun openProfileActivity(context: Context, profileId: Int, isHost: Boolean) {
            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra("profileId", profileId)
            intent.putExtra("isHost", isHost)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = viewDataBinding!!
        viewModel.navigator = this
        initView()
        subscribeToLiveData()
    }

    private fun subscribeToLiveData() {
        viewModel.userProfile.observe(this, Observer { results ->
            results?.let {
                setUp(it)
            }
        })
    }

    private fun initView() {

        mBinding.actionBar.ivCameraToolbar.gone()
        viewModel.setValuesFromIntent(intent)
        val isHost = intent.extras!!.getBoolean("isHost")
        mBinding.actionBar.ivNavigateup.onClick {
            finish()
        }
    }


    private fun setUp(it: ShowUserProfileQuery.Results) {
        mBinding.rlUserProfile.withModels {
            viewholderUserImage {
                id("viewHolderUserImage")
                pic(it.picture)
            }
            viewholderUserName {
                id("name")
                name(it.firstName)
            }
            if (it.location != null && it.location.isNullOrBlank().not()) {
                viewholderUserHeadingSmall {
                    id("address")
                    text(it.location)
                }
            }
            viewholderUserNormalText {
                id("memberSince")
                val preferences =
                    PreferenceManager.getDefaultSharedPreferences(this@UserProfileActivity)
                val langType = preferences.getString("Locale.Helper.Selected.Language", "en")
                text(
                    resources.getString(R.string.member_since) + " " + Utils.memberSince(
                        it.createdAt,
                        langType
                    )
                )
                paddingBottom(true)
            }
            if (it.info != null) {
                viewholderDividerPaddingTop {
                    id("viewHolderDivider - 1")
                }
                viewholderUserHeadingSmall {
                    id("about")
                    paddingTop(true)
                    paddingBottom(true)
                    text(resources.getString(R.string.about))
                }
            }

            if (it.info.isNullOrEmpty().not()) {
                viewholderUserNormalText {
                    id("info")
                    text(it.info)
                    Utils.findUrl(it.info.toString())
                    paddingBottom(true)
                }
            }
            viewholderDividerPaddingTop {
                id("viewHolderDivider - 2")
            }

            if (it.reviewsCount != null) {
                if (it.reviewsCount!! > 0) {
                    viewholderUserHeadingSmall {
                        id("reviews")
                        if (it.reviewsCount!! > 0) {
                            text(resources.getString(R.string.reviews))
                        } else {
                            text("${it.reviewsCount}" + resources.getString(R.string.review))
                        }
                        paddingTop(true)
                        paddingBottom(true)
                    }
                    viewholderListingDetailsListShowmore {
                        id("see all reviews")
                        paddingTop(true)
                        imgVisibility(2)
                        totalReviews = it.reviewsCount!!
                        text(resources.getString(R.string.read_all) + " " + resources.getString(R.string.reviews_small))

                        clickListener(View.OnClickListener {
                            try {
                                viewModel.userProfile.value?.let {
                                    openFragment(
                                        ReviewFragment.newInstance(
                                            it.reviewsCount,
                                            it.firstName!!
                                        ), "reviews"
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                showError()
                            }
                        })
                    }
                    viewholderDividerPaddingTop {
                        id("viewHolderDivider - 4")
                    }
                }
            }

            val verifyInfo = viewModel.userProfile.value?.userVerifiedInfo!!
            if (verifyInfo.isEmailConfirmed!! || verifyInfo.isGoogleConnected!! || verifyInfo.isFacebookConnected!! || verifyInfo.isIdVerification!! || verifyInfo.isPhoneVerified!!) {
                viewholderUserHeadingSmall {
                    id("VerifiedHeader")
                    text(resources.getString(R.string.verified_info))
                    paddingTop(true)
                    paddingBottom(true)
                }
                viewholderUserVerifiedStatus {
                    id("VerifiedText")
                    isEmail(false)
                    isFacebook(false)
                    isGoogle(false)
                    isPhone(false)
                    if (verifyInfo.isEmailConfirmed!!) {
                        isEmail(true)
                    }
                    if (verifyInfo.isGoogleConnected!!) {
                        isGoogle(true)
                    }
                    if (verifyInfo.isFacebookConnected!!) {
                        isFacebook(true)
                    }
                    if (verifyInfo.isIdVerification!!) {

                    }
                    if (verifyInfo.isPhoneVerified!!) {
                        isPhone(true)
                    }
                }

                viewholderDividerPaddingTop {
                    id("viewHolderDivider - 5")
                }
            }

            if (viewModel.dataManager.isHostOrGuest)
                mBinding.actionBar.tvToolbarHeading.text = resources.getText(R.string.user)
            else
                mBinding.actionBar.tvToolbarHeading.text = resources.getText(R.string.host)


            if (it.userId != viewModel.dataManager.currentUserId) {
                viewholderUserHeadingSmall {
                    id("report")
                    paddingTop(true)
                    paddingBottom(true)
                    text(resources.getString(R.string.report))
                }
                viewholderUserNormalTextBlue {
                    id("ReportThisUser")
                    if (viewModel.dataManager.isHostOrGuest)
                        text(resources.getString(R.string.report_this_user))
                    else {
                        text(resources.getString(R.string.report_this_host))
                    }
                    color(true)
                    paddingBottom(true)
                    paddingTop(true)
                    clickListener(View.OnClickListener { view ->
                        if (viewModel.dataManager.currentUserId == null) {
                            AuthActivity.openActivity(
                                this@UserProfileActivity,
                                "Home"
                            )
                        } else {
                            openFragment(ReportUserFragment(), "report")
                        }

                    })
                }
            }

        }
    }

    fun openFragment(fragment: androidx.fragment.app.Fragment,screen: String) {

        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
            .add(mBinding.flUserprofile.id, fragment)
            .addToBackStack(null)
            .commit()
    }
    fun supportFragmentInjector(): AndroidInjector<androidx.fragment.app.Fragment> {
        return fragmentDispatchingAndroidInjector
    }



    override fun onRetry() {
        if (supportFragmentManager.backStackEntryCount != 0) {
            (supportFragmentManager.fragments[supportFragmentManager.backStackEntryCount] as BaseFragment<*, *>).onRetry()
        } else {
            viewModel.getUserProfile()
        }
    }
}