package com.halfeaten.vaycray.host.payout.addPayout

import com.halfeaten.vaycray.ui.host.step_three.StepThreeViewModel


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.airbnb.epoxy.databinding.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.HostFragmentOptionsBinding
import com.halfeaten.vaycray.ui.base.BaseBottomSheet
import com.halfeaten.vaycray.util.withModels
import com.halfeaten.vaycray.viewholderDivider
import com.halfeaten.vaycray.viewholderOptionText
import javax.inject.Inject

class BottomSheetFragment : BaseBottomSheet<HostFragmentOptionsBinding, AddPayoutViewModel>(){

    @Inject lateinit var mViewModelFactory : ViewModelProvider.Factory
    lateinit var mBinding : HostFragmentOptionsBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = com.halfeaten.vaycray.R.layout.host_fragment_options
    override val viewModel: AddPayoutViewModel
        get() = ViewModelProvider(baseActivity!!,mViewModelFactory).get(AddPayoutViewModel::class.java)



    private var isSelected = false

    var selectedValue : Int? = null

    var optionsArray = ArrayList<Boolean>()

    var type : String = ""

    companion object {
        @JvmStatic
        fun newInstance(type : String) =
                BottomSheetFragment().apply {
                    arguments = Bundle().apply {
                        putString("type", type)
                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding=viewDataBinding!!
        arguments?.let {
            type=it.getString("type", "ert")
        }
        mBinding.rvOptions.setHasFixedSize(true)
        subscribeToLiveData()
    }

    fun subscribeToLiveData(){
            val options = arrayOf(getString(R.string.individual), getString(R.string.company))
            optionsArray.clear()
            mBinding.rvOptions.withModels {
                options?.forEachIndexed { index, s ->
                    optionsArray.add(false)
                    viewholderOptionText {
                        id("option" + index)
                        paddingBottom(true)
                        paddingTop(true)
                        desc(s)
                        size(20.toFloat())
                        iconVisible(true)

                        clickListener(View.OnClickListener {
                            //viewModel.accountType.set(s)
                            if(s.equals(getString(R.string.individual))){
                                viewModel.listPref.set(s)
                                viewModel.lastNameVisible.value = false
                            }else{
                                viewModel.listPref.set(s)
                                viewModel.lastNameVisible.value = true
                            }
                            dismiss()
                        })
                        if (viewModel.listPref.get().equals(s)) {
                            isSelected(true)
                            txtColor(true)
                        }else{
                            isSelected(false)
                            txtColor(false)
                        }
                    }
                    viewholderDivider {
                        id(index)
                    }
                }

            }
    }


    override fun onRetry() {

    }

}