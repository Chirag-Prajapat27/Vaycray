package com.halfeaten.vaycray.host.payout.editpayout

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.halfeaten.vaycray.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.ActivityPayoutBinding
import com.halfeaten.vaycray.ui.WebViewActivity
import com.halfeaten.vaycray.ui.base.BaseActivity
import com.halfeaten.vaycray.ui.profile.edit_profile.RC_LOCATION_PERM
import com.halfeaten.vaycray.util.epoxy.CustomSpringAnimation
import com.halfeaten.vaycray.util.onClick
import com.halfeaten.vaycray.viewholderLoader
import com.halfeaten.vaycray.viewholderPayoutType
import com.halfeaten.vaycray.vo.Payout
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import javax.inject.Inject


class EditPayoutActivity : BaseActivity<ActivityPayoutBinding, PayoutViewModel>(),
    EditPayoutNavigator,EasyPermissions.PermissionCallbacks {

    enum class Screen {
        INTRO,
        INFO,
        PAYOUTTYPE,
        PAYOUTDETAILS,
        PAYPALDETAILS,
        WEBVIEW,
        FINISH
    }

    companion object {
        @JvmStatic
        fun openActivity(activity: Activity) {
            val intent = Intent(activity, EditPayoutActivity::class.java)
            activity.startActivity(intent)
        }
    }

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var mBinding: ActivityPayoutBinding

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_payout
    override val viewModel: PayoutViewModel
        get() = ViewModelProvider(this, mViewModelFactory).get(PayoutViewModel::class.java)
    var payoutList = ArrayList<Payout>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        mBinding = viewDataBinding!!
        viewModel.navigator = this
        initView()
        subscribeToLiveData()
        setUp()
    }

    private fun initView() {
        mBinding.rlToolbarNavigateup.onClick {
            finish()
        }
        if (payoutList.isNotEmpty()) {

        }
        mBinding.containerAddPayoutMethod.onClick {
            openFragment(CountryFragment(), "Country")
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.payoutsList.value != null) {
            viewModel.getPayouts()
        }
    }

    private fun subscribeToLiveData() {
        viewModel.loadPayouts().observe(this, Observer {
            it?.let { result ->
                payoutList = result
                mBinding.rlEditPayout.requestModelBuild()
            }
        })
    }

    private fun setUp() {
        mBinding.rlEditPayout.withModels {
            if (viewModel.isLoading.get()) {
                viewholderLoader {
                    id("Loader")
                    isLoading(true)
                }
            } else {
                if (payoutList.isNotEmpty()) {

                    payoutList.forEachIndexed { index, result ->
                        viewholderPayoutType {

                            id(result.id)
                            toolTipIcon(result.isVerified)

                            onBind { _, view, _ ->
                                val viewpressed =
                                    view.dataBinding.root.findViewById<ImageView>(R.id.tooltipImage)
                                viewpressed.setOnClickListener {
                                    val builder = AlertDialog.Builder(this@EditPayoutActivity)
                                    builder.setMessage(R.string.stripe_tooltip_text)
                                    builder.setPositiveButton(getString(R.string.ok_text)) { _, _ -> }
                                    builder.show()
                                }
                            }

                            if (result.method == 1) {
                                paymentType(resources.getString(R.string.paypal))
                            } else {
                                paymentType(getString(R.string.bank_account))
                            }

                            if (result.method == 1) {
                                details(resources.getString(R.string.details) + ": " + result.payEmail)
                            } else {
                                details(resources.getString(R.string.details) + ": " + "****" + result.pinDigit)
                            }
                            isVerified(result.isVerified)
                            isDefault(result.isDefault)
                            if (!result.isVerified) {
                                removeVisible(false)
                                buttonText(getString(R.string.verify))
                            } else {
                                if (result.isDefault) {
                                    removeVisible(true)
                                    buttonText(getString(R.string.default_txt))
                                } else {
                                    removeVisible(false)
                                    buttonText(getString(R.string.set_default_caps))
                                }
                            }
                            if (result.isVerified) {
                                if (result.isDefault) {
                                    isDefaultInverse(false)
                                } else {
                                    isDefaultInverse(true)
                                }
                            } else {
                                isDefaultInverse(true)
                            }
                            currency(resources.getString(R.string.currency) + ": [" + result.currency + "]")
                            if (result.isVerified) {
                                status(
                                    resources.getString(R.string.status) + ": " + resources.getString(
                                        R.string.ready
                                    )
                                )
                            } else {
                                status(
                                    resources.getString(R.string.status) + ": " + resources.getString(
                                        R.string.not_ready
                                    )
                                )
                            }

                            removeClick { _ ->
                                viewModel.setDefaultRemovePayputs("remove", result.id)
                            }
                            setDefault { _ ->
                                if (payoutList.getOrNull(index)?.isVerified == true)
                                    viewModel.setDefaultRemovePayputs("set", result.id)
                                else
                                    viewModel.verifyPayout(result.payEmail!!)
                            }
                        }
                    }
                }
            }
        }
    }

    fun openFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_up,
                R.anim.slide_down,
                R.anim.slide_up,
                R.anim.slide_down
            )
            .add(mBinding.flEditPayout.id, fragment, tag)
            .addToBackStack(null)
            .commit()
    }

    override fun disableCountrySearch(flag: Boolean) {
        val fragment = supportFragmentManager.findFragmentById(mBinding.flEditPayout.id)
        if (fragment is CountryFragment) {
            fragment.disableCountrySearch(flag)
        }
    }

    override fun moveToScreen(screen: Screen) {
        when (screen.name) {
            Screen.WEBVIEW.name -> {
                askCameraPermission()
            }

            Screen.FINISH.name -> finish()
        }
    }

    fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    override fun onBackPressed() {
        viewModel.listSearch.value = null
        super.onBackPressed()
    }
    @AfterPermissionGranted(RC_LOCATION_PERM)
    private fun askCameraPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            if (EasyPermissions.hasPermissions(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA

                )
            ) {
                WebViewActivity.openWebViewActivity(
                    this,
                    viewModel.connectingURL,
                    "EditStripe Onboarding-${viewModel.accountID}"
                )
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    "Grant Permission to access your gallery and photos",
                    RC_LOCATION_PERM,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA
                )
            }
        } else {
            if (EasyPermissions.hasPermissions(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA

                )
            ) {
                WebViewActivity.openWebViewActivity(
                    this,
                    viewModel.connectingURL,
                    "EditStripe Onboarding-${viewModel.accountID}"
                )
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    "Grant Permission to access your gallery and photos",
                    RC_LOCATION_PERM, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
            }
        }

    }
    override fun onRetry() {
        val fragment = supportFragmentManager.findFragmentById(mBinding.flEditPayout.id)
        if (fragment is CountryFragment) {
            viewModel.getCountryCode()
        } else {
            viewModel.getPayouts()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Timber.tag("AddPayoutActivity").d("Permission Denied!!")
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            Timber.tag("AddPayoutActivity").d("Permission Denied!!")
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

}