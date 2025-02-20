package com.halfeaten.vaycray.ui.profile.manageAccount

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.DialogDeleteAccountBinding
import com.halfeaten.vaycray.ui.base.BaseDialogFragment
import com.halfeaten.vaycray.ui.splash.SplashActivity
import com.halfeaten.vaycray.util.gone
import com.halfeaten.vaycray.util.onClick
import com.halfeaten.vaycray.util.visible
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class DeleteAccountDialog : BaseDialogFragment(), ManageAccountNavigator {

    private val TAG = DeleteAccountDialog::class.java.simpleName
    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    val viewModel: ManageAccountViewModel
        get() = ViewModelProvider(this, mViewModelFactory).get(ManageAccountViewModel::class.java)

    companion object {
        @JvmStatic
        fun newInstance() = DeleteAccountDialog()
    }

    fun dismissDialog() {
        dismissDialog(TAG)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<DialogDeleteAccountBinding>(
            inflater,
            R.layout.dialog_delete_account,
            container,
            false
        )
        val view = binding.root
        AndroidSupportInjection.inject(this)
        viewModel.navigator = this
        binding.btnCancel.onClick {
            binding.ltLoading.gone()
            dismissDialog()
        }
        binding.btnApply.onClick {
            binding.ltLoading.visible()
            viewModel.getDeleteAccount(requireContext())
        }
        return view
    }

    fun show(fragmentManager: androidx.fragment.app.FragmentManager) {
        super.show(fragmentManager, TAG)
    }

    override fun navigateScreen(
        OpenScreen: ManageAccountViewModel.OpenScreen,
        vararg params: String?
    ) {
        val intent = Intent(activity, SplashActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun closeDialog() {
        dismissDialog()
    }

}