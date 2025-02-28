package com.halfeaten.vaycray.ui.host.step_three.tripLength

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airbnb.epoxy.databinding.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.HostFragmentTripLengthBinding
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.ui.host.step_three.StepThreeViewModel
import com.halfeaten.vaycray.util.disable
import com.halfeaten.vaycray.util.gone
import com.halfeaten.vaycray.util.onClick
import com.halfeaten.vaycray.viewholderHostPlusMinus
import com.halfeaten.vaycray.viewholderUserName2
import javax.inject.Inject

class TripLengthFragment : BaseFragment<HostFragmentTripLengthBinding, StepThreeViewModel>() {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    lateinit var mBinding: HostFragmentTripLengthBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.host_fragment_trip_length
    override val viewModel: StepThreeViewModel
        get() = ViewModelProvider(
            baseActivity!!,
            mViewModelFactory
        ).get(StepThreeViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding!!
        mBinding.pgBar.progress = 60
        mBinding.tripLengthToolbar.rlToolbarRightside.gone()
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
        mBinding.tripLengthToolbar.ivNavigateup.onClick { baseActivity?.onBackPressed() }
        subscribeToLiveData()
        observeData()
    }

    fun subscribeToLiveData() {
        try {
            setChips()
            mBinding.rvTripLength.withModels {
                viewholderUserName2 {
                    id("header")
                    name(getString(R.string.trip_length))
                    paddingTop(true)
                    paddingBottom(true)
                    isBgNeeded(true)
                }

                viewholderHostPlusMinus {
                    id("min")
                    text(getString(R.string.min_stay))
                    minusLimit1(
                        viewModel.listSettingArray!!.value!!.minNight!!
                            .listSettings!![0]?.startValue
                    )
                    plusLimit1(
                        viewModel.listSettingArray!!.value!!.minNight!!
                            .listSettings!![0]?.endValue
                    )
                    paddingTop(true)
                    paddingBottom(true)
                    personCapacity1(viewModel.minNight.get())
                    isBgNeeded(true)
                    clickMinus(View.OnClickListener {
                        viewModel.minNight.get()?.let {
                            viewModel.minNight.set(it.toInt().minus(1).toString())
                            val data = viewModel.listDetailsStep3.value
                            data?.minStay = viewModel.minNight.get()
                            viewModel.listDetailsStep3.value = data
                        }
                    })
                    clickPlus(View.OnClickListener {
                        viewModel.minNight.get()?.let {
                            viewModel.minNight.set(it.toInt().plus(1).toString())
                            val data = viewModel.listDetailsStep3.value
                            data?.minStay = viewModel.minNight.get()
                            viewModel.listDetailsStep3.value = data
                        }
                    })
                }

                viewholderHostPlusMinus {
                    id("max")
                    text(getString(R.string.max_stay))
                    minusLimit1(
                        viewModel.listSettingArray!!.value!!.maxNight!!
                            .listSettings!![0]?.startValue
                    )
                    plusLimit1(
                        viewModel.listSettingArray!!.value!!.maxNight!!
                            .listSettings!![0]?.endValue
                    )
                    personCapacity1(viewModel.maxNight.get())
                    paddingTop(true)
                    paddingBottom(true)
                    clickMinus(View.OnClickListener {
                        viewModel.maxNight.get()?.let {
                            viewModel.maxNight.set(it.toInt().minus(1).toString())
                            val data = viewModel.listDetailsStep3.value
                            data?.maxStay = viewModel.maxNight.get()
                            viewModel.listDetailsStep3.value = data
                        }
                    })
                    clickPlus(View.OnClickListener {
                        viewModel.maxNight.get()?.let {
                            viewModel.maxNight.set(it.toInt().plus(1).toString())
                            val data = viewModel.listDetailsStep3.value
                            data?.maxStay = viewModel.maxNight.get()
                            viewModel.listDetailsStep3.value = data
                        }
                    })
                }

            }
        } catch (E: Exception) {
            showError()
        }
    }

    private fun setChips() {
        mBinding.chips.apply {
            paddingTop = true
            guestReq = false
            houseRules = false
            reviewGuestBook = false
            advanceNotice = false
            bookingWindow = false
            minMaxNights = true
            pricing = false
            discount = false
            booking = false
            localLaws = false
            guestReqClick = (View.OnClickListener {
                viewModel?.navigator?.navigateBack(StepThreeViewModel.BackScreen.GUESTREQUEST)
            })
            houseRulesClick = (View.OnClickListener {
                viewModel?.navigator?.navigateBack(StepThreeViewModel.BackScreen.HOUSERULE)
            })
            reviewGuestBookClick = (View.OnClickListener {
                viewModel?.navigator?.navigateBack(StepThreeViewModel.BackScreen.GUESTBOOK)
            })
            advanceNoticeClick = (View.OnClickListener {
                viewModel?.navigator?.navigateBack(StepThreeViewModel.BackScreen.GUESTNOTICE)
            })
            bookingWindowClick = (View.OnClickListener {
                viewModel?.navigator?.navigateBack(StepThreeViewModel.BackScreen.BOOKWINDOW)
            })
            minMaxNightsClick = (View.OnClickListener {

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

    fun observeData() {
        viewModel.listDetailsStep3.observe(viewLifecycleOwner, Observer {
            if (isAdded) {
                if (mBinding.rvTripLength.adapter != null) {
                    if (viewModel.isSnackbarShown) {
                        hideSnackbar()
                        viewModel.isSnackbarShown = false
                    }
                    mBinding.rvTripLength.requestModelBuild()
                }
            }
        })
    }

    override fun onRetry() {

    }
}