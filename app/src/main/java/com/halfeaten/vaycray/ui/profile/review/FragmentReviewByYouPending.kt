package com.halfeaten.vaycray.ui.profile.review

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import com.airbnb.epoxy.databinding.BR
import com.halfeaten.vaycray.GetPendingUserReviewsQuery
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.data.remote.paging.NetworkState
import com.halfeaten.vaycray.data.remote.paging.Status
import com.halfeaten.vaycray.databinding.FragmentReviewBinding
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.ui.profile.review.controller.PendingReviewsListController
import com.halfeaten.vaycray.util.epoxy.CustomSpringAnimation
import com.halfeaten.vaycray.util.gone
import com.halfeaten.vaycray.util.visible
import javax.inject.Inject

class FragmentReviewByYouPending : BaseFragment<FragmentReviewBinding, ReviewViewModel>(){
    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    lateinit var mBinding: FragmentReviewBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_review
    override val viewModel: ReviewViewModel
        get() = ViewModelProvider(baseActivity!!,mViewModelFactory).get(ReviewViewModel::class.java)
    private lateinit var pagingController: PendingReviewsListController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding=viewDataBinding!!
        mBinding.shimmerUpcoming.visible()
        mBinding.shimmerUpcoming.startShimmer()
        pagingController = PendingReviewsListController(this.requireContext(),viewModel)
        CustomSpringAnimation.spring(mBinding.rvReviewList)
        subscribeToLiveData()
        viewModel.reloadData.observe(this, Observer {
            it?.let {
                if ((it != "")) {
                    mBinding.shimmerUpcoming.visible()
                    viewModel.onRefreshPending()
                    viewModel.reloadData.value= ""
                }
            }
        })
        viewModel.networkStatePending.observe(this, Observer {
            it?.let { networkState ->
                when (networkState) {
                    NetworkState.SUCCESSNODATA -> {
                        mBinding.ltLoadingView.gone()
                        mBinding.shimmerUpcoming.gone()
                        mBinding.rvReviewList.gone()
                        mBinding.relNoReviews.visible()
                    }
                    NetworkState.LOADING -> {
                        mBinding.ltLoadingView.visible()
                        mBinding.shimmerUpcoming.visible()
                        mBinding.rvReviewList.gone()
                        mBinding.relNoReviews.gone()
                    }
                    NetworkState.LOADED -> {
                        mBinding.rvReviewList.visible()
                        mBinding.shimmerUpcoming.gone()
                        mBinding.ltLoadingView.gone()
                        mBinding.relNoReviews.gone()
                        mBinding.rvReviewList.visible()
                    }
                    else -> {
                        if (networkState.status == Status.FAILED) {
                            it.msg?.let {error ->
                                viewModel.handleException(error)
                            } ?: viewModel.handleException(Throwable())
                        }
                    }
                }
            }
        })
    }


    fun subscribeToLiveData(){
        viewModel.loadByYouListPending().observe(this, Observer<PagedList<GetPendingUserReviewsQuery.Result>>{
            it?.let {
                if(mBinding.rvReviewList.adapter==pagingController.adapter){
                    pagingController.submitList(it)
                }else{
                    mBinding.rvReviewList.adapter = pagingController.adapter
                    pagingController.submitList(it)
                }
            }
        })
    }


    override fun onRetry() {
    }
}