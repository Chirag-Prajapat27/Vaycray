package com.halfeaten.vaycray.host.payout.addPayout

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.halfeaten.vaycray.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.FragmentAddPayoutAccountDetailsBinding
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.util.onClick
import com.halfeaten.vaycray.viewholderPayoutAccountDetails
import javax.inject.Inject

class PayoutAccountDetailFragment :
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
        viewModel.lastNameVisible.value = false
        initView()
        viewModel.lastNameVisible.observe(viewLifecycleOwner, Observer {
            it.let {
                mBinding.rlAddPayout.requestModelBuild()
            }
        })
    }

    private fun initView() {
        getString(R.string.individual)
        mBinding.btnNext.text = baseActivity!!.resources.getString(R.string.finish)
        mBinding.btnNext.onClick {
            checkDetails()
        }
        mBinding.rlAddPayout.withModels {
            viewholderPayoutAccountDetails {
                id("qwe")
                lastNameVisible(viewModel.lastNameVisible.value!!)
                firstName(viewModel.firstName)
                lastName(viewModel.lastName)
                accountNo(viewModel.account)
                cnfAccountNo(viewModel.cnfAccount)

                if (viewModel.europeCountries.contains(viewModel.countryCode.get())) {
                    accountHint(getString(R.string.iban_number))
                    confimrAccountHint(getString(R.string.iban_number))
                    isRoutingVisible(false)
                } else if (viewModel.countryCode.get() == "MX" || viewModel.countryCode.get() == "NZ") {
                    accountHint(getString(R.string.account_number))
                    confimrAccountHint(getString(R.string.confirm_acc_number))
                    isRoutingVisible(false)
                } else if (viewModel.countryCode.get() == "UK" || viewModel.countryCode.get() == "GB") {
                    accountHint(getString(R.string.account_number))
                    confimrAccountHint(getString(R.string.confirm_acc_number))
                    isRoutingVisible(true)
                    routingHint(getString(R.string.sort))
                } else {
                    accountHint(getString(R.string.account_number))
                    confimrAccountHint(getString(R.string.confirm_acc_number))
                    ifscNo(viewModel.ifscNumber)
                    ifscHint(getString(R.string.ifsc_code))
                }
                if (viewModel.country.get().equals("Canada")) {
                    countryCheck(true)
                    offsetPos(5)
                }

                //ssnNo(viewModel.ssn)
            }
        }
    }

    override fun onDestroy() {
        viewModel.firstName.set("")
        viewModel.lastName.set("")
        viewModel.account.set("")
        viewModel.cnfAccount.set("")
        viewModel.ifscNumber.set("")
        //viewModel.routingNo.set("")
        viewModel.ssn.set("")
        super.onDestroy()
    }

    fun checkDetails() {
        if (viewModel.checkAccountInfo() && viewModel.checkAccountDetails()) {
//            viewModel.getResultAccountToken(3 , viewModel.buildTokenHashMap())
            viewModel.addPayout(3)
        }
    }

    override fun onRetry() {
        checkDetails()
    }
}