package com.halfeaten.vaycray.ui.host.step_three.guestNotice

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airbnb.epoxy.databinding.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.HostFragmentGuestNoticeBinding
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.ui.host.step_three.OptionsSubFragment
import com.halfeaten.vaycray.ui.host.step_three.StepThreeViewModel
import com.halfeaten.vaycray.util.disable
import com.halfeaten.vaycray.util.enable
import com.halfeaten.vaycray.util.gone
import com.halfeaten.vaycray.util.onClick
import com.halfeaten.vaycray.viewholderListTv
import com.halfeaten.vaycray.viewholderTips
import com.halfeaten.vaycray.viewholderUserName2
import com.halfeaten.vaycray.viewholderUserNormalText
import javax.inject.Inject

class GuestNoticeFragment : BaseFragment<HostFragmentGuestNoticeBinding, StepThreeViewModel>() {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    lateinit var mBinding: HostFragmentGuestNoticeBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.host_fragment_guest_notice
    override val viewModel: StepThreeViewModel
        get() = ViewModelProvider(
            baseActivity!!,
            mViewModelFactory
        ).get(StepThreeViewModel::class.java)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding!!
        mBinding.pgBar.progress = 30
        if (viewModel.isListAdded) {
            mBinding.tvRightsideText.text = getText(R.string.save_and_exit)
            mBinding.tvRightsideText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            )
            mBinding.tvRightsideText.setOnClickListener {
                it.disable()
                if (viewModel.checkPrice() && viewModel.checkDiscount() && viewModel.checkTripLength()&&viewModel.checkTax()) {

                    if (viewModel.fromChoosen == "Flexible" || viewModel.toChoosen == "Flexible") {
                        viewModel.retryCalled = "update"
                        viewModel.updateListStep3("edit")
                    } else {
                        if (viewModel.fromChoosen.toInt() >= viewModel.toChoosen.toInt()) {
                            viewModel.isSnackbarShown = true
                            showSnackbar(
                                getString(R.string.time),
                                getString(R.string.checkin_error_text)
                            )
                            it.enable()
                        } else {
                            viewModel.retryCalled = "update"
                            viewModel.updateListStep3("edit")
                        }
                    }
                }
            }
        } else {
            mBinding.tvRightsideText.visibility = View.GONE
            mBinding.chips.chip3.gone()
        }
        mBinding.guestNoticeToolbar.ivNavigateup.onClick { baseActivity?.onBackPressed() }

        subscribeToLiveData()
        observeOption()
    }

    fun subscribeToLiveData() {
        viewModel.listSettingArray.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (mBinding.rvGuestNotice.adapter != null) {
                    mBinding.rvGuestNotice.requestModelBuild()
                } else {
                    setUI()
                }
            }

        })
    }

    fun observeOption() {

        viewModel.listDetailsStep3.observe(viewLifecycleOwner, Observer {
            if (isAdded) {
                if (mBinding.rvGuestNotice.adapter != null) {
                    mBinding.rvGuestNotice.requestModelBuild()
                }
            }
        })
    }

    fun setUI() {
        setChips()
        mBinding.rvGuestNotice.withModels {
            viewModel.listDetailsStep3.value?.let { listDetailsStep3 ->
                viewholderUserName2 {
                    id("header")
                    name(getString(R.string.advance_notice_new))
                    isBgNeeded(true)
                    paddingBottom(true)
                    paddingTop(false)
                }

                viewholderUserNormalText {
                    id("checkText")
                    text(getString(R.string.when_guest_checkin))
                    paddingTop(false)
                    isBgNeeded(true)
                    paddingBottom(false)
                }

                viewholderListTv {
                    id("fromOptions")
                    hint(listDetailsStep3.noticeFrom)
                    etHeight(false)
                    maxLength(100)
                    onNoticeClick(View.OnClickListener {
                        OptionsSubFragment.newInstance("from").show(childFragmentManager, "from")
                    })
                }

                viewholderListTv {
                    id("toOptions")
                    hint(listDetailsStep3.noticeTo)
                    etHeight(false)
                    maxLength(100)
                    onNoticeClick(View.OnClickListener {
                        OptionsSubFragment.newInstance("to").show(childFragmentManager, "to")
                    })
                }

                viewholderUserNormalText {
                    id("cancellation")
                    text(getString(R.string.cancellation_policy))
                    paddingTop(false)
                    paddingBottom(false)
                    isBgNeeded(false)
                }
            }
            viewholderListTv {
                id("policy")
                hint(viewModel.listDetailsStep3.value?.cancellationPolicy)
                etHeight(false)
                maxLength(50)
                onNoticeClick(View.OnClickListener {
                    OptionsSubFragment.newInstance("policy").show(childFragmentManager, "policy")
                })
            }
            viewholderTips {
                id("tips5")
                when (viewModel.listDetailsStep3.value?.cancellationPolicy) {
                    getString(R.string.flexible) -> tips(getString(R.string.flexible_desc))
                    getString(R.string.moderate) -> tips(getString(R.string.moderate_desc))
                    getString(R.string.strict) -> tips(getString(R.string.strict_desc))

                }
            }
        }
    }

    private fun setChips() {
        mBinding.chips.apply {
            paddingTop = true
            guestReq = false
            houseRules = false
            reviewGuestBook = false
            advanceNotice = true
            bookingWindow = false
            minMaxNights = false
            pricing = false
            discount = false
            booking = false
            localLaws = false

            houseRulesClick = (View.OnClickListener {
                viewModel?.navigator?.navigateBack(StepThreeViewModel.BackScreen.HOUSERULE)
            })

            advanceNoticeClick = (View.OnClickListener {

            })
            guestReqClick = (View.OnClickListener {
                viewModel.navigator.navigateToScreen(StepThreeViewModel.NextStep.GUESTREQUEST)
            })
            reviewGuestBookClick = (View.OnClickListener {
                viewModel?.navigator?.navigateToScreen(StepThreeViewModel.NextStep.GUESTBOOK)
            })
            bookingWindowClick = (View.OnClickListener {
                viewModel?.navigator?.navigateToScreen(StepThreeViewModel.NextStep.BOOKWINDOW)
            })
            minMaxNightsClick = (View.OnClickListener {
                viewModel?.navigator?.navigateToScreen(StepThreeViewModel.NextStep.TRIPLENGTH)
            })
            pricingClick = (View.OnClickListener {
                viewModel?.navigator?.navigateToScreen(StepThreeViewModel.NextStep.LISTPRICE)
            })
            discountClick = (View.OnClickListener {
                viewModel?.navigator?.navigateToScreen(StepThreeViewModel.NextStep.DISCOUNTPRICE)
            })
            bookingClick = (View.OnClickListener {
                viewModel?.navigator?.navigateToScreen(StepThreeViewModel.NextStep.INSTANTBOOK)
            })
            localLawsClick = (View.OnClickListener {
                viewModel?.navigator?.navigateToScreen(StepThreeViewModel.NextStep.LAWS)
            })
        }
    }

    fun openFragment(fragment: androidx.fragment.app.Fragment, tag: String) {
        childFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_up,
                R.anim.slide_down,
                R.anim.slide_up,
                R.anim.slide_down
            )
            .add(mBinding.flSubFragment.id, fragment, tag)
            .addToBackStack(null)
            .commit()
    }

    override fun onRetry() {

    }
}