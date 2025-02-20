package com.halfeaten.vaycray.ui.reservation

import androidx.lifecycle.MutableLiveData
import com.halfeaten.vaycray.GetReservationQuery
import com.halfeaten.vaycray.data.DataManager
import com.halfeaten.vaycray.ui.base.BaseViewModel
import com.halfeaten.vaycray.util.Utils
import com.halfeaten.vaycray.util.performOnBackOutOnMain
import com.halfeaten.vaycray.util.resource.ResourceProvider
import com.halfeaten.vaycray.util.rx.Scheduler
import com.halfeaten.vaycray.util.toOptional
import javax.inject.Inject

class ReservationViewModel @Inject constructor(
    dataManager: DataManager,
    private val scheduler: Scheduler,
    val resourceProvider: ResourceProvider
) : BaseViewModel<ReservationNavigator>(dataManager, resourceProvider) {

    var reservationComplete = MutableLiveData<GetReservationQuery.GetReservation?>()
    var reservation = MutableLiveData<GetReservationQuery.Results?>()
    val reservationId = MutableLiveData<Int>()
    var type = 1
    var config = 1

    fun getName(): String? {
        return dataManager.currentUserName
    }

    fun getSiteName(): String? {
        return dataManager.siteName
    }

    fun getReservationDetails() {
        val buildQuery = GetReservationQuery(
            reservationId = reservationId.value!!,
            convertCurrency = getUserCurrency().toOptional()
        )
        compositeDisposable.add(dataManager.getReservationDetails(buildQuery)
            .doOnSubscribe { setIsLoading(true) }
            .doFinally { setIsLoading(false) }
            .performOnBackOutOnMain(scheduler)
            .subscribe({ response ->
                try {
                    val data = response.data!!.getReservation
                    if (data?.status == 200) {
                        try {
                            if (data?.results!!.listData == null) {
                                navigator.show404Page()
                            }
                        } catch (e: Exception) {
                        }
                        reservationComplete.value = response.data!!.getReservation
                        reservation.value = data.results

                    } else if (data?.status == 500) {
                        navigator.openSessionExpire("ReservationVM")
                    } else {
                        navigator.showError()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    navigator.showError()
                }

            }, {
                handleException(it)
            })
        )
    }

    fun currencyConverter(currency: String, total: Double): String {
        return getCurrencySymbol() + Utils.formatDecimal(getConvertedRate(currency, total))
    }
}