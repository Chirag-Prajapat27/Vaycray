package com.halfeaten.vaycray.ui.user_profile.verified

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.halfeaten.vaycray.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.ShowUserProfileQuery
import com.halfeaten.vaycray.databinding.FragmentUserProfileBinding
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.ui.user_profile.UserProfileViewModel
import com.halfeaten.vaycray.util.gone
import com.halfeaten.vaycray.util.onClick
import com.halfeaten.vaycray.viewholderUserName
import com.halfeaten.vaycray.viewholderVerifiedInfo
import javax.inject.Inject

class VerifiedInfoFragment : BaseFragment<FragmentUserProfileBinding, UserProfileViewModel>() {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var mBinding: FragmentUserProfileBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_user_profile
    override val viewModel: UserProfileViewModel
        get() = ViewModelProvider(
            baseActivity!!,
            mViewModelFactory
        ).get(UserProfileViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding!!
        subscribeToLiveData()
        mBinding.llBottomBtn.gone()
        mBinding.actionBar.root.gone()
        mBinding.actionBar.ivCameraToolbar.gone()
        mBinding.actionBar.tvToolbarHeading.gone()
        mBinding.actionBar.ivNavigateup.onClick {
            baseActivity?.onBackPressed()
        }
    }

    private fun subscribeToLiveData() {
        viewModel.userProfile.observe(viewLifecycleOwner, Observer { results ->
            results?.let {
                setUp(it)
            }
        })
    }

    private fun setUp(it: ShowUserProfileQuery.Results) {
        mBinding.rvUserProfile.withModels {
            viewholderUserName {
                id("header")
                name(resources.getString(R.string.verified_info))
                paddingTop(true)
                paddingBottom(true)
            }
            if (it.userVerifiedInfo?.isEmailConfirmed!!) {
                viewholderVerifiedInfo {
                    id("1")
                    verifiedText(resources.getString(R.string.email_confirmed))
                    isVerified(it.userVerifiedInfo?.isEmailConfirmed)
                }
            }
            if (it.userVerifiedInfo?.isFacebookConnected!!) {
                viewholderVerifiedInfo {
                    id("2")
                    verifiedText(resources.getString(R.string.facebook_connected))
                    isVerified(it.userVerifiedInfo?.isFacebookConnected)
                }
            }
            if (it.userVerifiedInfo?.isGoogleConnected!!) {
                viewholderVerifiedInfo {
                    id("3")
                    verifiedText(resources.getString(R.string.google_connected))
                    isVerified(it.userVerifiedInfo?.isGoogleConnected)
                }
            }
            if (it.userVerifiedInfo?.isIdVerification!!) {
                viewholderVerifiedInfo {
                    id("3")
                    verifiedText(resources.getString(R.string.document_verification))
                    isVerified(it.userVerifiedInfo?.isIdVerification)
                }
            }
            if (it.userVerifiedInfo?.isPhoneVerified!!) {
                viewholderVerifiedInfo {
                    id("3")
                    verifiedText(resources.getString(R.string.phone_verified))
                    isVerified(it.userVerifiedInfo?.isPhoneVerified)
                }
            }
        }
    }

    override fun onDestroyView() {
        mBinding.rvUserProfile.adapter = null
        super.onDestroyView()
    }

    override fun onRetry() {

    }

}
