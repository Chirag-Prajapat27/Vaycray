package com.halfeaten.vaycray.ui.auth.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.halfeaten.vaycray.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.FragmentAuthForgotPasswordBinding
import com.halfeaten.vaycray.ui.auth.AuthNavigator
import com.halfeaten.vaycray.ui.auth.AuthViewModel
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.util.RxBus
import com.halfeaten.vaycray.util.UiEvent
import com.halfeaten.vaycray.util.onClick
import javax.inject.Inject

class ForgotPasswordFragment :
    BaseFragment<FragmentAuthForgotPasswordBinding, ForgotPasswordViewModel>(), AuthNavigator {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_auth_forgot_password
    override val viewModel: ForgotPasswordViewModel
        get() = ViewModelProvider(this, mViewModelFactory).get(ForgotPasswordViewModel::class.java)
    lateinit var mBinding: FragmentAuthForgotPasswordBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding!!
        viewModel.navigator = this
        initView()
    }

    private fun initView() {

        mBinding.ivClose.onClick { viewModel.navigator.navigateScreen(AuthViewModel.Screen.AuthScreen) }
    }

    override fun navigateScreen(screen: AuthViewModel.Screen, vararg params: String?) {
        RxBus.publish(UiEvent.Navigate(screen, *params))
    }

    override fun onRetry() {
        viewModel.emailValidation()
    }

}