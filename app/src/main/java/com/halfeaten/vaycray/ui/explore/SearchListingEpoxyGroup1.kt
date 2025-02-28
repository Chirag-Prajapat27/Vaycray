package com.halfeaten.vaycray.ui.explore

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ImageSpan
import android.text.style.TypefaceSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.PagerSnapHelper
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelGroup
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.ViewholderExploreSearchListingItemBindingModel_
import com.halfeaten.vaycray.ViewholderHeartSavedBindingModel_
import com.halfeaten.vaycray.ViewholderListingDetailsCarouselBindingModel_
import com.halfeaten.vaycray.util.CurrencyUtil
import com.halfeaten.vaycray.util.CustomUnderlineTextView
import com.halfeaten.vaycray.util.Utils
import com.halfeaten.vaycray.util.binding.BindingAdapters
import com.halfeaten.vaycray.util.epoxy.ListingPhotosCarouselModel_
import com.halfeaten.vaycray.vo.ListingInitData
import com.halfeaten.vaycray.vo.SearchListing
import timber.log.Timber
import kotlin.math.round

class SearchListingEpoxyGroup1(
    val item: SearchListing,
    listingInitData: ListingInitData,
    val clickListener: (it: View, item: SearchListing, listingInitData: ListingInitData, view: View) -> Unit,
    val onWishListClick: (item: SearchListing, listingInitData: ListingInitData) -> Unit,
    val priceClickListener:(item: SearchListing,view:View,listingInitData: ListingInitData)->Unit,
    val isoneTotalpriceChecked:Boolean
) : EpoxyModelGroup(
    R.layout.model_carousel_group,
    buildModels1(item, listingInitData, clickListener, onWishListClick,priceClickListener,isoneTotalpriceChecked)
) {

    init {
        id("SearchListing - ${item.id}")
    }
}


fun buildModels1(
    item: SearchListing, listingInitData: ListingInitData,
    clickListener: (it2: View, item: SearchListing, listingInitData: ListingInitData, view: View) -> Unit,
    onWishListClick: (item: SearchListing, listingInitData: ListingInitData) -> Unit,
    priceClickListener:(item: SearchListing,view:View,listingInitData: ListingInitData)->Unit,
    isoneTotalpriceChecked:Boolean
): List<EpoxyModel<*>> {
    val models = ArrayList<EpoxyModel<*>>()
    try {
        val convertedPrice = getCurrencyRate1(item, listingInitData)
        val images = ArrayList<String>()




        item.listPhotoName?.let {
            images.add(it)
        }

        item.listPhotos.forEach {
            if (images.contains(it.name).not()) {
                it.name.let { name ->
                    images.add(name)
                }
            }
        }

        models.add(ListingPhotosCarouselModel_().apply {
            Carousel.setDefaultGlobalSnapHelperFactory(object : Carousel.SnapHelperFactory() {
                override fun buildSnapHelper(context: Context?): androidx.recyclerview.widget.SnapHelper {
                    return PagerSnapHelper()
                }
            })
            id("Carousel - ${item.id}")
            padding(Carousel.Padding.dp(0, 20, 0, 0, 0))
            models(mutableListOf<ViewholderListingDetailsCarouselBindingModel_>().apply {
                images.forEachIndexed { index, name: String ->
                    add(
                        ViewholderListingDetailsCarouselBindingModel_()
                            .id("images-$name")
                            .url(name)
                            .clickListener(View.OnClickListener {
                                listingInitData.price = convertedPrice
                                clickListener(it, item, listingInitData, it)
                            })
                    )
                }
            })
        })

        models.add(ViewholderHeartSavedBindingModel_()
            .id("heart - ${item.id}")
            .wishListStatus(item.wishListStatus)
            .isOwnerList(item.isListOwner)
            .heartClickListener(View.OnClickListener {

                onWishListClick(item, listingInitData)
            })
        )
        var reviewStarRating = item.reviewsCount ?: 0
        var ratingCount = ""
        if (item.reviewsStarRating != null && item.reviewsStarRating != 0 && item.reviewsCount != null && item.reviewsCount != 0) {
            var roundOff =
                round(item.reviewsStarRating.toDouble() / item.reviewsCount.toDouble()).toInt()
            Timber.d("ratingCount ${round(item.reviewsStarRating.toDouble() / item.reviewsCount.toDouble())}")
            ratingCount = roundOff.toString()
        } else {
            ratingCount = ""
        }
        models.add(
            ViewholderExploreSearchListingItemBindingModel_()
                .id(item.id)
                .title(item.title.trim().replace("\\s+", " "))
                .roomType(item.roomType)
                .bookType(item.bookingType)
                .bedsCount(item.beds)
                .ratingsCount(ratingCount.toString())
                .ratingStarCount(reviewStarRating)
                .reviewsCount(item.reviewsCount)
                .price(convertedPrice)
                .onClick(View.OnClickListener {
                    listingInitData.price = convertedPrice

                })
                .onPriceClick(View.OnClickListener {
                    priceClickListener(item,it,listingInitData)
                })
                .setunderline(item.oneTotalpricechecked)
                .onBind { model, view, position ->
                    val totalPrice=view.dataBinding.root.findViewById<CustomUnderlineTextView>(R.id.tv_item_explore_search_listing_price)
                    if (item.bookingType=="instant") {
                        if(item.oneTotalpricechecked!!){
                            spanString(totalPrice,getCurrencyRateonetotal(item,listingInitData) +" "+totalPrice.context.getString(R.string.before_taxes)+"   ",
                                getCurrencyRateonetotal(item,listingInitData).length,true)
                            totalPrice.showUnderlines(true)
                        } else {
                            spanString(totalPrice,convertedPrice +" / "+totalPrice.context.getString(R.string.night)+"   ",
                                convertedPrice.length,true)
                            totalPrice.showUnderlines(false)
                        }

                    } else {
                        if(item.oneTotalpricechecked!!){
                            spanString(totalPrice,getCurrencyRateonetotal(item,listingInitData) +" "+totalPrice.context.getString(R.string.before_taxes)+"   ",
                                getCurrencyRateonetotal(item,listingInitData).length,false)
                            totalPrice.showUnderlines(true)
                        } else {
                            spanString(totalPrice,convertedPrice +" / "+totalPrice.context.getString(R.string.night)+"   ",
                                convertedPrice.length,false)
                            totalPrice.showUnderlines(false)
                        }
                    }


                }

        )
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        return models
    }
}

