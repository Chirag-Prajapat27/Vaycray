package com.halfeaten.vaycray.host.payout.editpayout

import androidx.lifecycle.MutableLiveData
import com.halfeaten.vaycray.ConfirmPayoutMutation
import com.halfeaten.vaycray.GetCountrycodeQuery
import com.halfeaten.vaycray.GetPayoutsQuery
import com.halfeaten.vaycray.SetDefaultPayoutMutation
import com.halfeaten.vaycray.VerifyPayoutMutation
import com.halfeaten.vaycray.data.DataManager
import com.halfeaten.vaycray.ui.base.BaseViewModel
import com.halfeaten.vaycray.util.performOnBackOutOnMain
import com.halfeaten.vaycray.util.resource.ResourceProvider
import com.halfeaten.vaycray.util.rx.Scheduler
import com.halfeaten.vaycray.util.toOptional
import com.halfeaten.vaycray.vo.Payout
import javax.inject.Inject

class PayoutViewModel @Inject constructor(
    dataManager: DataManager,
    val scheduler: Scheduler,
    val resourceProvider: ResourceProvider
) : BaseViewModel<EditPayoutNavigator>(dataManager, resourceProvider) {

    init {
        dataManager.clearHttpCache()
    }

    lateinit var list: MutableLiveData<List<GetCountrycodeQuery.Result?>?>
    val listSearch = MutableLiveData<ArrayList<GetCountrycodeQuery.Result?>?>()
    lateinit var payoutsList: MutableLiveData<ArrayList<Payout>?>

    var connectingURL: String = ""
    var accountID: String = ""

    fun loadCountryCode(): MutableLiveData<List<GetCountrycodeQuery.Result?>?> {
        if (!::list.isInitialized) {
            list = MutableLiveData()
            getCountryCode()
        } else {
            list = MutableLiveData()
            getCountryCode()
        }
        return list
    }

    fun loadPayouts(): MutableLiveData<ArrayList<Payout>?> {
        if (!::payoutsList.isInitialized) {
            payoutsList = MutableLiveData()
            getPayouts()
        }
        return payoutsList
    }

    fun onSearchTextChanged(text: CharSequence) {
        if (text.isNotEmpty()) {
            val searchText = text.toString().capitalize()
            val containsItem = ArrayList<GetCountrycodeQuery.Result?>()
            list.value?.forEachIndexed { _, result ->
                result?.countryName?.let {
                    if (it.contains(searchText)) {
                        containsItem.add(result)
                    }
                }
            }
            listSearch.value = containsItem
        } else {
            list.value?.let {
                listSearch.value = ArrayList(it)
            }
        }
    }

    fun getCountryCode() {
        navigator.disableCountrySearch(false)
        val query = GetCountrycodeQuery()
        compositeDisposable.add(dataManager.getCountryCode(query)
            .doOnSubscribe { setIsLoading(true) }
            .doFinally { setIsLoading(false) }
            .performOnBackOutOnMain(scheduler)
            .subscribe({ response ->
                try {
                    val data = response.data!!.getCountries
                    if (data?.status == 200) {
                        navigator.disableCountrySearch(true)
                        list.value = response.data!!.getCountries!!.results
                    } else {
                        if (data?.errorMessage==null){
                            navigator.showError()}
                        else{
                            navigator.showToast(data.errorMessage.toString())
                        }
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

    fun getPayouts() {
        val query = GetPayoutsQuery()
        compositeDisposable.add(dataManager.getPayouts(query)
            .doOnSubscribe { setIsLoading(true) }
            .doFinally { setIsLoading(false) }
            .performOnBackOutOnMain(scheduler)
            .subscribe({ response ->
                try {
                    if (response.data!!.getPayouts!!.results != null) {
                        parseResult(response.data!!.getPayouts!!.results!!)
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




    fun verifyPayout(id: String) {
        val query = VerifyPayoutMutation(stripeAccount = id.toOptional())

        compositeDisposable.add(dataManager.confirmPayout(query)
            .doOnSubscribe { setIsLoading(true) }
            .doFinally { setIsLoading(false) }
            .performOnBackOutOnMain(scheduler)
            .subscribe({ response ->
                try {
                    val data = response.data!!.verifyPayout
                    if (response.data!!.verifyPayout!!.status == 200) {
//                        connectingURL = data?.connectUrl!!
//                        accountID = data.stripeAccountId!!
                        navigator.moveToScreen(EditPayoutActivity.Screen.WEBVIEW)
                    } else {
                        if (data?.errorMessage == null) {
                            navigator.showError()
                        } else {
                            navigator.showToast(data.errorMessage.toString())
                        }
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

    private fun parseResult(results: List<GetPayoutsQuery.Result?>) {
        val list = ArrayList<Payout>()
        results.forEach {
            val verified = it?.isVerified
            if (verified != null) {
                list.add(
                    Payout(
                        id = it.id!!,
                        name = it.paymentMethod!!.name!!,
                        method = it.paymentMethod.id!!,
                        currency = it.currency!!,
                        isDefault = it.default!!,
                        payEmail = it.payEmail,
                        pinDigit = it.last4Digits,
                        isVerified = it.isVerified
                    )
                )
            } else {
                list.add(
                    Payout(
                        id = it?.id!!,
                        name = it.paymentMethod!!.name!!,
                        method = it.paymentMethod.id!!,
                        currency = it.currency!!,
                        isDefault = it.default!!,
                        payEmail = it.payEmail,
                        pinDigit = it.last4Digits,
                        isVerified = false
                    )
                )
            }


        }
        payoutsList.value = list
    }

    private fun removePayout(id: Int) {
        val payoutList = payoutsList.value
        for (i in 0 until payoutList!!.size) {
            if (payoutList[i].id == id) {
                payoutList.removeAt(i)
                break
            }
        }
        payoutsList.value = payoutList
    }

    private fun setDefaultPayout(id: Int) {
        val payoutList = payoutsList.value
        for (i in 0 until payoutList!!.size) {
            payoutList[i].isDefault = payoutList[i].id == id
        }
        payoutsList.value = payoutList
    }

    fun setDefaultRemovePayputs(type: String, id: Int) {
        val mutate = SetDefaultPayoutMutation(type = type, id = id)

        compositeDisposable.add(
            dataManager.setDefaultPayout(mutate)
                .doOnSubscribe { setIsLoading(true) }
                .doFinally { setIsLoading(false) }
                .performOnBackOutOnMain(scheduler)
                .subscribe({ response ->
                    try {
                        val data = response.data!!.setDefaultPayout
                        if (data?.status == 200) {
                            if (type == "remove") {
                                removePayout(id)
                                getPayouts()
                            } else {
                                setDefaultPayout(id)
                                getPayouts()
                            }
                        } else {
                            if (data?.errorMessage == null) {
                                navigator.showError()
                            } else {
                                navigator.showToast(data.errorMessage.toString())
                            }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        navigator.showError()
                    }
                }, {
                    handleException(it, true)
                })
        )
    }


}