package com.halfeaten.vaycray.ui.listing.guest

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airbnb.epoxy.databinding.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.ActivityGuestBinding
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.ui.listing.ListingDetails
import com.halfeaten.vaycray.ui.listing.ListingDetailsViewModel
import com.halfeaten.vaycray.util.onClick
import javax.inject.Inject

class GuestFragment : BaseFragment<ActivityGuestBinding, ListingDetailsViewModel>() {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_guest
    override val viewModel: ListingDetailsViewModel
        get() = ViewModelProvider(baseActivity!!, mViewModelFactory).get(ListingDetailsViewModel::class.java)
    lateinit var mBinding: ActivityGuestBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding!!
        initView()
        subscribeToLiveData()
        if(activity is ListingDetails){
            (activity as ListingDetails).changeStatusBarColor(R.color.black)
        }

    }

    private fun initView() {
        mBinding.personCapacity1 = viewModel.personCapacity1
        viewModel.initialValue.value?.let {
            viewModel.personCapacity1.set(it.guestCount.toString())
        }
        mBinding.inlToolbar.ivNavigateup.onClick { baseActivity?.onBackPressed() }
        mBinding.inlToolbar.tvToolbarHeading.text=getString(R.string.edit_guests)
        mBinding.ibGuestMinus.setOnClickListener {
            viewModel.personCapacity1.get()?.let {
                viewModel.personCapacity1.set(it.toInt().minus(1).toString())
            }
        }
        mBinding.ibGuestPlus.setOnClickListener {
            viewModel.personCapacity1.get()?.let {
                viewModel.personCapacity1.set(it.toInt().plus(1).toString())
            }
        }
        mBinding.btnGuestSeeresult.onClick {
            try {
                baseActivity?.onBackPressed()
                val initialValues = viewModel.initialValue.value!!
                initialValues.guestCount = viewModel.personCapacity1.get()!!.toInt()
                viewModel.initialValue.value = initialValues
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun subscribeToLiveData() {
        viewModel.listingDetails.observe(viewLifecycleOwner, Observer {
            it?.let { listDetails ->
                mBinding.minusLimit1 = 1
                mBinding.plusLimit1 = listDetails.personCapacity
            }
        })
    }

    override fun onRetry() {

    }
    override fun onResume() {
        super.onResume()
        viewModel.clearStatusBar(requireActivity())
    }
}