private fun spanString(tvItemListingSimilarPrice: TextView, price : String, length : Int,instant : Boolean) {
    val spannableString = SpannableString(price)
    spannableString.setSpan(AbsoluteSizeSpan(tvItemListingSimilarPrice.resources.getDimensionPixelSize(R.dimen.text_size_h4)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    spannableString.setSpan(AbsoluteSizeSpan(tvItemListingSimilarPrice.resources.getDimensionPixelSize(R.dimen.text_size_h2)), length+1, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    val imageSpan = ImageSpan(tvItemListingSimilarPrice.context, R.drawable.ic_light, ImageSpan.ALIGN_BASELINE)

    if (instant) {
        spannableString.setSpan(imageSpan,  spannableString.length-2,
            spannableString.length-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    tvItemListingSimilarPrice.text = spannableString

}

fun getCurrencyRate1(item: SearchListing, listingInitData: ListingInitData): String {
    return try {
        (BindingAdapters.getCurrencySymbol(listingInitData.selectedCurrency) + Utils.formatDecimal(
            CurrencyUtil.getRate(
                base = listingInitData.currencyBase,
                to = listingInitData.selectedCurrency,
                from = item.currency,
                rateStr = listingInitData.currencyRate,
                amount = item.basePrice
            )
        ))
    } catch (e: Exception) {
        e.printStackTrace()
        "0"
    }
}
fun getCurrencyRateonetotal(item: SearchListing, listingInitData: ListingInitData): String {
    return try {
        (BindingAdapters.getCurrencySymbol(listingInitData.selectedCurrency) + Utils.formatDecimal(
            CurrencyUtil.getRate(
                base = listingInitData.currencyBase,
                to = listingInitData.selectedCurrency,
                from = item.currency,
                rateStr = listingInitData.currencyRate,
                amount = item.oneTotalPrice.Total
            )
        ))
    } catch (e: Exception) {
        e.printStackTrace()
        "0"
    }
}