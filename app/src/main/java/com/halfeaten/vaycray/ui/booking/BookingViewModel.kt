package com.halfeaten.vaycray.ui.booking

import android.content.Intent
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.halfeaten.vaycray.GetProfileQuery
import com.halfeaten.vaycray.GetReservationQuery
import com.halfeaten.vaycray.data.DataManager
import com.halfeaten.vaycray.ui.base.BaseViewModel
import com.halfeaten.vaycray.util.performOnBackOutOnMain
import com.halfeaten.vaycray.util.resource.ResourceProvider
import com.halfeaten.vaycray.util.rx.Scheduler
import com.halfeaten.vaycray.vo.BillingDetails
import com.halfeaten.vaycray.vo.ListingInitData
import javax.inject.Inject

class BookingViewModel @Inject constructor(
        dataManager: DataManager,
        private val scheduler: Scheduler,
        val resourceProvider: ResourceProvider
): BaseViewModel<BookingNavigator>(dataManager, resourceProvider) {

    val billingDetails = MutableLiveData<BillingDetails>()

    var listDetails = ListingInitData()

    val msg = ObservableField("")

    val avatar = MutableLiveData<String>()

    val reservation = MutableLiveData<GetReservationQuery.Results>()







    fun setInitialData(intent: Intent) {
        try {
            listDetails = intent.getParcelableExtra("lisitingDetails")!!
            billingDetails.value = BillingDetails(
                    checkIn = intent.getStringExtra("checkIn").orEmpty(),
                    checkOut = intent.getStringExtra("checkOut").orEmpty(),
                    basePrice = intent.getDoubleExtra("basePrice", 0.0),
                    nights = intent.getIntExtra("nights", 0),
                    guestServiceFee = intent.getDoubleExtra("guestServiceFee", 0.0),
                    cleaningPrice = intent.getDoubleExtra("cleaningPrice", 0.0),
                    discount = intent.getDoubleExtra("discount", 0.0),
                    discountLabel = intent.getStringExtra("discountLabel"),
                    total = intent.getDoubleExtra("total", 0.0),
                    houseRule = intent.getStringArrayListExtra("houseRules")!!,
                    title = intent.getStringExtra("title").orEmpty(),
                    image = intent.getStringExtra("image").orEmpty(),
                    cancellation = intent.getStringExtra("cancellation").orEmpty(),
                    cancellationContent = intent.getStringExtra("cancellationContent").orEmpty(),
                    guest = intent.getIntExtra("guest", 0),
                    hostServiceFee = intent.getDoubleExtra("hostServiceFee", 0.0),
                    currency = intent.getStringExtra("currency").orEmpty(),
                    listId = intent.getIntExtra("listId", 0),
                    bookingType = intent.getStringExtra("bookingType").orEmpty(),
                    isProfilePresent = intent.getBooleanExtra("isProfilePresent", false),
                    averagePrice = intent.getDoubleExtra("averagePrice",0.0),
                    priceForDays = intent.getDoubleExtra("priceForDays",0.0),
                    specialPricing = intent.getStringExtra("specialPricing").orEmpty(),
                    isSpecialPriceAssigned = intent.getBooleanExtra("isSpecialPriceAssigned",false),
                    threadId = intent.getIntExtra("threadId",0)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}