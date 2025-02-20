package com.halfeaten.vaycray.ui.profile.currency

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.halfeaten.vaycray.GetCurrenciesListQuery
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.data.DataManager
import com.halfeaten.vaycray.ui.base.BaseNavigator
import com.halfeaten.vaycray.ui.base.BaseViewModel
import com.halfeaten.vaycray.util.Event
import com.halfeaten.vaycray.util.performOnBackOutOnMain
import com.halfeaten.vaycray.util.resource.ResourceProvider
import com.halfeaten.vaycray.util.rx.Scheduler
import com.halfeaten.vaycray.vo.Outcome
import javax.inject.Inject

@SuppressLint("LogNotTimber")
class CurrencyVIewModel @Inject constructor(
        dataManager: DataManager,
        private val scheduler: Scheduler,
        val resourceProvider: ResourceProvider
): BaseViewModel<BaseNavigator>(dataManager,resourceProvider) {

    val postsOutcome = MutableLiveData<Event<Outcome<List<GetCurrenciesListQuery.Result?>?>>>()
    val preSelectedLanguages = MutableLiveData<String>()

    init {
        getCurrency()
    }

    fun getCurrency() {
        val query = GetCurrenciesListQuery()
        compositeDisposable.add(dataManager.getCurrencyList(query)
                .doOnSubscribe { postsOutcome.postValue(Event(Outcome.loading(true))) }
                .doFinally { postsOutcome.postValue(Event(Outcome.loading(false))) }
                .performOnBackOutOnMain(scheduler)
                .subscribe( { response ->
                    try {
                        val data = response.data!!.getCurrencies
                        if (data?.status == 200) {
                            postsOutcome.value = Event(Outcome.success(data.results!!))
                        } else if(data?.status == 500) {
                            navigator.openSessionExpire("CurrencyVM")
                        } else {
                            postsOutcome.value = Event(Outcome.error(Throwable()))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        postsOutcome.value = Event(Outcome.error(Throwable()))
                        navigator.showToast(resourceProvider.getString(R.string.something_went_wrong))
                    }
                }, {
                    postsOutcome.value = Event(Outcome.error(Throwable()))
                    handleException(it, true)
                } )
        )
    }


}