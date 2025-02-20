package com.halfeaten.vaycray.ui.host.step_three.listingPrice

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airbnb.epoxy.databinding.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.HostFragmentListPriceBinding
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.ui.host.step_three.OptionsSubFragment
import com.halfeaten.vaycray.ui.host.step_three.StepThreeViewModel
import com.halfeaten.vaycray.util.binding.BindingAdapters
import com.halfeaten.vaycray.util.disable
import com.halfeaten.vaycray.util.gone
import com.halfeaten.vaycray.util.onClick
import com.halfeaten.vaycray.viewholderDiscount
import com.halfeaten.vaycray.viewholderListNumEt
import com.halfeaten.vaycray.viewholderListTv
import com.halfeaten.vaycray.viewholderTips
import com.halfeaten.vaycray.viewholderUserName2
import com.halfeaten.vaycray.viewholderUserNormalText
import java.util.Locale
import javax.inject.Inject

class ListingPriceFragment : BaseFragment<HostFragmentListPriceBinding,StepThreeViewModel>(){

    @Inject lateinit var mViewModelFactory : ViewModelProvider.Factory
    lateinit var mBinding : HostFragmentListPriceBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.host_fragment_list_price
    override val viewModel: StepThreeViewModel
        get() = ViewModelProvider(baseActivity!!,mViewModelFactory).get(StepThreeViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding=viewDataBinding!!
        mBinding.pgBar.progress = 30
        mBinding.listPriceToolbar.rlToolbarRightside.gone()
        if(viewModel.isListAdded) {
            mBinding.tvRightsideText.text = getText(R.string.save_and_exit)
            mBinding.tvRightsideText.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            mBinding.tvRightsideText.setOnClickListener {
                if(viewModel.checkPrice() && viewModel.checkDiscount()  && viewModel.checkTripLength()&&viewModel.checkTax()){
                    it.disable()
                    viewModel.retryCalled = "update"
                    viewModel.updateListStep3("edit")
                }
            }
        }else{
            mBinding.tvRightsideText.visibility = View.GONE
            mBinding.chips.chip3.gone()
        }
        mBinding.listPriceToolbar.ivNavigateup.onClick {
                baseActivity!!.onBackPressed()
        }

        subscribeToLiveData()
        obserbeData()
    }

    fun obserbeData(){
        viewModel.listDetailsStep3.observe(viewLifecycleOwner, Observer {
            if(isAdded) {
                if (mBinding.rvListPrice.adapter != null) {
                    if(viewModel.isSnackbarShown){
                        hideSnackbar()
                        viewModel.isSnackbarShown = false
                    }
                    mBinding.rvListPrice.requestModelBuild()
                }
            }
        })

    }

    fun subscribeToLiveData(){

        setChips()
        mBinding.rvListPrice.withModels {
            viewholderUserName2 {
                id("header")
                name(getString(R.string.pricing))
                isBgNeeded(true)
                paddingBottom(true)
                paddingTop(true)
            }

            viewholderUserNormalText {
                id("currency")
                text(getString(R.string.currency))
                paddingTop(false)
                isBgNeeded(true)
                paddingBottom(false)
            }

            viewholderListTv {
                id("noticeOption")
                if (BindingAdapters.getCurrencySymbol(viewModel.listDetailsStep3.value!!.currency)==viewModel.listDetailsStep3.value!!.currency){
                    hint( viewModel.listDetailsStep3.value!!.currency)
                }else{
                    hint( BindingAdapters.getCurrencySymbol(viewModel.listDetailsStep3.value!!.currency)+" "+viewModel.listDetailsStep3.value!!.currency)
                }
                etHeight(false)
                maxLength(50)
                onNoticeClick(View.OnClickListener {
                    OptionsSubFragment.newInstance("price").show(childFragmentManager, "price")
                    Handler(Looper.getMainLooper()).postDelayed({
                        this@withModels.requestModelBuild()
                    }, 1000)

                })
            }
            viewholderListNumEt {
                id("priceEt")
                text(viewModel.basePrice)
                hint(getString(R.string.base_price_hint))
                inputType(true)
                paddingBottom(true)
                title(getString(R.string.base_price))
            }
            viewholderTips {
                id("tips6")
                paddingBottom(true)
                tips(getString(R.string.tips_six))
            }

            viewholderListNumEt {
                id("cleanEt")
                paddingBottom(true)
                title(getString(R.string.cleaning_price_hint))
                inputType(true)
                text(viewModel.cleaningPrice)
                hint(getString(R.string.cleaning_price_hint))
            }

            viewholderDiscount {
                id("taxes")
                hint(getString(R.string.taxes))
                title(getString(R.string.taxes))
                inputType(false)
                paddingBottom(true)
                text(viewModel.TaxPrice)
                onBind { model, view, position ->
                    val textView=view.dataBinding.root.findViewById<TextView>(R.id.discount)
                    val editText=view.dataBinding.root.findViewById<TextView>(R.id.et_host_edit)
                    editText.filters = arrayOf(InputFilter.LengthFilter(2))
                    val isRTL = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL
                    if (isRTL) {
                        textView.scaleX = -1f
                        textView.textScaleX = -1f
                    } else {
                        textView.scaleX = 1f
                        textView.textScaleX = 1f
                    }

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
            advanceNotice = false
            bookingWindow = false
            minMaxNights = false
            pricing = true
            discount = false
            booking = false
            localLaws = false


            advanceNoticeClick = (View.OnClickListener {
                viewModel?.navigator?.navigateBack(StepThreeViewModel.BackScreen.GUESTNOTICE)
            })
            houseRulesClick = (View.OnClickListener {
                viewModel?.navigator?.navigateBack(StepThreeViewModel.BackScreen.HOUSERULE)
            })
            pricingClick = (View.OnClickListener {

            })
            reviewGuestBookClick = (View.OnClickListener {
                viewModel?.navigator?.navigateToScreen(StepThreeViewModel.NextStep.GUESTBOOK)
            })
            guestReqClick = (View.OnClickListener {
                viewModel?.navigator?.navigateToScreen(StepThreeViewModel.NextStep.GUESTREQUEST)
            })

            bookingWindowClick = (View.OnClickListener {
                viewModel?.navigator?.navigateToScreen(StepThreeViewModel.NextStep.BOOKWINDOW)
            })
            minMaxNightsClick = (View.OnClickListener {
                viewModel?.navigator?.navigateToScreen(StepThreeViewModel.NextStep.TRIPLENGTH)
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
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
                .add(mBinding.flSubFragment.id, fragment, tag)
                .addToBackStack(null)
                .commit()
    }

    override fun onRetry() {

    }
}