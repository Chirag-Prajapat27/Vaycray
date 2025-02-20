package com.halfeaten.vaycray.ui.payment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.airbnb.epoxy.databinding.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.FragmentPaymentDialogBinding
import com.halfeaten.vaycray.ui.base.BaseBottomSheet
import com.halfeaten.vaycray.util.binding.BindingAdapters
import com.halfeaten.vaycray.viewholderDivider
import com.halfeaten.vaycray.viewholderHostBottomOptions
import com.halfeaten.vaycray.viewholderOptionText
import javax.inject.Inject

class PaymentDialogOptionsFragment : BaseBottomSheet<FragmentPaymentDialogBinding,PaymentViewModel>(){

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_payment_dialog
    override val viewModel: PaymentViewModel
        get() = ViewModelProvider(baseActivity!!,mViewModelFactory).get(PaymentViewModel::class.java)
    lateinit var mBinding: FragmentPaymentDialogBinding
    var type: String = ""

    companion object {
        @JvmStatic
        fun newInstance(type: String) =
                PaymentDialogOptionsFragment().apply {
                    arguments = Bundle().apply {
                        putString("type", type)
                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding=viewDataBinding!!
        arguments?.let {
            type= it.getString("type")!!
        }
        setUp()
    }

    fun setUp(){
        mBinding.rvPaymentType.withModels {
                if(type=="paymentCurrency") {
                    val options = viewModel.currencies.value
                    options?.forEachIndexed { index, s ->
                            if (s!!.isPayment == true) {
                                viewholderOptionText {
                                    id("selected - $index")
                                    paddingTop(true)
                                    iconVisible(true)
                                    paddingBottom(true)
                                    desc(s.symbol)
                                    clickListener(View.OnClickListener {
                                        viewModel.selectedCurrency.set(s.symbol)
                                        dismiss()
                                    })
                                    if (viewModel.selectedCurrency.get()!!.equals(s.symbol)) {
                                        isSelected(true)
                                        txtColor(true)
                                    }else{
                                        isSelected(false)
                                        txtColor(false)
                                    }
                                }
                                viewholderDivider {
                                    id("divider - $index")
                                }
                            }
                    }
                }
        }
    }


    override fun onRetry() {
    }
}