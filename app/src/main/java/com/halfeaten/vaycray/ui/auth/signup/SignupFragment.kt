package com.halfeaten.vaycray.ui.auth.signup

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.halfeaten.vaycray.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.FragmentAuthSignupBinding
import com.halfeaten.vaycray.ui.auth.AuthNavigator
import com.halfeaten.vaycray.ui.auth.AuthViewModel
import com.halfeaten.vaycray.ui.auth.login.LoginViewModel
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.util.RxBus
import com.halfeaten.vaycray.util.UiEvent
import com.halfeaten.vaycray.util.onClick
import com.halfeaten.vaycray.vo.Outcome
import javax.inject.Inject

class SignupFragment : BaseFragment<FragmentAuthSignupBinding, LoginViewModel>(), AuthNavigator {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_auth_signup
    override val viewModel: LoginViewModel
        get() = ViewModelProvider(this, mViewModelFactory).get(LoginViewModel::class.java)
    lateinit var mBinding: FragmentAuthSignupBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding!!
        viewModel.navigator = this
        initView()
        subscribeToLiveData()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        mBinding.tvWelcome.text =
            getString(R.string.welcome_to_appname)
        mBinding.tvForgotPassword.onClick { RxBus.publish(UiEvent.Navigate(AuthViewModel.Screen.FORGOTPASSWORD)) }
//        mBinding.btnFb.onClick { RxBus.publish(UiEvent.Navigate(AuthViewModel.Screen.FB)) }
        mBinding.rlSigninBtns.onClick { RxBus.publish(UiEvent.Navigate(AuthViewModel.Screen.GOOGLE)) }
        mBinding.btnCreateAccount.onClick { RxBus.publish(UiEvent.Navigate(AuthViewModel.Screen.NAME)) }
        mBinding.btnLogin.onClick { viewModel.checkLogin() }
        val mSpannableString = SpannableString(getString(R.string.signup_here))
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
        mBinding.tvSignUp.text = mSpannableString
        mBinding.ivPasswordVisibility.onClick { showPassword() }
        mBinding.tvSkip.onClick {
            viewModel.moveToScreenSKIP()
        }
    }

    override fun onRetry() {

    }

    override fun navigateScreen(screen: AuthViewModel.Screen, vararg params: String?) {
        RxBus.publish(UiEvent.Navigate(screen, *params))
    }

    private fun subscribeToLiveData() {
        viewModel.fireBaseResponse?.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let { outcome ->
                when (outcome) {
                    is Outcome.Error -> {
                        showError()
                    }

                    is Outcome.Failure -> {
                        showError()
                    }

                    is Outcome.Success -> {
                        viewModel.checkLogin()
                    }

                    is Outcome.Progress -> {
                        viewModel.isLoading.set(outcome.loading)
                    }
                }
            }
        })
    }

    fun showPassword() {
        if (viewModel.showPassword.get() == false) {
            viewModel.showPassword.set(true)
            mBinding.ivPasswordVisibility.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_visibility_show
                )
            )
        } else {
            viewModel.showPassword.set(false)
            mBinding.ivPasswordVisibility.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_visibility_hide,
                )
            )
        }
    }
}