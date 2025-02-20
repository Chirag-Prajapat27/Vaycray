package com.halfeaten.vaycray.ui.host.step_three.houseRules

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airbnb.epoxy.databinding.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.HostFragmentHouseRuleBinding
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.ui.host.step_three.StepThreeViewModel
import com.halfeaten.vaycray.util.disable
import com.halfeaten.vaycray.util.gone
import com.halfeaten.vaycray.util.onClick
import com.halfeaten.vaycray.viewholderBgBottomsheet
import com.halfeaten.vaycray.viewholderDivider
import com.halfeaten.vaycray.viewholderFilterCheckbox
import com.halfeaten.vaycray.viewholderUserName2
import javax.inject.Inject

class HouseRuleFragment : BaseFragment<HostFragmentHouseRuleBinding, StepThreeViewModel>() {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    lateinit var mBinding: HostFragmentHouseRuleBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.host_fragment_house_rule
    override val viewModel: StepThreeViewModel
        get() = ViewModelProvider(
            baseActivity!!,
            mViewModelFactory
        ).get(StepThreeViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding!!
        mBinding.pgBar.progress = 10
        if (viewModel.isListAdded) {
            mBinding.tvRightsideText.text = getText(R.string.save_and_exit)
            mBinding.tvRightsideText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            )
            mBinding.tvRightsideText.setOnClickListener {
                if (viewModel.checkPrice() && viewModel.checkDiscount() && viewModel.checkTripLength()&&viewModel.checkTax()) {
                    it.disable()
                    viewModel.retryCalled = "update"
                    viewModel.updateListStep3("edit")
                }
            }
        } else {
            mBinding.tvRightsideText.visibility = View.GONE
            mBinding.chips.chip3.gone()
        }
        mBinding.houseRuleToolbar.ivNavigateup.onClick {
            baseActivity?.onBackPressed()
        }

        subscribeToLiveData()
    }

    fun subscribeToLiveData() {
        viewModel.listSettingArray.observe(viewLifecycleOwner, Observer {
            it?.let { rulesList ->
                setUI()
            }

        })
    }

    fun setUI() {
        try {
            setChips()
            mBinding.rvHouseRule.withModels {
                viewholderUserName2 {
                    id("header")
                    name(getString(R.string.what_house))
                    isBgNeeded(true)
                    paddingTop(true)
                    paddingBottom(true)
                }

                viewholderBgBottomsheet { id("top") }
                val rules = viewModel.listSettingArray.value!!.houseRules!!.listSettings

                rules?.forEachIndexed { index, s ->
                    viewholderFilterCheckbox {
                        id("rule" + index)
                        text(s!!.itemName)
                        isChecked(viewModel.selectedRules.contains(s.id!!.toInt()))
                        onClick(View.OnClickListener {
                            if (viewModel.selectedRules.contains(s.id!!.toInt())) {
                                viewModel.selectedRules.removeAt(viewModel.selectedRules.indexOf(s.id))
                            } else {
                                viewModel.selectedRules.add(s.id!!.toInt())
                            }
                            this@withModels.requestModelBuild()
                        })
                    }
                    if (index != rules.lastIndex) {
                        viewholderDivider {
                            id("Divider - $index")
                        }
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
            showError()
        }
    }

    private fun setChips() {
        mBinding.chips.apply {
            paddingTop = true
            guestReq = false
            houseRules = true
            reviewGuestBook = false
            advanceNotice = false
            bookingWindow = false
            minMaxNights = false
            pricing = false
            discount = false
            booking = false
            localLaws = false

            houseRulesClick = (View.OnClickListener {

            })
            guestReqClick = (View.OnClickListener {
                viewModel?.navigator?.navigateToScreen(StepThreeViewModel.NextStep.GUESTREQUEST)
            })
            reviewGuestBookClick = (View.OnClickListener {
                viewModel?.navigator?.navigateToScreen(StepThreeViewModel.NextStep.GUESTBOOK)
            })
            advanceNoticeClick = (View.OnClickListener {
                viewModel?.navigator?.navigateToScreen(StepThreeViewModel.NextStep.GUESTNOTICE)
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

    override fun onRetry() {

    }
}