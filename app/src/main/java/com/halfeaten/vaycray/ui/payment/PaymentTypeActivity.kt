package com.halfeaten.vaycray.ui.payment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airbnb.epoxy.databinding.BR
import com.halfeaten.vaycray.*
import com.halfeaten.vaycray.databinding.ActivityPaymentBinding
import com.halfeaten.vaycray.ui.WebViewActivity
import com.halfeaten.vaycray.ui.base.BaseActivity
import com.halfeaten.vaycray.ui.reservation.ReservationActivity
import com.halfeaten.vaycray.util.onClick
import com.halfeaten.vaycray.vo.PaymentType
import com.stripe.android.Stripe
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class PaymentTypeActivity : BaseActivity<ActivityPaymentBinding, PaymentViewModel>(),
    PaymentResultWithDataListener, PaymentNavigator {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var mBinding: ActivityPaymentBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_payment
    override val viewModel: PaymentViewModel
        get() = ViewModelProvider(this, mViewModelFactory).get(PaymentViewModel::class.java)
    private lateinit var stripe: Stripe
    lateinit var amount: String
    val checkout = Checkout()

    var paymentTypeArray = ArrayList<PaymentType>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = viewDataBinding!!
        viewModel.navigator = this
        paymentTypeArray.add(PaymentType("Paypal", 1))
        paymentTypeArray.add(PaymentType("Stripe", 2))
        paymentTypeArray.add(PaymentType("Razorpay", 3))
        viewModel.initData(intent)
        viewModel.getCurrency()
        initView()
    }

    private fun initView() {

        /*
   * To ensure faster loading of the Checkout form,
   * call this method as early as possible in your checkout flow
   * */
        Checkout.preload(applicationContext)

        mBinding.ivNavigateup.onClick {
            if (viewModel.isLoading.get().not()) {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        mBinding.btnPay.onClick {
            checkout.setKeyID(Constants.RAZORPAY_KEY_LIVE)

            if (viewModel.selectedPaymentType == 0) {
                viewModel.navigator.showSnackbar(
                    getString(R.string.select_payment_type),
                    getString(R.string.please_select_payment_type_to_continue)
                )
            } else {
                if (viewModel.selectedPaymentType == 1) {
                    if (viewModel.selectedCurrency.get() != getString(R.string.currency)) {
                        //viewModel.createReservation("")
                    } else {
                        viewModel.navigator.showSnackbar(
                            getString(R.string.currency),
                            getString(R.string.please_select_currency)
                        )
                    }
                } else if (viewModel.selectedPaymentType == 2) {
                    stripe = Stripe(applicationContext, Constants.stripePublishableKey)
                    viewModel.stripe = stripe
                    addFragment(StripePaymentFragment(), "STRIPE_FRAGMENT")
                } else {
                    viewModel.selectedPaymentType = 3

                    ///Get data after the successful execution of the createReservation API.
                    viewModel.paymentLiveData.observe(this) { paymentDetail ->
                        paymentDetail?.let {
                            razorpayStartPayment(
                                it.fullName,
                                it.email,
                                it.contact,
                                it.razorpayOrderId
                            )
                        }
                    }
                    ///Make a request to the CreateReservation API.
                    viewModel.createReservation()
                }
            }
        }

        subscribeToLiveData()
    }

    private fun subscribeToLiveData() {
        viewModel.loadPayoutMethods().observe(this, Observer {
            it?.let { result ->
                setUp(result)
            }
        })
    }

    private fun setUp(result: List<GetPaymentMethodsQuery.Result?>) {
        Log.d("DataSetUp", "setUp: $result")
        if (result.size < 2) {
            viewModel.selectedPaymentType = result[0]?.paymentType!!
        } else {
            viewModel.selectedPaymentType = 1
        }
        mBinding.rvPaymentType.withModels {
            result.forEachIndexed { index, paymentType ->
                if (paymentType?.isEnable!!) {
                    viewholderSelectPaymentType {
                        id("paymentType--$index")
                        if (paymentType.paymentType == 1) {
                            text("Paypal")
                        } else if (paymentType.paymentType == 2) {
                            text("Stripe")
                        } else {
                            text("Razorpay")
                        }
                        if (paymentType.paymentType == 1) {
                            drawable(R.drawable.ic_paypal)
                        } else if (paymentType.paymentType == 2) {
                            drawable(R.drawable.ic_stripe)
                        } else {
                            drawable(R.drawable.ic_razorpay)
                        }
                        onClick(View.OnClickListener {
                            viewModel.selectedPaymentType = paymentType.paymentType!!
                            this@withModels.requestModelBuild()
                        })
                        isChecked(viewModel.selectedPaymentType == paymentType.paymentType!!)

                    }
                    if (index == 0 && result.size > 1) {
                        viewholderDivider {
                            id("idDivider")
                        }
                    }
                }
            }
        }
    }


    private fun addFragment(fragment: Fragment, tag: String?) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_up,
                R.anim.slide_down,
                R.anim.slide_up,
                R.anim.slide_down
            )
            .add(mBinding.fragFrame.id, fragment, tag)
            .addToBackStack(null)
            .commit()
    }

    override fun moveToReservation(id: Int) {
        val intent = Intent(this, ReservationActivity::class.java)
        intent.putExtra("type", 3)
        intent.putExtra("reservationId", id)
        intent.putExtra("userType", "Guest")
        setResult(32, intent)
        startActivity(intent)
        finish()
    }

    override fun finishScreen() {
        val intent = Intent()
        setResult(32, intent)
        finish()
    }

    //Not Using
    override fun moveToPayPalWebView(redirectUrl: String) {
        if (redirectUrl.isNotEmpty()) {
            WebViewActivity.openWebViewActivityForResult(
                104,
                this,
                redirectUrl,
                "PayPalPayment-104"
            )
        } else {
            viewModel.navigator.showToast(getString(R.string.return_url_not_fount))
        }
    }

    override fun onBackPressed() {
        if (viewModel.isLoading.get().not()) {
            super.onBackPressed()
        }
    }

    fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    override fun onRetry() {
    }


    ///Razorpay Integration/ setup
    private fun razorpayStartPayment(
        fullName: String,
        email: String,
        contact: String,
        razorpayOrderId: String
    ) {

        val amountInPaise = (viewModel.billingDetails.value!!.total * 100).toInt()
        val obj = JSONObject()
        try {
            obj.put("name", fullName)
            obj.put("description", viewModel.msg.value)
            obj.put(
                "image",
                "https://media.tradly.app/images/marketplace/34521/razor_pay_icon-ICtywSbN.png"
            )

            obj.put("send_sms_hash", true)
            obj.put("allow_rotation", false)

            obj.put("theme.color", "#3399cc")
            obj.put("currency", viewModel.billingDetails.value!!.currency)
            //obj.put("order_id", razorpayOrderId)   // temp is not pass in razorpay due to some error
            obj.put("amount", amountInPaise)
            obj.put("prefill.email", email)
            obj.put("prefill.contact", contact)

            checkout.open(this, obj)
        } catch (e: JSONException) {
            Toast.makeText(this, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    ///Razorpay payment success function
    override fun onPaymentSuccess(s: String?, paymentData: PaymentData?) {

        try {
            Toast.makeText(this, "Payment is success: $s", Toast.LENGTH_SHORT).show()

            ///Make a request to the ConfirmReservation API after payment successfully"
            viewModel.confirmReservation(paymentData!!.paymentId)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("Data", "onPaymentSuccess: $s :: ${paymentData}")
    }

    //Razorpay payment Error function
    override fun onPaymentError(code: Int, description: String?, paymentData: PaymentData?) {
        Toast.makeText(this, "Payment failed due to error: $description ", Toast.LENGTH_SHORT)
            .show()

        Log.d("Data", "onPaymentError: $code :: $description :: $paymentData")

        when (code) {
            Checkout.NETWORK_ERROR -> showToast("Network error. Please check your connection.")
            Checkout.INVALID_OPTIONS -> showToast("Invalid payment options. Check your UPI ID.")
            Checkout.PAYMENT_CANCELED -> showToast("Payment was cancelled by the user.")
            Checkout.TLS_ERROR -> showToast("Security error. Update your app.")
            else -> showToast("Payment failed: $description")
        }
    }

    /*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            viewModel.setIsLoading(false)
            if (resultCode != 0) {
                if (resultCode == 104) {
                    if (data?.getStringArrayExtra("url").toString().contains("/cancel".toRegex())) {
                        viewModel.navigator.showToast(getString(R.string.payment_failed))
                    } else {
                        data?.getStringExtra("url")?.let {
                            val map = getQueryMap(it)
                            val paymentId = map?.get("token")
                            val payerId = map?.get("PayerID")
                            viewModel.confirmPayPalPayment(paymentId ?: "", payerId ?: "")
                        }
                    }
                } else if (resultCode == 107) {
                    viewModel.navigator.showToast(getString(R.string.payment_cancelled))
                } else {
                    stripe.onPaymentResult(
                        requestCode,
                        data,
                        object : ApiResultCallback<PaymentIntentResult> {
                            override fun onSuccess(result: PaymentIntentResult) {
                                val paymentIntent = result.intent
                                when (paymentIntent.status) {
                                    StripeIntent.Status.RequiresPaymentMethod ->
                                        viewModel.navigator.showToast("Payment failed")

                                    StripeIntent.Status.RequiresConfirmation -> {
                                        viewModel.paymentIntentLiveData.value = paymentIntent.id
                                    }

                                    else -> viewModel.navigator.showToast("Payment failed")
                                }
                            }

                            override fun onError(e: Exception) {
                                viewModel.navigator.showToast(e.message!!)
                            }
                        })
                }
            }
        }*/


}

