package com.halfeaten.vaycray.host.payout.addPayout

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.halfeaten.vaycray.BR
import com.halfeaten.vaycray.Constants
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.FragmentAddPayoutAccountDetailsBinding
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.util.onClick
import com.halfeaten.vaycray.viewholderPayoutAccountInfo
import com.stripe.android.Stripe
import javax.inject.Inject

class PayoutAccountInfoFragment :
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
        viewModel.stripe = Stripe(requireContext(), Constants.stripePublishableKey)
        initView()
    }

    private fun initView() {
        mBinding.btnNext.text = baseActivity!!.resources.getString(R.string.next)
        mBinding.btnNext.onClick {
            if (viewModel.checkAccountInfo()) {
                viewModel.navigator.moveToScreen(AddPayoutActivity.Screen.PAYOUTTYPE)
            }
        }
        mBinding.rlAddPayout.withModels {
            viewholderPayoutAccountInfo {
                id(14)
                country(viewModel.country.get())
                city(viewModel.city)
                address1(viewModel.address1)
                address2(viewModel.address2)
                state(viewModel.state)
                zip(viewModel.zip)
            }
        }
    }


    override fun onRetry() {

    }
}