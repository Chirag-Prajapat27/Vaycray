package com.halfeaten.vaycray.ui.host.hostReservation

import android.os.Bundle
import android.view.View
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

class HostTripsFragment : BaseFragment<FragmentTripsBinding, HostTripsViewModel>() {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var mBinding: FragmentTripsBinding

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_trips
    override val viewModel: HostTripsViewModel
        get() = ViewModelProvider(
            baseActivity!!,
            mViewModelFactory
        ).get(HostTripsViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding!!
        val myAdapter = MyAdapter(this)
        with(mBinding.viewPager) {
            adapter = myAdapter
            offscreenPageLimit = 2
            TabLayoutMediator(mBinding.tabs, this) { tab, position ->
                when(position){
                    0->tab.text=getString(R.string.upcoming)
                    1->tab.text=getString(R.string.previous)
                }
            }.attach()
        }
       mBinding.refresh.onClick {
           checkNetwork {
               viewModel.loadTrips("")
               viewModel.loadUpcomingTrips("")
           }
       }

    }


    override fun clearDisposal() {
        viewModel.compositeDisposable.clear()
    }

    override fun onRetry() {
        viewModel.loadTrips("")
        viewModel.loadUpcomingTrips("")
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
                0 ->HostTripsListFragment.newInstance("upcoming")
                1 -> HostTripsListFragment.newInstance("previous")
                else -> Fragment()
            }
        }
    }

    fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }
}
