package com.halfeaten.vaycray.host.payout.addPayout

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.halfeaten.vaycray.BR
import com.halfeaten.vaycray.GetPaymentMethodsQuery
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.FragmentAddPayoutAccountDetailsBinding
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.util.gone
import com.halfeaten.vaycray.viewholderPayoutChooseHowWePay
import javax.inject.Inject

class PaymentTypeFragment :
    BaseFragment<FragmentAddPayoutAccountDetailsBinding, AddPayoutViewModel>() {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_add_payout_account_details
    override val viewModel: AddPayoutViewModel
        get() = ViewModelProvider(
            baseActivity!!,
            mViewModelFactory
        ).get(AddPayoutViewModel::class.java)
    lateinit var mBinding: FragmentAddPayoutAccountDetailsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding!!
        initView()
        subscribeToLiveData()
    }

    private fun initView() {
        mBinding.btnNext.gone()
    }

    private fun subscribeToLiveData() {
        viewModel.loadPayoutMethods().observe(viewLifecycleOwner, Observer {
            it?.let { result ->
                setup(result)
            }
        })
        hideKeyboard()
    }

    private fun setup(result: List<GetPaymentMethodsQuery.Result?>) {
        mBinding.rlAddPayout.withModels {
            result.forEach {
                if (it?.isEnable!!) {
                    viewholderPayoutChooseHowWePay {
                        id(it.id)
                        info(it.details)
                        currency(
                            baseActivity!!.resources.getString(R.string.currency) + ": [" + (it.currency
                                ?: "USD") + "]"
                        )
                        fees(baseActivity!!.resources.getString(R.string.fees) + ": ${it.fees}")
                        processingTime(baseActivity!!.resources.getString(R.string.processed_in) + ": " + it.processedIn)
                        if (it.id == 1) {
                            paymentType(baseActivity!!.resources.getString(R.string.paypal))
                        } else {
                            paymentType(baseActivity!!.resources.getString(R.string.bank_account))
                        }
                        clickListener { _ ->
                            viewModel.currency.set(it.currency)
                            if (it.paymentType == 1) {
                                viewModel.navigator.moveToScreen(AddPayoutActivity.Screen.PAYPALDETAILS)
                            } else if (it.paymentType == 2) {
                                if (viewModel.isDOB) {
                                    viewModel.navigator.moveToScreen(AddPayoutActivity.Screen.PAYOUTDETAILS)
                                } else {
                                    showSnackbar(
                                        baseActivity!!.resources.getString(R.string.dob),
                                        baseActivity!!.resources.getString(R.string.dob_msg)
                                    )
                                }
                            } else {
                                if (viewModel.isDOB) {
                                    viewModel.navigator.moveToScreen(AddPayoutActivity.Screen.PAYOUTDETAILS)
                                } else {
                                    showSnackbar(
                                        baseActivity!!.resources.getString(R.string.dob),
                                        baseActivity!!.resources.getString(R.string.dob_msg)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onRetry() {

    }
}