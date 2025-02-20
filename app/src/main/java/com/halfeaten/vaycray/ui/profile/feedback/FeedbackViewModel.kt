package com.halfeaten.vaycray.ui.profile.feedback

import androidx.databinding.ObservableField
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.SendUserFeedbackMutation
import com.halfeaten.vaycray.data.DataManager
import com.halfeaten.vaycray.ui.base.BaseViewModel
import com.halfeaten.vaycray.util.performOnBackOutOnMain
import com.halfeaten.vaycray.util.resource.ResourceProvider
import com.halfeaten.vaycray.util.rx.Scheduler
import com.halfeaten.vaycray.util.toOptional
import javax.inject.Inject

class FeedbackViewModel @Inject constructor(
        dataManager: DataManager,
        private val scheduler: Scheduler,
        val resourceProvider: ResourceProvider
): BaseViewModel<FeedbackNavigator>(dataManager, resourceProvider) {

    val msg = ObservableField("")
    val feedbackType = ObservableField("")


    fun sendFeedback(typeOfFeedback : String, msg : String){
        try {
            val mutate = SendUserFeedbackMutation(
                    type = typeOfFeedback.toOptional(),
                    message = msg.toOptional()
            )

            compositeDisposable.add(dataManager.sendfeedBack(mutate)
                    .performOnBackOutOnMain(scheduler)
                    .subscribe({ response ->
                        try {
                            val data = response.data!!.userFeedback
                            if (data?.status == 200) {
                                if (typeOfFeedback.equals("Feed Back")){
                                    navigator.showToast(resourceProvider.getString(R.string.feedback_sent))
                                }else {
                                    navigator.showToast(resourceProvider.getString(R.string.your_feedback)+" "+ typeOfFeedback + " "+resourceProvider.getString(R.string.has_been_sent))
                                }
                            } else if (data?.status == 500) {
                                navigator.openSessionExpire("FeedbackVM")
                            } else {
                                if (data?.errorMessage==null){
                                    navigator.showError()
                                }else{
                                    navigator.showToast(data.errorMessage.toString())
                                }

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            navigator.showError()
                        }
                    }, {
                        handleException(it)
                    } )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            navigator.showError()
        }
    }

}