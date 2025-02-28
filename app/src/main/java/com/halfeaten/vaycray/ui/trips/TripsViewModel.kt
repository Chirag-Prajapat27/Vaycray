package com.halfeaten.vaycray.ui.trips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import com.halfeaten.vaycray.GetAllReservationQuery
import com.halfeaten.vaycray.data.DataManager
import com.halfeaten.vaycray.data.remote.paging.Listing
import com.halfeaten.vaycray.data.remote.paging.NetworkState
import com.halfeaten.vaycray.ui.base.BaseNavigator
import com.halfeaten.vaycray.ui.base.BaseViewModel
import com.halfeaten.vaycray.util.resource.ResourceProvider
import com.halfeaten.vaycray.util.rx.Scheduler
import com.halfeaten.vaycray.util.toOptional
import javax.inject.Inject


class TripsViewModel @Inject constructor(
        dataManager: DataManager,
        val scheduler: Scheduler,
        val resourceProvider: ResourceProvider
) : BaseViewModel<BaseNavigator>(dataManager,resourceProvider) {

    val currencyBase = MutableLiveData<String>()
    val currencyRates = MutableLiveData<String>()

    val tripResult = MutableLiveData<Listing<GetAllReservationQuery.Result>>()
    lateinit var tripList: LiveData<PagedList<GetAllReservationQuery.Result>>
    val networkState: LiveData<NetworkState> = tripResult.switchMap() { it.networkState }
    val refreshState: LiveData<NetworkState> = tripResult.switchMap() { it.refreshState }


    var isRefreshing = false

    fun loadTrips(dateFilters: String) : LiveData<PagedList<GetAllReservationQuery.Result>> {
            tripList = MutableLiveData()
            val buildQuery = GetAllReservationQuery(
                    dateFilter = "previous".toOptional(),
                    userType  = "Guest".toOptional()
            )
            tripResult.value = dataManager.listOfTripsList(buildQuery, 10)
            tripList = tripResult.switchMap() {
                it.pagedList
            }

        return tripList
    }

    fun tripRefresh() {
        tripResult.value?.refresh?.invoke()
    }


    val upcomingTripResult = MutableLiveData<Listing<GetAllReservationQuery.Result>>()
    lateinit var upcomingTripList: LiveData<PagedList<GetAllReservationQuery.Result>>
    val upcomingNetworkState: LiveData<NetworkState> = upcomingTripResult.switchMap() { it.networkState }

    fun loadUpcomingTrips(dateFilters: String) : LiveData<PagedList<GetAllReservationQuery.Result>> {
            upcomingTripList = MutableLiveData()
            val buildQuery = GetAllReservationQuery(
                dateFilter = "upcoming".toOptional(),
                userType  = "Guest".toOptional()
            )
            upcomingTripResult.value = dataManager.listOfTripsList(buildQuery, 10)
            upcomingTripList = upcomingTripResult.switchMap() {
                it.pagedList
            }

        return upcomingTripList
    }

    fun upcomingTripRefresh() {
        upcomingTripResult.value?.refresh?.invoke()
    }


}
