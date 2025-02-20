package com.halfeaten.vaycray.ui.host.step_three

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.airbnb.epoxy.databinding.BR
import com.halfeaten.vaycray.databinding.HostFragmentOptionsBinding
import com.halfeaten.vaycray.ui.base.BaseBottomSheet
import com.halfeaten.vaycray.util.binding.BindingAdapters
import com.halfeaten.vaycray.viewholderDivider
import com.halfeaten.vaycray.viewholderOptionText
import javax.inject.Inject

class OptionsSubFragment : BaseBottomSheet<HostFragmentOptionsBinding, StepThreeViewModel>() {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    lateinit var mBinding: HostFragmentOptionsBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = com.halfeaten.vaycray.R.layout.host_fragment_options
    override val viewModel: StepThreeViewModel
        get() = ViewModelProvider(
            baseActivity!!,
            mViewModelFactory
        ).get(StepThreeViewModel::class.java)


    var optionsArray = ArrayList<Boolean>()

    val availOptions =
        arrayOf("unavailable", "3months", "6months", "9months", "12months", "available")

    var type: String = ""

    companion object {
        @JvmStatic
        fun newInstance(type: String) =
            OptionsSubFragment().apply {
                arguments = Bundle().apply {
                    putString("type", type)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding!!

        arguments?.let {
            type = it.getString("type", "ert")
        }
        mBinding.rvOptions.setHasFixedSize(true)
        subscribeToLiveData()
    }

    fun subscribeToLiveData() {
        if (type.equals("options")) {
            val options = viewModel.listSettingArray.value!!.bookingNoticeTime!!.listSettings
            optionsArray.clear()
            mBinding.rvOptions.withModels {
                options?.forEachIndexed { index, s ->
                    optionsArray.add(false)
                    viewholderOptionText {
                        id("option" + index)
                        paddingBottom(true)
                        paddingTop(true)
                        iconVisible(true)
                        desc(s!!.itemName)
                        size(20.toFloat())
                        txtColor(optionsArray.get(index))
                        isSelected(optionsArray.get(index))
                        clickListener(View.OnClickListener {
                            val data = viewModel.listDetailsStep3.value
                            data?.noticePeriod = s.itemName
                            viewModel.listDetailsStep3.value = data
                            viewModel.noticeTime = s.id.toString()
                            dismiss()
                        })
                        if (viewModel.listDetailsStep3.value!!.noticeFrom!!.equals(s)) {
                            isSelected(true)
                            txtColor(true)
                        } else {
                            isSelected(false)
                            txtColor(false)
                        }
                    }
                    viewholderDivider {
                        id(index)
                    }
                }

            }
        } else if (type.equals("from")) {
            optionsArray.clear()
            mBinding.rvOptions.withModels {
                viewModel.fromOptions?.forEachIndexed { index, s ->
                    optionsArray.add(false)
                    viewholderOptionText {
                        id("from" + index)
                        paddingBottom(true)
                        paddingTop(true)
                        iconVisible(true)
                        desc(s)
                        txtColor(optionsArray.get(index))
                        isSelected(optionsArray.get(index))
                        size(20.toFloat())
                        clickListener(View.OnClickListener {
                            val data = viewModel.listDetailsStep3.value
                            data?.noticeFrom = s
                            viewModel.listDetailsStep3.value = data
                            viewModel.fromChoosen = viewModel.fromTime.get(index)
                            viewModel.noticeFrom = index.toString()
                            dismiss()
                        })
                        if (viewModel.listDetailsStep3.value!!.noticeFrom!!.equals(s)) {
                            isSelected(true)
                            txtColor(true)
                        } else {
                            isSelected(false)
                            txtColor(false)
                        }
                    }
                    viewholderDivider {
                        id(index)
                    }
                }

            }
        } else if (type.equals("to")) {
            optionsArray.clear()
            mBinding.rvOptions.withModels {
                viewModel.toOptions?.forEachIndexed { index, s ->
                    optionsArray.add(false)
                    viewholderOptionText {
                        id("to" + index)
                        paddingBottom(true)
                        paddingTop(true)
                        iconVisible(true)
                        desc(s)
                        txtColor(optionsArray.get(index))
                        isSelected(optionsArray.get(index))
                        size(20.toFloat())
                        clickListener(View.OnClickListener {
                            val data = viewModel.listDetailsStep3.value
                            data?.noticeTo = s
                            viewModel.listDetailsStep3.value = data

                            viewModel.toChoosen = viewModel.toTime.get(index)
                            viewModel.noticeTo = index.toString()
                            dismiss()

                        })
                        if (viewModel.listDetailsStep3.value!!.noticeTo.equals(s)) {
                            isSelected(true)
                            txtColor(true)
                        } else {
                            isSelected(false)
                            txtColor(false)
                        }
                    }
                    viewholderDivider {
                        id(index)
                    }
                }

            }
        } else if (type.equals("dates")) {
            optionsArray.clear()
            mBinding.rvOptions.withModels {

                viewModel.datesAvailable.forEachIndexed { index, s ->
                    optionsArray.add(false)
                    viewholderOptionText {
                        id("dates" + index)
                        paddingBottom(true)
                        paddingTop(true)
                        desc(s)
                        iconVisible(true)
                        txtColor(optionsArray.get(index))
                        isSelected(optionsArray.get(index))
                        size(20.toFloat())
                        clickListener(View.OnClickListener {
                            val data = viewModel.listDetailsStep3.value
                            data?.availableDate = s
                            viewModel.listDetailsStep3.value = data

                            viewModel.bookWind = viewModel.availOptions.get(index)
                            dismiss()
                        })
                        if (viewModel.listDetailsStep3.value!!.availableDate.equals(s)) {
                            isSelected(true)
                            txtColor(true)
                        } else {
                            isSelected(false)
                            txtColor(false)
                        }
                    }
                    viewholderDivider {
                        id(index)
                    }
                }

            }
        } else if (type.equals("policy")) {
            optionsArray.clear()
            mBinding.rvOptions.withModels {

                viewModel.cancellationPolicy.forEachIndexed { index, s ->
                    optionsArray.add(false)
                    viewholderOptionText {
                        id("policy" + index)
                        paddingBottom(true)
                        paddingTop(true)
                        iconVisible(true)
                        txtColor(optionsArray.get(index))
                        isSelected(optionsArray.get(index))
                        desc(s)
                        size(20.toFloat())
                        clickListener(View.OnClickListener {
                            val data = viewModel.listDetailsStep3.value
                            data?.cancellationPolicy = s
                            viewModel.listDetailsStep3.value = data

                            viewModel.cancelPolicy = index + 1
                            dismiss()
                        })
                        if (viewModel.listDetailsStep3.value!!.cancellationPolicy!!.equals(s)) {
                            isSelected(true)
                            txtColor(true)
                        } else {
                            isSelected(false)
                            txtColor(false)
                        }
                    }
                    viewholderDivider {
                        id(index)
                    }
                }

            }
        } else if (type.equals("price")) {

            mBinding.rvOptions.withModels {

                viewModel.currency.value!!.results!!.forEachIndexed { index, s ->
                    viewholderOptionText {
                        id("price" + index)
                        paddingBottom(true)
                        paddingTop(true)
                        iconVisible(true)

                        if (BindingAdapters.getCurrencySymbol(s!!.symbol) == s.symbol) {
                            desc(s.symbol)
                        } else {
                            desc(BindingAdapters.getCurrencySymbol(s.symbol) + " " + s.symbol)
                        }

                        isSelected(getoption(s.symbol.toString())!!)
                        txtColor(getoption(s.symbol.toString())!!)

                        size(20.toFloat())
                        clickListener(View.OnClickListener {

                            val data = viewModel.listDetailsStep3.value
                            data?.currency = s.symbol
                            viewModel.listDetailsStep3.value = data
                            dismiss()


                        })

                    }
                    viewholderDivider {
                        id(index)
                    }
                }

            }
        }

    }

    private fun getoption(symbol: String): Boolean? {

        var currency = ""
        if (viewModel.listDetailsStep3.value!!.currency.toString().split(" ").size > 1)
            currency =
                viewModel.listDetailsStep3.value!!.currency.toString().split(" ")[1].toString()
        else
            currency = viewModel.listDetailsStep3.value!!.currency.toString()

        return currency.equals(symbol)
    }


    override fun onRetry() {

    }

}