package com.halfeaten.vaycray.ui.payment

import android.content.Intent
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.halfeaten.vaycray.ConfirmReservationMutation
import com.halfeaten.vaycray.CreateReservationMutation
import com.halfeaten.vaycray.GetCurrenciesListQuery
import com.halfeaten.vaycray.GetPaymentMethodsQuery
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.data.DataManager
import com.halfeaten.vaycray.ui.base.BaseViewModel
import com.halfeaten.vaycray.util.performOnBackOutOnMain
import com.halfeaten.vaycray.util.resource.ResourceProvider
import com.halfeaten.vaycray.util.rx.Scheduler
import com.halfeaten.vaycray.util.toOptional
import com.halfeaten.vaycray.vo.BillingDetails
import com.halfeaten.vaycray.vo.Outcome
import com.halfeaten.vaycray.vo.PaymentData
import com.halfeaten.vaycray.vo.UserData
import com.stripe.android.Stripe
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import javax.inject.Inject

class PaymentViewModel @Inject constructor(
    dataManager: DataManager,
    private val scheduler: Scheduler,
    val resourceProvider: ResourceProvider
) : BaseViewModel<PaymentNavigator>(dataManager, resourceProvider) {

    private val _paymentLiveData = MutableLiveData<PaymentData?>()
    val paymentLiveData: LiveData<PaymentData?> = _paymentLiveData

    val stripeCard = MutableLiveData<Card?>()
    val billingDetails = MutableLiveData<BillingDetails>()
    val userData = MutableLiveData<UserData>()
    val msg = MutableLiveData<String>()
    val token = MutableLiveData<String?>()
    var selectedPaymentType = 0
    var selectedCurrency = ObservableField("")
    val paymentIntentSecret = MutableLiveData<String>()
    val stripeReqAdditionAction = MutableLiveData<String>()
    val paymentIntentLiveData = MutableLiveData<String>()
    val reservationId = MutableLiveData<Int>()
    val razorpayOrderId = MutableLiveData<String>()
    var currencies: MutableLiveData<List<GetCurrenciesListQuery.Result?>> = MutableLiveData()
    val stripeResponse: LiveData<Outcome<Token>>? = stripeCard.switchMap() {
        it?.let { it1 -> dataManager.createToken(it1) }
    }
    lateinit var stripe: Stripe

    lateinit var paymentMethods: MutableLiveData<List<GetPaymentMethodsQuery.Result?>?>

    init {
        selectedCurrency.set(resourceProvider.getString(R.string.currency))
    }

    fun loadPayoutMethods(): MutableLiveData<List<GetPaymentMethodsQuery.Result?>?> {
        if (!::paymentMethods.isInitialized) {
            paymentMethods = MutableLiveData()
            getPayoutMethods()
        }
        return paymentMethods
    }

    fun initData(intent: Intent?) {
        intent?.let {
            billingDetails.value = intent.getParcelableExtra("billingDetails")
            userData.value = intent.getParcelableExtra("userData")
            msg.value = intent.getStringExtra("msg")

            Log.d("DataUser", "initData: $userData")
            Log.d("DataPayment", "initData: ${intent.getStringExtra("msg")}")
            Log.d("DataPayment", "initDataBill: ${billingDetails.value}")
        }
    }

    /*    fun validateToken() {
            token.value?.let {
                if (it.isNotEmpty()) {
                    createReservation(it)

                }
            } ?: navigator.showToast(resourceProvider.getString(R.string.something_went_wrong))
        }*/

    fun createReservation() {
        try {
            val query = CreateReservationMutation(
                listId = billingDetails.value!!.listId,
                checkIn = billingDetails.value!!.checkIn,
                checkOut = billingDetails.value!!.checkOut,
                guests = billingDetails.value!!.guest,
                message = msg.value!!,
                basePrice = billingDetails.value!!.basePrice,
                cleaningPrice = billingDetails.value!!.cleaningPrice,
                currency = billingDetails.value!!.currency,
                discount = billingDetails.value!!.discount.toOptional(),
                discountType = billingDetails.value!!.discountLabel.toOptional(),
                guestServiceFee = billingDetails.value!!.guestServiceFee.toOptional(),
                hostServiceFee = billingDetails.value!!.hostServiceFee.toOptional(),
                total = billingDetails.value!!.total,
                bookingType = billingDetails.value!!.bookingType.toOptional(),
                paymentType = selectedPaymentType.toOptional(),
                convCurrency = getUserCurrency(),
                specialPricing = billingDetails.value!!.specialPricing,
                averagePrice = billingDetails.value!!.averagePrice.toOptional(),
                nights = billingDetails.value!!.nights.toOptional(),
                paymentCurrency = selectedCurrency.get().toOptional(),
                threadId = billingDetails.value!!.threadId.toOptional()
            )

            compositeDisposable.add(dataManager.createReservation(query)
                .doOnSubscribe { setIsLoading(true) }
                .doFinally { setIsLoading(false) }
                .performOnBackOutOnMain(scheduler)
                .subscribe({
                    try {
                        token.value = null
                        if (it.data?.createReservation?.status == 200) {

                            if (selectedPaymentType == 3) {
                                val data = it.data!!.createReservation!!.results!!
                                razorpayOrderId.postValue(it.data!!.createReservation!!.razorpayOrderId!!)
                                reservationId.postValue(it.data!!.createReservation!!.reservationId!!)

                                _paymentLiveData.postValue(
                                    PaymentData(
                                        "${data.guestData!!.firstName} ${data.guestData!!.lastName}",
                                        "${data.guestData!!.userData!!.email!!}",
                                        "${data.guestData!!.phoneNumber ?: "12345"}",
                                        "${it.data!!.createReservation!!.razorpayOrderId}"
                                    )
                                )
                            }
                        } else if (it.data?.createReservation?.status == 500) {
                            navigator.openSessionExpire("PaymentVM")
                        } else {
                            if (it.data?.createReservation?.errorMessage == null) {
                                navigator.showToast(it.data?.createReservation.toString())
                                Log.d(
                                    "Data",
                                    "500 else::createReservation: ${it.data?.createReservation}"
                                )
                            } else {
                                navigator.showSnackbar(
                                    "",
                                    it.data?.createReservation?.errorMessage!!
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, {
                    if (it is ApolloNetworkException) {
                        navigator.showOffline()
                    } else {
                        navigator.showError()
                        navigator.finishScreen()
                    }
                })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun confirmReservation(PaymentIntentId: String) {
        val query = ConfirmReservationMutation(
            reservationId = reservationId.value!!,
            paymentId = PaymentIntentId,
            razorpayOrderId = razorpayOrderId.value!!
        )
        compositeDisposable.add(dataManager.confirmReservation(query)
            .doOnSubscribe { setIsLoading(true) }
            .doFinally { setIsLoading(false) }
            .performOnBackOutOnMain(scheduler)
            .subscribe({
                try {
                    print(it.data)
                    if (it.data?.confirmReservation?.status == 200) {
                        navigator.moveToReservation(
                            it!!.data!!.confirmReservation!!.results!!.id!!
                        )
                    } else if (it.data?.confirmReservation?.status == 500) {
                        navigator.openSessionExpire("PaymentVM")
                    } else {
                        navigator.showToast(it.data?.confirmReservation?.errorMessage.toString())
                        Log.d("Data", "confirmReservation: ${it.data?.confirmReservation}")
                        /*if (it.data?.confirmReservation?.requireAdditionalAction == true) {
                            paymentIntentSecret.value =
                                it.data?.confirmReservation?.paymentIntentSecret!!
                            reservationId.value = it.data?.confirmReservation?.reservationId!!
                            stripeReqAdditionAction.value = "1"
                        } else {
                            stripeReqAdditionAction.value = "0"
                        }*/
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, {
                if (it is ApolloNetworkException) {
                    navigator.showOffline()
                } else {
                    navigator.showError()
                    navigator.finishScreen()
                }
            })
        )
    }

    fun getCurrency() {
        val query = GetCurrenciesListQuery()
        compositeDisposable.add(dataManager.getCurrencyList(query)
            .performOnBackOutOnMain(scheduler)
            .doOnSubscribe { setIsLoading(true) }
            .doFinally { setIsLoading(false) }
            .subscribe({ response ->
                val data = response.data!!.getCurrencies
                if (data?.status == 200) {
                    currencies.value = data.results!!
                } else if (data?.status == 500) {
                    if (data.errorMessage == null)
                        navigator.showError()
                    else navigator.showToast(data.errorMessage.toString())
                }
            }, {
                handleException(it)
            })
        )
    }

    fun getPayoutMethods() {
        val query = GetPaymentMethodsQuery()
        compositeDisposable.add(dataManager.getPayoutsMethod(query)
            .doOnSubscribe { setIsLoading(true) }
            .doFinally { setIsLoading(false) }
            .performOnBackOutOnMain(scheduler)
            .subscribe({ response ->
                try {
                    val data = response.data!!.getPaymentMethods
                    if (data!!.status == 200) {
                        paymentMethods.value = data.results
                        Log.d("DataPAY", "getPayoutMethods: ${data.results}")

                    } else {
                        if (data.errorMessage == null) {
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

}
