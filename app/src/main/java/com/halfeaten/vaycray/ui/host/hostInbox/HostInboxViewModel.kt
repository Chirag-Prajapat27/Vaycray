package com.halfeaten.vaycray.ui.host.hostInbox

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import com.halfeaten.vaycray.GetAllThreadsQuery
import com.halfeaten.vaycray.data.DataManager
import com.halfeaten.vaycray.data.remote.paging.Listing
import com.halfeaten.vaycray.ui.base.BaseNavigator
import com.halfeaten.vaycray.ui.base.BaseViewModel
import com.halfeaten.vaycray.util.resource.ResourceProvider
import com.halfeaten.vaycray.util.rx.Scheduler
import com.halfeaten.vaycray.util.toOptional
import javax.inject.Inject

class HostInboxViewModel @Inject constructor(
        dataManager: DataManager,
        val scheduler: Scheduler,
        val resourceProvider: ResourceProvider
): BaseViewModel<BaseNavigator>(dataManager,resourceProvider) {

    val inboxResult = MutableLiveData<Listing<GetAllThreadsQuery.Result>>()
    lateinit var inboxList: LiveData<PagedList<GetAllThreadsQuery.Result>>
    val networkState = inboxResult.switchMap() { it.networkState }
    val refreshState = inboxResult.switchMap() { it.refreshState }

    fun loadInbox() : LiveData<PagedList<GetAllThreadsQuery.Result>> {
        if (!::inboxList.isInitialized) {
            inboxList = MutableLiveData()
            val buildQuery = GetAllThreadsQuery(
                    threadType = "host".toOptional()
            )
            inboxResult.value = dataManager.listOfInbox(buildQuery, 10)
            inboxList = inboxResult.switchMap() {
                it.pagedList
            }
        }
        return inboxList
    }

    fun inboxRefresh() {
        inboxResult.value?.refresh?.invoke()
    }



    fun getInboxList() {
        val buildQuery = GetAllThreadsQuery(
            threadType = "host".toOptional()
        )
        inboxResult.value = dataManager.listOfInbox(buildQuery, 10)
    }
}