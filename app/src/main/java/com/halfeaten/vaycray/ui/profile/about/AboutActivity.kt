package com.halfeaten.vaycray.ui.profile.about

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.halfeaten.vaycray.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.AboutActivityBinding
import com.halfeaten.vaycray.ui.base.BaseActivity
import com.halfeaten.vaycray.ui.profile.about.why_Host.WhyHostActivity
import com.halfeaten.vaycray.util.addFragmentToActivity
import com.halfeaten.vaycray.util.epoxy.CustomSpringAnimation
import com.halfeaten.vaycray.util.gone
import com.halfeaten.vaycray.util.replaceFragmentInActivity
import com.halfeaten.vaycray.viewholderDividerNoPadding
import com.halfeaten.vaycray.viewholderProfileLists
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject


class AboutActivity : BaseActivity<AboutActivityBinding, AboutViewModel>() {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    lateinit var mBinding: AboutActivityBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.about_activity
    override val viewModel: AboutViewModel
        get() = ViewModelProvider(this, mViewModelFactory).get(AboutViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = viewDataBinding!!

        CustomSpringAnimation.spring(mBinding.rvSetting)
        mBinding.actionBar.tvToolbarHeading.text = getString(R.string.about)
        mBinding.actionBar.ivCameraToolbar.gone()
        mBinding.actionBar.ivNavigateup.setOnClickListener {
            finish()
        }
        setUp()
    }

    fun setUp() {
        mBinding.rvSetting.withModels {


            viewholderProfileLists {
                id("whyHost")
                name(getString(R.string.why_host))
                arrowVisibile(true)
                iconVisible(true)
                onClick(View.OnClickListener {
                    val intent = Intent(this@AboutActivity, WhyHostActivity::class.java)
                    startActivity(intent)
                })
            }

            viewholderDividerNoPadding {
                id("div1")
            }


            viewholderProfileLists {
                id("aboutUs")
                name(getString(R.string.about_us))
                iconVisible(true)
                arrowVisibile(true)
                onClick(View.OnClickListener {
                    val intent = Intent(this@AboutActivity, StaticPageActivity::class.java)
                    intent.putExtra("id", 1)
                    startActivity(intent)
                })
            }
            viewholderDividerNoPadding {
                id("div3")
            }

            viewholderProfileLists {
                id("trustnSafety")
                name(getString(R.string.trust_and_safty))
                iconVisible(true)
                arrowVisibile(true)
                paddingbottam(true)
                onClick(View.OnClickListener {
                    val intent = Intent(this@AboutActivity, StaticPageActivity::class.java)
                    intent.putExtra("id", 2)
                    startActivity(intent)
                })
            }


            viewholderDividerNoPadding {
                id("div4")
            }

        }
    }




    override fun onRetry() {

    }

}