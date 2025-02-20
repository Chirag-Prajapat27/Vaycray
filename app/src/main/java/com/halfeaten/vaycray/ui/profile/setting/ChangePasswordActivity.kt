package com.halfeaten.vaycray.ui.profile.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airbnb.epoxy.databinding.BR
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.ActivityChangePasswordBinding
import com.halfeaten.vaycray.databinding.ActivitySettingBinding
import com.halfeaten.vaycray.ui.base.BaseActivity
import com.halfeaten.vaycray.ui.home.HomeActivity
import com.halfeaten.vaycray.ui.host.hostHome.HostHomeActivity
import com.halfeaten.vaycray.ui.profile.setting.currency.CurrencyBottomSheet
import com.halfeaten.vaycray.ui.splash.SplashActivity
import com.halfeaten.vaycray.util.LocaleHelper
import com.halfeaten.vaycray.util.Utils
import com.halfeaten.vaycray.util.binding.BindingAdapters
import com.halfeaten.vaycray.util.gone
import com.halfeaten.vaycray.util.onClick
import com.halfeaten.vaycray.viewholderDivider
import com.halfeaten.vaycray.viewholderProfileLists
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

class ChangePasswordActivity: BaseActivity<ActivityChangePasswordBinding, SettingViewModel>(),SettingsNavigator {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private var mGoogleSignInClient: GoogleSignInClient? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_change_password
    override val viewModel: SettingViewModel
        get() = ViewModelProvider(this,mViewModelFactory).get(SettingViewModel::class.java)
    lateinit var mBinding : ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = viewDataBinding!!
        viewModel.navigator = this
        mBinding.ivClose.setOnClickListener {
            finish()
        }
        setUp()
    }

    fun setUp(){
        mBinding.btnSignup.onClick {
            checkNetwork {
                viewModel.checkPassword()
            }

        }
        mBinding.ivOldPasswordVisibility.onClick {
            showOldPassword()
        }
        mBinding.ivNewPasswordVisibility.onClick {
            showNewPassword()
        }
        mBinding.ivConfirmPasswordVisibility.onClick {
            showConfirmPassword()
        }
    }

    fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    override fun openSplashScreen() {
        if (viewModel.dataManager.isHostOrGuest){
            val intent = Intent(this, HostHomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

    }
    override fun onRetry() {
        if(!isNetworkConnected){
            showOffline()
        }
    }

    override fun navigateToSplash() {
    }

    override fun setLocale(key: String) {
    }

    override fun finishActivity() {
       finish()
    }

    fun showOldPassword() {
        if (viewModel.showOldPassword.get() == false) {
            viewModel.showOldPassword.set(true)
            mBinding.ivOldPasswordVisibility.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_visibility_show
                )
            )
        } else {
            viewModel.showOldPassword.set(false)
            mBinding.ivOldPasswordVisibility.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_visibility_hide,
                )
            )
        }
    }

    fun showNewPassword() {
        if (viewModel.showNewPassword.get() == false) {
            viewModel.showNewPassword.set(true)
            mBinding.ivNewPasswordVisibility.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_visibility_show
                )
            )
        } else {
            viewModel.showNewPassword.set(false)
            mBinding.ivNewPasswordVisibility.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_visibility_hide,
                )
            )
        }
    }

    fun showConfirmPassword() {
        if (viewModel.showConfirmPassword.get() == false) {
            viewModel.showConfirmPassword.set(true)
            mBinding.ivConfirmPasswordVisibility.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_visibility_show
                )
            )
        } else {
            viewModel.showConfirmPassword.set(false)
            mBinding.ivConfirmPasswordVisibility.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_visibility_hide,
                )
            )
        }
    }

}