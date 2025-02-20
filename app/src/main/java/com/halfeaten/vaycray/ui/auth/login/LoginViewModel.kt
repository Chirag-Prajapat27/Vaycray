package com.halfeaten.vaycray.ui.auth.login

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.halfeaten.vaycray.Constants
import com.halfeaten.vaycray.LoginQuery
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.data.DataManager
import com.halfeaten.vaycray.ui.auth.AuthNavigator
import com.halfeaten.vaycray.ui.auth.AuthViewModel
import com.halfeaten.vaycray.ui.base.BaseViewModel
import com.halfeaten.vaycray.util.Event
import com.halfeaten.vaycray.util.Utils
import com.halfeaten.vaycray.util.performOnBackOutOnMain
import com.halfeaten.vaycray.util.resource.ResourceProvider
import com.halfeaten.vaycray.util.rx.Scheduler
import com.halfeaten.vaycray.util.toOptional
import com.halfeaten.vaycray.vo.Outcome
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    dataManager: DataManager,
    val scheduler: Scheduler,
    val resourceProvider: ResourceProvider
) : BaseViewModel<AuthNavigator>(dataManager, resourceProvider) {

    val email = ObservableField("")
    val password = ObservableField("")
    val lottieProgress =
        ObservableField<AuthViewModel.LottieProgress>(AuthViewModel.LottieProgress.NORMAL)
    val showPassword = ObservableField(false)

    private val generateFirebase = MutableLiveData<String>()
    val fireBaseResponse: LiveData<Event<Outcome<String>>>? = generateFirebase.switchMap {
        dataManager.generateFirebaseToken()
    }

    private fun validateFirebase(): Boolean {
        return dataManager.firebaseToken != null
    }

    fun checkLogin() {
        navigator.hideSnackbar()
        navigator.hideKeyboard()
        try {
            if (validateFirebase()) {
                if (Utils.isValidEmail(email.get()!!)) {
                    if (password.get()!!.isNotBlank() && password.get()!!.length >= 8) {
                        loginUser()
                    } else {
                        navigator.showSnackbar(
                            resourceProvider.getString(R.string.password_error),
                            resourceProvider.getString(R.string.invalid_password_desc)
                        )
                    }

                } else {
                    navigator.showSnackbar(
                        resourceProvider.getString(R.string.invalid_email),
                        resourceProvider.getString(R.string.invalid_email_desc)
                    )
                }
            } else {
                generateFirebase.value = "Login"
            }
        } catch (e: KotlinNullPointerException) {
            e.printStackTrace()
            navigator.showError()
        }
    }

    fun showPassword() {
        navigator.hideSnackbar()
        showPassword.set(showPassword.get()?.not())
    }

    private fun loginUser() {
        val buildQuery = LoginQuery(
            email = email.get()!!.trim(),
            password = password.get()!!,
            deviceType = Constants.deviceType,
            deviceDetail = "".toOptional(),
            deviceId = dataManager.firebaseToken!!
        )

        compositeDisposable.add(dataManager.doServerLoginApiCall(buildQuery)
            .doOnSubscribe {
                setIsLoading(true)
                lottieProgress.set(AuthViewModel.LottieProgress.LOADING)
            }
            .doFinally { setIsLoading(false) }
            .performOnBackOutOnMain(scheduler)
            .subscribe({ response ->
                try {
                    if (response.data?.userLogin?.status == 200) {
                        lottieProgress.set(AuthViewModel.LottieProgress.CORRECT)
                        val data = response.data!!.userLogin
                        saveDataInPref(data!!.result!!)
                    } else if (response.data?.userLogin?.status == 500) {
                        if ("blocked" in (response.data?.userLogin?.errorMessage ?: "")) {
                            navigator.showToast(
                                response.data?.userLogin?.errorMessage ?: ""

                            )
                            lottieProgress.set(AuthViewModel.LottieProgress.NORMAL)
                        } else
                            navigator.openSessionExpire("LoginVM")
                    } else {
                        lottieProgress.set(AuthViewModel.LottieProgress.NORMAL)
                        if (response.data?.userLogin?.errorMessage == null)
                            navigator.showSnackbar(
                                resourceProvider.getString(R.string.invalid_login_credentials),
                                resourceProvider.getString(R.string.show_password),
                                resourceProvider.getString(R.string.login_txt)
                            )
                        else navigator.showToast(
                            response.data?.userLogin?.errorMessage.toString()
                        )
                    }
                } catch (e: KotlinNullPointerException) {
                    e.printStackTrace()
                    lottieProgress.set(AuthViewModel.LottieProgress.NORMAL)
                    navigator.showError()
                }
            }, {
                lottieProgress.set(AuthViewModel.LottieProgress.NORMAL)
                handleException(it)
            })
        )
    }

    private fun saveDataInPref(data: LoginQuery.Result?) {
        val userCurrency = if (data?.user?.preferredCurrency == null) {
            dataManager.currencyBase
        } else {
            data.user.preferredCurrency
        }
        dataManager.updateUserInfo(
            data?.userToken,
            data?.userId,
            DataManager.LoggedInMode.LOGGED_IN_MODE_SERVER,
            data?.user?.firstName + " " + data?.user?.lastName,
            null,
            data?.user?.picture,
            userCurrency,
            data?.user?.preferredLanguage,
            data?.user?.createdAt
        )
        try {
            data?.user?.verification?.let {
                dataManager.updateVerification(
                    it.isPhoneVerified,
                    it.isEmailConfirmed,
                    it.isIdVerification,
                    it.isGoogleConnected,
                    it.isFacebookConnected
                )
            }
            moveToScreen()
        } catch (e: KotlinNullPointerException) {
            e.printStackTrace()
            navigator.showError()
        }
    }

    fun moveToScreen() {
        navigator.navigateScreen(AuthViewModel.Screen.MOVETOHOME_DARK_MODE)
    }

    fun moveToScreenSKIP() {
        navigator.navigateScreen(AuthViewModel.Screen.MOVETOHOME)
    }
}