package com.halfeaten.vaycray.ui.auth.signup

import com.halfeaten.vaycray.ui.auth.AuthViewModel

interface SignupNavigator {

/*    fun onCreateAccountButtonClick()

    fun onFacebookButtonClick()

    fun onGoogleButtonClick()

    fun onLoginButtonClick()

    fun onCloseButtonClick()
    */

    fun onButtonClick(screen: AuthViewModel.Screen)

}