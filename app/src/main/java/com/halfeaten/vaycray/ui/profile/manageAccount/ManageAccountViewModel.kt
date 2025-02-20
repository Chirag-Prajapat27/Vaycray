package com.halfeaten.vaycray.ui.profile.manageAccount

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.halfeaten.vaycray.DeleteUserMutation
import com.halfeaten.vaycray.data.DataManager
import com.halfeaten.vaycray.ui.base.BaseViewModel
import com.halfeaten.vaycray.util.performOnBackOutOnMain
import com.halfeaten.vaycray.util.resource.ResourceProvider
import com.halfeaten.vaycray.util.rx.Scheduler
import javax.inject.Inject

class ManageAccountViewModel @Inject constructor(
        dataManager: DataManager,
        val scheduler: Scheduler,
        val resourceProvider: ResourceProvider,
) : BaseViewModel<ManageAccountNavigator>(dataManager, resourceProvider) {

    enum class OpenScreen {
        LOGIN,
        FINISHED
    }


    fun getDeleteAccount(context: Context) {
        val buildQuery = DeleteUserMutation()
        compositeDisposable.add(dataManager.clearHttpCache()
                .flatMap { dataManager.getDeleteUser(buildQuery).toObservable() }
                .doOnSubscribe { setIsLoading(true) }
                .doFinally { setIsLoading(false) }
                .performOnBackOutOnMain(scheduler)
                .subscribe( { response ->
                    try {
                        val data = response.data!!
                        if (data.deleteUser?.status == 200) {
                            afterSignOut(context)
                            navigator.navigateScreen(OpenScreen.LOGIN)
                        } else if (data.deleteUser?.status == 400) {
                            navigator.showToast(data.deleteUser!!.errorMessage.toString())
                            navigator.closeDialog()
                        }  else if (data.deleteUser?.status == 500) {
                            navigator.openSessionExpire("AboutVM")
                        } else {
                            navigator.showError()
                        }
                    } catch (e: KotlinNullPointerException) {
                        e.printStackTrace()
                        navigator.showError()
                    }
                }, {
                    it.printStackTrace()
                } )
        )
    }

    fun afterSignOut(context: Context) {
        dataManager.setUserAsLoggedOut()

    }
}