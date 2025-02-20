package com.halfeaten.vaycray.ui.profile.edit_profile

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.halfeaten.vaycray.BR
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.databinding.ActivityEditProfileBinding
import com.halfeaten.vaycray.ui.base.BaseFragment
import com.halfeaten.vaycray.util.onClick
import javax.inject.Inject

class EditProfileFragment: BaseFragment<ActivityEditProfileBinding, EditProfileViewModel>() {

    @Inject lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var activityEditProfileBinding: ActivityEditProfileBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_edit_profile
    override val viewModel: EditProfileViewModel
        get() = ViewModelProvider(baseActivity!!, mViewModelFactory).get(EditProfileViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityEditProfileBinding = viewDataBinding!!
        activityEditProfileBinding.btnAdd.onClick {
            viewModel.done()
        }
        activityEditProfileBinding.btnCancel.onClick {
            viewModel.navigator.moveToBackScreen()
        }
    }

    override fun onRetry() { }
}