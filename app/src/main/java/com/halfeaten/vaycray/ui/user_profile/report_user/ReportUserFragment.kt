package com.halfeaten.vaycray.ui.user_profile.report_user

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.halfeaten.vaycray.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.FragmentUserProfileBinding
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.ui.user_profile.UserProfileNavigator
import com.halfeaten.vaycray.ui.user_profile.UserProfileViewModel
import com.halfeaten.vaycray.util.gone
import com.halfeaten.vaycray.util.onClick
import com.halfeaten.vaycray.viewholderDivider
import com.halfeaten.vaycray.viewholderReportUserRadio
import com.halfeaten.vaycray.viewholderUserName
import com.halfeaten.vaycray.viewholderUserNormalText
import javax.inject.Inject


class ReportUserFragment() : BaseFragment<FragmentUserProfileBinding, UserProfileViewModel>(),
    UserProfileNavigator {

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
    private var selectArray = arrayOf(false, false, false)
    private var isSelected = false

    constructor(hello: String) : this() {

    }

    var isReCreated = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding!!
        viewModel.navigator = this

        val bundle = this.arguments
        if (bundle != null) {
            viewModel.profileID.value = bundle.getInt("ProfileID", 0)
        }
        initView()
        setUp()
    }

    private fun initView() {
        mBinding.text = viewModel.selectContent
        mBinding.actionBar.tvToolbarHeading.text = getString(R.string.report)
        mBinding.actionBar.ivCameraToolbar.gone()

        mBinding.actionBar.ivNavigateup.onClick {
            activity?.supportFragmentManager?.popBackStack()
        }

        mBinding.tvOk.onClick {
            if (isSelected) {
                viewModel.reportUser()
            } else {
                Toast.makeText(
                    context,
                    resources.getString(R.string.please_select_the_option),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setUp() {
        mBinding.rvUserProfile.withModels {
            viewholderUserName {
                id("header")
                if (viewModel.dataManager.isHostOrGuest) {
                    if (viewModel.isHosts.value != null) {
                        name(resources.getString(R.string.do_you_want_to_report_this_user))
                    } else {
                        name(resources.getString(R.string.do_you_want_to_report_this_host))
                    }
                } else {
                    if (viewModel.isHosts.value == true)
                        name(resources.getString(R.string.do_you_want_to_report_this_host))
                    else
                        name(resources.getString(R.string.do_you_want_to_report_this_user))
                }
                paddingTop(true)
            }
            viewholderUserNormalText {
                id("headerInfo")
                text(resources.getString(R.string.if_so_please_choose_one_of_the_following_reasons))
                paddingTop(true)
                paddingBottom(true)
            }
            viewholderReportUserRadio {
                viewModel.resourceProvider
                id("1")
                text(resources.getString(R.string.this_profile_should_not_be_on_appname))
                radioVisibility(selectArray[0])
                onClick(View.OnClickListener { selector(0); viewModel.selectContent.set("Shouldn't available") })
            }
            viewholderDivider {
                id("Divider - 1")
            }
            viewholderReportUserRadio {
                id("2")
                text(resources.getString(R.string.attempt_to_share_contact_information))
                radioVisibility(selectArray[1])
                onClick(View.OnClickListener { selector(1); viewModel.selectContent.set("Direct contact") })
            }
            viewholderDivider {
                id("Divider - 2")
            }
            viewholderReportUserRadio {
                id("3")
                text(resources.getString(R.string.inappropriate_content_of_spam))
                radioVisibility(selectArray[2])
                onClick(View.OnClickListener { selector(2); viewModel.selectContent.set("Spam") })
            }
        }
    }

    private fun selector(index: Int) {
        selectArray.forEachIndexed { i: Int, _: Boolean ->
            selectArray[i] = index == i
            isSelected = true
        }
        mBinding.rvUserProfile.requestModelBuild()
    }

    override fun onDestroyView() {
        viewModel.selectContent.set("")
        super.onDestroyView()
    }

    override fun closeScreen() {
        activity?.supportFragmentManager?.popBackStack()
    }

    override fun onRetry() {

    }

    override fun onResume() {
        super.onResume()
        if (viewModel.uiMode == null) {
            viewModel.clearStatusBar(requireActivity())
        }
    }

}
