package com.halfeaten.vaycray.ui.host.step_one

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.halfeaten.vaycray.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.HostFragmentTypeOfSpaceBinding
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.util.onClick
import com.halfeaten.vaycray.viewholderListTv
import com.halfeaten.vaycray.viewholderUserName2
import com.halfeaten.vaycray.viewholderUserNormalText
import javax.inject.Inject

class TypeOfSpaceFragment : BaseFragment<HostFragmentTypeOfSpaceBinding, StepOneViewModel>() {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.host_fragment_type_of_space
    override val viewModel: StepOneViewModel
        get() = ViewModelProvider(
            baseActivity!!,
            mViewModelFactory
        ).get(StepOneViewModel::class.java)
    lateinit var mBinding: HostFragmentTypeOfSpaceBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding!!
        viewModel.isEdit = false
        if (isAdded && baseActivity != null) {
            subscribeToLiveData()
            initView()
        }

    }

    private fun initView() {
        viewModel.roomType.observe(viewLifecycleOwner, Observer {
            requestModelBuildIt()
        })
        viewModel.capacity.observe(viewLifecycleOwner, Observer {
            requestModelBuildIt()
        })
        viewModel.becomeHostStep1.observe(viewLifecycleOwner, Observer {
            requestModelBuildIt()
        })
        mBinding.tvNext.onClick {
            viewModel.onContinueClick(StepOneViewModel.NextScreen.KIND_OF_PLACE)
        }
        mBinding.actionBar.ivNavigateup.onClick {
            baseActivity?.onBackPressed()
        }
    }

    fun requestModelBuildIt() {
        if (mBinding.rvStepOne.adapter != null) {
            mBinding.rvStepOne.requestModelBuild()
        }
    }

    private fun subscribeToLiveData() {
        viewModel.loadDefaultSettings().observe(viewLifecycleOwner, Observer {
            it?.let {
                try {
                    if (isAdded) {
                        if (mBinding.rvStepOne.adapter != null) {
                            mBinding.rvStepOne.requestModelBuild()
                        }
                        setUp()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun setUp() {
        viewModel.becomeHostStep1.value.let {
            mBinding.rvStepOne.withModels {
                viewholderUserName2 {
                    id("heading")
                    name(context?.resources?.getString(R.string.lets_get_ready))
                    paddingTop(false)
                    paddingBottom(true)
                    isBgNeeded(true)
                }

                viewholderUserNormalText {
                    id("what property")
                    text(context?.resources?.getString(R.string.what_kindof_place))
                    paddingTop(false)
                    paddingBottom(false)
                    isBgNeeded(true)
                }

                viewholderListTv {
                    id("place")
                    hint(viewModel.roomType.value!!)
                    isBgNeeded(true)
                    etHeight(false)
                    maxLength(50)
                    onNoticeClick(View.OnClickListener {
                        StepOneOptionsFragment.newInstance("placeOptions")
                            .show(childFragmentManager, "placeOptions")
                    })
                }
                viewholderUserNormalText {
                    id("what property2")
                    text(baseActivity!!.getString(R.string.how_many_guest_accom))
                    paddingTop(false)
                    paddingBottom(false)
                    isBgNeeded(false)
                }

                viewholderListTv {
                    id("guest")
                    hint(viewModel.capacity.value!!)
                    etHeight(false)
                    maxLength(50)
                    onNoticeClick(View.OnClickListener {
                        StepOneOptionsFragment.newInstance("guestOptions")
                            .show(childFragmentManager, "guestOptions")
                    })
                }
            }
        }
    }

    override fun onRetry() {

    }
}