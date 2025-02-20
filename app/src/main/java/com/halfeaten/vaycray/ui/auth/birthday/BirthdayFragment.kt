package com.halfeaten.vaycray.ui.auth.birthday

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.halfeaten.vaycray.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.FragmentAuthBirthdayBinding
import com.halfeaten.vaycray.ui.auth.AuthNavigator
import com.halfeaten.vaycray.ui.auth.AuthViewModel
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.ui.dobcalendar.BirthdayDialog
import com.halfeaten.vaycray.util.RxBus
import com.halfeaten.vaycray.util.UiEvent
import com.halfeaten.vaycray.util.gone
import com.halfeaten.vaycray.util.onClick
import javax.inject.Inject


class BirthdayFragment : BaseFragment<FragmentAuthBirthdayBinding, BirthdayViewModel>(),
    AuthNavigator {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_auth_birthday
    override val viewModel: BirthdayViewModel
        get() = ViewModelProvider(this, mViewModelFactory).get(BirthdayViewModel::class.java)
    lateinit var mBinding: FragmentAuthBirthdayBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding!!
        viewModel.navigator = this
        initView()
    }

    private fun initView() {
        mBinding.actionBar.tvRightside.gone()
        mBinding.actionBar.rlToolbarNavigateup.onClick { baseActivity?.onBackPressedDispatcher?.onBackPressed() }
        mBinding.ltLoadingView.setImageResource(R.drawable.ic_right_arrow_blue)
        mBinding.rlBirthday.onClick {
            hideSnackbar()
            openCalender1()
        }
    }

    private fun openCalender1() {
        val birthdayDialog = BirthdayDialog.newInstance(
            viewModel.dob.get()!![0],
            viewModel.dob.get()!![1],
            viewModel.dob.get()!![2]
        )
        birthdayDialog.show(childFragmentManager)
        birthdayDialog.setCallBack(DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            viewModel.yearLimit.set(arrayOf(year, month + 1, dayOfMonth))
            viewModel.dob.set(arrayOf(year, month, dayOfMonth))
        })
    }

    override fun navigateScreen(screen: AuthViewModel.Screen, vararg params: String?) {
        RxBus.publish(UiEvent.Navigate(screen, *params))
    }

    override fun onRetry() {
        viewModel.signUpUser()
    }

}
