package com.halfeaten.vaycray.ui.listing.cancellation

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.databinding.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.FragmentListingAmenitiesBinding
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.ui.listing.ListingDetails
import com.halfeaten.vaycray.ui.listing.ListingDetailsViewModel
import com.halfeaten.vaycray.util.Utils
import com.halfeaten.vaycray.util.gone
import com.halfeaten.vaycray.util.onClick
import com.halfeaten.vaycray.util.visible
import com.halfeaten.vaycray.viewholderCancellationPolicy
import com.halfeaten.vaycray.viewholderHeader
import com.halfeaten.vaycray.viewholderListingDetailsCancellation
import com.halfeaten.vaycray.viewholderListingDetailsHeader
import com.halfeaten.vaycray.viewholderReviewAndPaySpanText
import com.halfeaten.vaycray.viewholderUserHeadingSmall
import javax.inject.Inject

class CancellationFragment: BaseFragment<FragmentListingAmenitiesBinding, ListingDetailsViewModel>() {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_listing_amenities
    override val viewModel: ListingDetailsViewModel
        get() = ViewModelProvider(baseActivity!!, mViewModelFactory).get(ListingDetailsViewModel::class.java)
    lateinit var mBinding: FragmentListingAmenitiesBinding
    val list = ArrayList<CancellationState>()
    var policyName=""
     var lang=""
    data class CancellationState(val desc: String,val descdate: String, val date: String, val day: String, val content: String)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding!!
        initView()
        subscribeToLiveData()
    }

    private fun initView() {
        mBinding.ivClose.visible()
        mBinding.ivClose.onClick { baseActivity?.onBackPressed() }
        mBinding.rlListingAmenities.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        mBinding.rlShowresult.gone()
    }

    private fun subscribeToLiveData() {
        viewModel.listingDetails.observe(viewLifecycleOwner, Observer { it?.let { details ->

            policyName= details.listingData?.cancellation?.policyName!!
            when (details.listingData?.cancellation?.policyName) {
                getString(R.string.flexible) -> generateFlexibleItems()
               getString(R.string.moderate) -> generateModerateItems()
                getString(R.string.strict) -> generateStrictItems()
            }
            initEpoxy()
        } })
    }

    private fun generateModerateItems() {
        list.add(CancellationState(
                getString(R.string.moderate_desc),getString(R.string.moderate_descday),
           getString(R.string.moderate_date),
                getString(R.string.moderate_dayy),
                getString(R.string.moderate_content)))
        list.add(CancellationState(
            "","",
                resources.getString(R.string.check_in),
                getString(R.string.moderate_checkin_day),
                getString(R.string.moderate_checkin_content)))
        list.add(CancellationState(
            "","",
                resources.getString(R.string.check_out),
                "",
                getString(R.string.moderate_checkout_content)))
    }

    private fun generateStrictItems() {
        list.add(CancellationState(
            getString(R.string.strict_desc),getString(R.string.strict_descday),
                getString(R.string.strict_date),
                getString(R.string.strict_dayy),
                getString(R.string.strict_content)))
        list.add(CancellationState(
            "","",
                resources.getString(R.string.check_in),
                getString(R.string.strict_checkin_day),
                getString(R.string.strict_checkin_content)))
        list.add(CancellationState(
            "","",
                resources.getString(R.string.check_out),
            "",
                getString(R.string.strict_checkout_content)))
    }

    private fun generateFlexibleItems() {
        list.add(CancellationState(
            getString(R.string.flexible_desc),getString(R.string.flexible_descday),
               getString(R.string.flexible_date),
                getString(R.string.flexible_dayy),
                getString(R.string.flexible_content)))
        list.add(CancellationState(
            "","",
                resources.getString(R.string.check_in),
                getString(R.string.flexible_checkin_day),
                getString(R.string.flexible_checkin_content)))
        list.add(CancellationState(
            "","",
                resources.getString(R.string.check_out),
                "",
                getString(R.string.flexible_checkout_content)))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initEpoxy() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
         lang= preferences.getString("Locale.Helper.Selected.Language", "en").toString()
        Log.d("langtype",lang)
        mBinding.rlListingAmenities.withModels {
            viewholderHeader {
                id("header")
                header(resources.getString(R.string.cancellation_policy))
            }


            viewholderReviewAndPaySpanText {
                id("DetailsDesc - CancellationContent")
                var name=baseActivity!!.resources.getString(R.string.cancellation_policy_title_part1)+" '"+viewModel.listingDetails.value!!.listingData?.cancellation?.policyName+"' "+baseActivity!!.resources.getString(R.string.cancellation_policy_title_part2)+" "+viewModel.listingDetails.value!!.listingData?.cancellation?.policyContent

                var content=baseActivity!!.resources.getString(R.string.cancellation_policy_title_part1)+" '"+viewModel.listingDetails.value!!.listingData?.cancellation?.policyName+"' "
                var contentStart=baseActivity!!.resources.getString(R.string.cancellation_policy_title_part1)+" '"

                var end=content.length
                var start=contentStart.length
                onBind { model, view, position ->


                    var hintText=view.dataBinding.root.findViewById<TextView>(R.id.desc1)
                    var spString = SpannableString(name)
                    spString.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorPrimary)), start.minus(1), end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    hintText.text=spString


                }
            }
            viewholderListingDetailsHeader {
                id("example")
                header(policyName)
                isBlack(true)
                large(false)
                typeface(Typeface.DEFAULT_BOLD)
            }
            list.forEachIndexed { index, item ->
                viewholderListingDetailsCancellation {
                    if (index==0){
                        id(index)
                        desc(item.desc)
                        descvisiblity(true)
                        descdate(item.descdate)
                        date(item.date)
                        day(item.day)
                        content(item.content)

                    }else {
                        if (index==2){
                            datevisiblity(true)
                            id(index)
                            desc(item.desc)
                            descdate(item.descdate)
                            date(item.date)
                            day(item.day)
                            padding(true)
                            content(item.content)
                        }else {
                            id(index)
                            desc(item.desc)
                            descdate(item.descdate)
                            date(item.date)
                            day(item.day)
                            content(item.content)
                        }
                    }
                }
            }
            viewholderUserHeadingSmall {
                id(11)
                text("")
            }
            viewholderCancellationPolicy {
                id("cancellation policy")
                text(resources.getString(R.string.cancellation_policy_points))
            }
            viewholderCancellationPolicy {
                id("cancellation policy")
                text(resources.getString(R.string.cancellation_policy_points1))
            }
            viewholderCancellationPolicy {
                id("cancellation policy1")
                text(resources.getString(R.string.cancellation_policy_points2))
            }
            viewholderCancellationPolicy {
                id("cancellation policy2")
                text(resources.getString(R.string.cancellation_policy_points3))
            }
            viewholderCancellationPolicy {
                id("cancellation policy3")
                text(resources.getString(R.string.cancellation_policy_points4))
            }
            viewholderCancellationPolicy {
                id("cancellation policy4")
                text(resources.getString(R.string.cancellation_policy_points5))
            }
            viewholderCancellationPolicy {
                id("cancellation policy5")
                text(resources.getString(R.string.cancellation_policy_points6))
            }
            viewholderCancellationPolicy {
                id("cancellation policy6")
                text(resources.getString(R.string.cancellation_policy_points7))
            }



        }
    }

    override fun onRetry() {

    }
    override fun onResume() {
        super.onResume()
        viewModel.clearStatusBar(requireActivity())
    }
}