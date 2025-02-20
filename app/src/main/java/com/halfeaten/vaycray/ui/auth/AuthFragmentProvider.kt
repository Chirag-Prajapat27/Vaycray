package com.halfeaten.vaycray.ui.auth

import com.halfeaten.vaycray.ui.auth.birthday.BirthdayFragment
import com.halfeaten.vaycray.ui.auth.email.EmailFragment
import com.halfeaten.vaycray.ui.auth.forgotpassword.ForgotPasswordFragment
import com.halfeaten.vaycray.ui.auth.login.LoginFragment
import com.halfeaten.vaycray.ui.auth.name.NameCreationFragment
import com.halfeaten.vaycray.ui.auth.password.PasswordFragment
import com.halfeaten.vaycray.ui.auth.resetPassword.ResetPasswordFragment
import com.halfeaten.vaycray.ui.auth.signup.SignupFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class AuthFragmentProvider {

    @ContributesAndroidInjector
    abstract fun provideSignupFragmentFactory(): SignupFragment

    @ContributesAndroidInjector
    abstract fun provideNameCreationFragmentFactory(): NameCreationFragment

    @ContributesAndroidInjector
    abstract fun provideEmailFragmentFactory(): EmailFragment

    @ContributesAndroidInjector
    abstract fun providePasswordFragmentFactory(): PasswordFragment

    @ContributesAndroidInjector
    abstract fun provideBirthdayFragmentFactory(): BirthdayFragment

    @ContributesAndroidInjector
    abstract fun provideLoginFragmentFactory(): LoginFragment

    @ContributesAndroidInjector
    abstract fun provideForgotPasswordFragmentFactory(): ForgotPasswordFragment

    @ContributesAndroidInjector
    abstract fun provideResetPasswordFragmentFactory(): ResetPasswordFragment
}
