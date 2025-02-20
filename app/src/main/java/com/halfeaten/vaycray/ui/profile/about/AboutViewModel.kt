package com.halfeaten.vaycray.ui.profile.about

import androidx.lifecycle.MutableLiveData
import com.halfeaten.vaycray.GetStaticPageContentQuery
import com.halfeaten.vaycray.GetWhyHostDataQuery
import com.halfeaten.vaycray.data.DataManager
import com.halfeaten.vaycray.ui.base.BaseViewModel
import com.halfeaten.vaycray.util.performOnBackOutOnMain
import com.halfeaten.vaycray.util.resource.ResourceProvider
import com.halfeaten.vaycray.util.rx.Scheduler
import com.halfeaten.vaycray.util.toOptional
import javax.inject.Inject

class AboutViewModel @Inject constructor(
    dataManager: DataManager,
    val scheduler: Scheduler,
    val resourceProvider: ResourceProvider
) : BaseViewModel<AboutNavigator>(dataManager, resourceProvider) {

    var staticContentDetails: MutableLiveData<GetStaticPageContentQuery.Data?> = MutableLiveData()
    var whyHostData: MutableLiveData<GetWhyHostDataQuery.Data?> = MutableLiveData()

    enum class OpenScreen {
        WHY_HOST,
        FINISHED
    }

    fun onClick(openScreen: AboutViewModel.OpenScreen) {
        navigator.navigateScreen(openScreen)
    }


    fun getStaticContent(id: Int) {
        val buildQuery = GetStaticPageContentQuery(id = id.toOptional())
        compositeDisposable.add(dataManager.clearHttpCache()
            .flatMap { dataManager.getStaticPageContent(buildQuery).toObservable() }
            .doOnSubscribe { setIsLoading(true) }
            .doFinally { setIsLoading(false) }
            .performOnBackOutOnMain(scheduler)
            .subscribe({ response ->
                try {
                    val data = response.data!!.getStaticPageContent
                    if (data?.status == 200) {
                        staticContentDetails.value = response.data
                    } else if (data?.status == 500) {
                        navigator.openSessionExpire("AboutVM")
                    } else {
                        if (data?.errorMessage == null)
                            navigator.showError()
                        else navigator.showToast(data?.errorMessage.toString())
                    }
                } catch (e: KotlinNullPointerException) {
                    e.printStackTrace()
                    navigator.showError()
                }
            }, {
                it.printStackTrace()

            })
        )
    }

    fun getWhyHostData() {
        val buildQuery = GetWhyHostDataQuery()
        compositeDisposable.add(dataManager.clearHttpCache()
            .flatMap { dataManager.getWhyHostData(buildQuery).toObservable() }
            .doOnSubscribe { setIsLoading(true) }
            .doFinally { setIsLoading(false) }
            .performOnBackOutOnMain(scheduler)
            .subscribe({ response ->
                try {
                    val data = response.data!!.getWhyHostData
                    if (data?.status == 200) {
                        whyHostData.value = response.data
                    } else if (data?.status == 500) {
                        navigator.openSessionExpire("AboutVM")
                    } else {
                        if (data?.errorMessage == null)
                            navigator.showError()
                        else navigator.showToast(data?.errorMessage.toString())
                    }
                } catch (e: KotlinNullPointerException) {
                    e.printStackTrace()
                    navigator.showError()
                }
            }, {
                it.printStackTrace()

            })
        )
    }
}