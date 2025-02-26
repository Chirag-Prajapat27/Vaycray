package com.halfeaten.vaycray.ui.host.hostHome

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.airbnb.epoxy.databinding.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.data.local.prefs.AppPreferencesHelper
import com.halfeaten.vaycray.databinding.ActivityHomeBinding
import com.halfeaten.vaycray.host.calendar.CalendarListingFragment1
import com.halfeaten.vaycray.services.CheckInternetReceiver
import com.halfeaten.vaycray.services.MyBroadcastListener
import com.halfeaten.vaycray.ui.base.BaseActivity
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.ui.home.HomeNavigator
import com.halfeaten.vaycray.ui.home.HomePageAdapter
import com.halfeaten.vaycray.ui.home.HomeViewModel
import com.halfeaten.vaycray.ui.host.hostInbox.HostInboxFragment
import com.halfeaten.vaycray.ui.host.hostListing.HostListingFragment
import com.halfeaten.vaycray.ui.host.hostReservation.HostTripsFragment
import com.halfeaten.vaycray.ui.profile.ProfileFragment
import com.halfeaten.vaycray.ui.profile.confirmPhonenumber.ConfirmPhnoActivity
import com.halfeaten.vaycray.util.gone
import com.halfeaten.vaycray.util.onClick
import com.halfeaten.vaycray.util.visible
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import java.util.*
import javax.inject.Inject

class HostHomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(), HomeNavigator,
    SharedPreferences.OnSharedPreferenceChangeListener,
    MyBroadcastListener {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var mBinding: ActivityHomeBinding

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_home
    override val viewModel: HomeViewModel
        get() = ViewModelProvider(this, mViewModelFactory).get(HomeViewModel::class.java)

    private val fragmentList = ArrayList<Fragment>()
    lateinit var pageAdapter: HomePageAdapter
    lateinit var checkInternetReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = viewDataBinding!!
        viewModel.navigator = this
        checkInternetReceiver = CheckInternetReceiver(this)
        registerNetworkBroadcastForNougat();
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        })
        if (viewModel.loginStatus == 0) {
            openSessionExpire("")
        }
        viewModel.validateData()
        initView()
    }

    private fun registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(
                checkInternetReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(
                checkInternetReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
    }

    private fun initView() {
        topView = mBinding.root
        setUpBottomNavigation()
        mBinding.fab.onClick {
            setTrips()
        }
        viewModel.dataManager.isHostOrGuest = true
        try {
            intent?.let {
                if (intent.hasExtra("from")) {
                    val from = intent.getStringExtra("from")
                    if (from == "verification") {
                        setProfile()
                    } else if (from == "trip") {
                        Handler(Looper.getMainLooper()).postDelayed({
                            setTrips()},100)
                    } else if (from == "calendar") {
                        setCalendar()
                    } else if (from == "fcm") {
                        Handler(Looper.getMainLooper()).postDelayed({
                            setInbox()
                        },100)

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (viewModel.dataManager.isPhoneVerified == false && viewModel.loginStatus != 0) {
            val intent = Intent(this, ConfirmPhnoActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    private fun setTrips() {
        try {
            mBinding.vpHome.setCurrentItem(2, false)
            mBinding.bnExplore.selectedItemId = R.id.trips
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setCalendar() {
        try {
            mBinding.vpHome.setCurrentItem(1, false)
            mBinding.bnExplore.selectedItemId = R.id.calendar
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setInbox() {
        try {
            mBinding.vpHome.setCurrentItem(3, false)
            mBinding.bnExplore.selectedItemId = R.id.inbox
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun initialAdapter() {
        pageAdapter = HomePageAdapter(supportFragmentManager, createFragment())
        setUpBottomNavigationListener()
        with(mBinding.vpHome) {
            adapter = pageAdapter
            offscreenPageLimit = 4
        }
    }

    private fun createFragment(): ArrayList<Fragment> {
        with(fragmentList) {
            clear()
            add(HostListingFragment())
            add(CalendarListingFragment1())
            add(HostTripsFragment())
            add(HostInboxFragment())
            add(ProfileFragment())

        }
        return fragmentList
    }

    private fun setUpBottomNavigation() {
        mBinding.bnExplore.menu.clear()
        mBinding.bnExplore.inflateMenu(R.menu.host_bottom_navigation)
    }

    private fun setUpBottomNavigationListener() {

        mBinding.bnExplore.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.listing -> {
                    mBinding.vpHome.setCurrentItem(0, false)
                    (pageAdapter.getCurrentFragment() as HostListingFragment).onRefresh()
                }

                R.id.calendar -> {
                    mBinding.vpHome.setCurrentItem(1, false)
                    (pageAdapter.getCurrentFragment() as CalendarListingFragment1).onRefresh()
                }

                R.id.trips -> {
                    mBinding.vpHome.setCurrentItem(2, false)
                    (pageAdapter.getCurrentFragment() as HostTripsFragment).onRefresh()
                }

                R.id.inbox -> {
                    mBinding.vpHome.setCurrentItem(3, false)
                    (pageAdapter.getCurrentFragment() as HostInboxFragment).onRefresh()
                }

                R.id.profile -> {
                    mBinding.vpHome.setCurrentItem(4, false)
                    (pageAdapter.getCurrentFragment() as ProfileFragment).onRefresh()
                }
            }
            true
        }
    }


    fun hideBottomNavigation() {
        mBinding.bnExplore.gone()
        mBinding.bottomAppBar.gone()
        mBinding.fab.gone()

    }

    fun showBottomNavigation() {
        mBinding.bnExplore.visible()
        mBinding.bottomAppBar.visible()
        mBinding.fab.visible()
    }

    override fun onBackPressed() {
        if (::pageAdapter.isInitialized) {
            hideSnackbar()
            if (mBinding.vpHome.currentItem == 1) {
                if (pageAdapter.getCurrentFragment() is CalendarListingFragment1) {
                    val count =
                        (pageAdapter.getCurrentFragment() as CalendarListingFragment1).childFragmentManager.backStackEntryCount
                    when {
                        count == 1 -> {
                            showBottomNavigation()
                            (pageAdapter.getCurrentFragment() as CalendarListingFragment1).childFragmentManager.popBackStack()
                        }

                        else -> {
                            setManageList()
                        }
                    }
                }
            } else if (mBinding.vpHome.currentItem == 0) {
                if (pageAdapter.getCurrentFragment() is HostListingFragment) {
                    val count =
                        (pageAdapter.getCurrentFragment() as HostListingFragment).childFragmentManager.backStackEntryCount
                    when {
                        count == 0 -> {
                            showBottomNavigation()
                            (pageAdapter.getCurrentFragment() as HostListingFragment).onBackPressed()
                        }

                        else -> {
                            finish()
                        }
                    }
                }
            } else {
                setManageList()
            }
        } else {
            finish()
        }
    }


    private fun setManageList() {
        try {
            mBinding.vpHome.setCurrentItem(0, false)
            mBinding.bnExplore.selectedItemId = R.id.listing
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setProfile() {
        viewModel.clearHttpCache()
        mBinding.vpHome.setCurrentItem(4, false)
        mBinding.bnExplore.selectedItemId = R.id.profile
    }

    override fun onResume() {
        super.onResume()
        if (::pageAdapter.isInitialized) {
            val currentFragment = pageAdapter.getCurrentFragment()
            if ((currentFragment) is HostListingFragment) {
                currentFragment.onRefresh()
            }
            if ((currentFragment) is HostInboxFragment) {
                currentFragment.onRefresh()
            }
            if ((currentFragment) is HostTripsFragment) {
                currentFragment.onRefresh()
            }
        }
        viewModel.pref.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        viewModel.disposeObservable()
        viewModel.pref.unregisterOnSharedPreferenceChangeListener(this)
    }

    fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    override fun onRetry() {
        val currentFragment = pageAdapter.getCurrentFragment()
        (currentFragment as BaseFragment<*, *>).onRetry()
        hideSnackbar()
    }

    override fun onDestroy() {
        unregisterReceiver(checkInternetReceiver)
        super.onDestroy()
    }

    override fun doSomething(online: Boolean) {
        if (!online)
            showOffline()
        onRetry()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        sharedPreferences?.let {
            key?.let { key ->
                if (key == AppPreferencesHelper.PREF_KEY_NOTIFICATION) {
                    viewModel.setNotification(sharedPreferences.getBoolean(key, false))
                }
            }
        }
    }
}

