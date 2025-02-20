package com.halfeaten.vaycray.ui.trips

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.halfeaten.vaycray.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.FragmentTripsBinding
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.util.onClick
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import timber.log.Timber
import javax.inject.Inject


class TripsFragment : BaseFragment<FragmentTripsBinding, TripsViewModel>() {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var mBinding: FragmentTripsBinding
    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_trips
    override val viewModel: TripsViewModel
        get() = ViewModelProvider(baseActivity!!, mViewModelFactory).get(TripsViewModel::class.java)

    override fun onConfigurationChanged(newConfig: Configuration) {
        val appTheme: String = viewModel.dataManager.prefTheme.toString()
        if (appTheme == "Auto") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            requireActivity().recreate()
        }
        super.onConfigurationChanged(newConfig)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding!!
        mBinding.tvHeader.text = getString(R.string.my_trips)
        val myAdapter = MyAdapter(this)
        mBinding.viewPager.adapter=myAdapter
        with(mBinding.viewPager) {
            adapter = myAdapter
            offscreenPageLimit = 2
            TabLayoutMediator(mBinding.tabs, this) { tab, position ->
                // Customize tabs if needed
                when(position){
                    0->tab.text = resources.getString(R.string.upcoming_trips)
                    1->tab.text = resources.getString(R.string.previous_trips)
                }

            }.attach()
        }
        mBinding.refresh.onClick {
            checkNetwork {
                viewModel.loadUpcomingTrips("")
                viewModel.loadTrips("")
            }
        }
    }



    override fun clearDisposal() {
        viewModel.compositeDisposable.clear()
    }

    override fun onRetry() {
        viewModel.loadUpcomingTrips("")
        viewModel.loadTrips("")
    }

    fun onRefresh() {
        viewModel.upcomingTripRefresh()
        viewModel.tripRefresh()
    }


    inner class MyAdapter(fm: Fragment) :
        FragmentStateAdapter(fm) {

        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> TripsListFragment.newInstance("upcoming")
                1 -> TripsListFragment.newInstance("previous")
                else -> Fragment()
            }
        }
    }

    fun supportFragmentInjector(): AndroidInjector<androidx.fragment.app.Fragment> {
        return fragmentDispatchingAndroidInjector
    }
}
