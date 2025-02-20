package com.halfeaten.vaycray.host.payout.addPayout

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.halfeaten.vaycray.BR
import com.halfeaten.vaycray.GetCountrycodeQuery
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.FragmentAddPayoutIntroBinding
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.util.onClick
import javax.inject.Inject

class PaymentIntroFragment : BaseFragment<FragmentAddPayoutIntroBinding, AddPayoutViewModel>() {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_add_payout_intro
    override val viewModel: AddPayoutViewModel
        get() = ViewModelProvider(baseActivity!!, mViewModelFactory).get(AddPayoutViewModel::class.java)
    lateinit var mBinding: FragmentAddPayoutIntroBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding!!
        initView()
    }

    private fun initView() {
        mBinding.btnNext.onClick {
            viewModel.navigator.moveToScreen(AddPayoutActivity.Screen.INFO)
        }
    }

    override fun onRetry() {

    }
}