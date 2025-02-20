package com.halfeaten.vaycray.ui.reservation

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.halfeaten.vaycray.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.ActivityBookingBinding
import com.halfeaten.vaycray.ui.base.BaseActivity
import com.halfeaten.vaycray.ui.home.HomeActivity
import com.halfeaten.vaycray.util.*
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

class ReservationActivity : BaseActivity<ActivityBookingBinding, ReservationViewModel>(), ReservationNavigator {

    @Inject lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var mBinding: ActivityBookingBinding
    @Inject lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_booking
    override val viewModel: ReservationViewModel
        get() = ViewModelProvider(this, mViewModelFactory).get(ReservationViewModel::class.java)

    private var is404PageShown = false
    fun supportFragmentInjector(): AndroidInjector<androidx.fragment.app.Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = viewDataBinding!!
        viewModel.navigator = this
        onBackPressedType()
        initView()
        subscribeToLiveData()
    }

    private fun initView() {
        viewModel.type = intent.getIntExtra("type", 1)

        viewModel.reservationId.value = intent.getIntExtra("reservationId", 0)
        intent?.extras?.let {
             var userType = it.getString("userType", "")
        }
        if(viewModel.config == 1){
            if (viewModel.type == 1 || viewModel.type == 3) {
                addFragment(ItineraryFragment(), "ItineraryFragment")
            } else {
                addFragment(ReceiptFragment(), "ReceiptFragment")
            }
        }


        mBinding.btnExplore.onClick {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

    }

    private fun addFragment(fragment: androidx.fragment.app.Fragment, tag: String) {
        addFragmentToActivity(mBinding.flBooking.id, fragment, tag)
    }

    private fun replaceFragment(fragment: androidx.fragment.app.Fragment, tag: String) {
        replaceFragmentInActivity(mBinding.flBooking.id, fragment, tag)
    }

    private fun subscribeToLiveData() {
        viewModel.reservationId.observe(this, Observer {
            viewModel.getReservationDetails()
        })
    }

    fun loadListingDetails() {
        viewModel.getReservationDetails()
    }

    override fun navigateToScreen(screen: Int) {
        when (screen) {
            9 -> {
                viewModel.config=2
                replaceFragment(ReceiptFragment(), "ReceiptFragment")
            }
        }
    }

    override fun onRetry() {
            viewModel.getReservationDetails()
    }

    override fun show404Page() {
        is404PageShown =true
        mBinding.flBooking.gone()
        mBinding.ll404Page.visible()
    }
    fun onBackPressedType() {
        if(is404PageShown){
            onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    println("Back button pressed")
                    mBinding.ll404Page.gone()
                }
            })

        }
    }
}