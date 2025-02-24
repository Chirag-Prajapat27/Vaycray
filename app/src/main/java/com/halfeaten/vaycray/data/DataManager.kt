package com.halfeaten.vaycray.data

import androidx.lifecycle.MutableLiveData
import com.halfeaten.vaycray.data.local.db.DbHelper
import com.halfeaten.vaycray.data.local.prefs.PreferencesHelper
import com.halfeaten.vaycray.data.remote.ApiHelper
import com.halfeaten.vaycray.util.Event
import com.halfeaten.vaycray.vo.Outcome

interface DataManager: DbHelper, PreferencesHelper, ApiHelper {

    fun setUserAsLoggedOut()

    fun updateAccessToken(accezzToken: String?)

    fun generateFirebaseToken(): MutableLiveData<Event<Outcome<String>>>

    fun updateUserInfo(
            accezzToken: String?,
            userId: String?,
            loggedInMode: LoggedInMode,
            userName: String?,
            email: String?,
            profilePicPath: String?,
            currency: String?,
            language: String?,
            createdAt: String?
    )

    fun updateVerification(
            isPhoneVerification: Boolean?,
            isEmailConfirmed: Boolean?,
            isIdVerification: Boolean?,
            isGoogleConnected: Boolean?,
            isFacebookConnected: Boolean?,
    )

    fun isUserLoggedIn(): Boolean

    enum class LoggedInMode(val type: Int) {
        LOGGED_IN_MODE_LOGGED_OUT(0),
        LOGGED_IN_MODE_GOOGLE(1),
        LOGGED_IN_MODE_FB(2),
        LOGGED_IN_MODE_SERVER(3)
    }

    open class ListingApproval(val type: Int) {
        companion object {
            const val OPTIONAL = 0
            const val REQUIRED = 1
        }

    }
}
