package com.halfeaten.vaycray.ui.inbox

import android.R.color
import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelGroup
import com.halfeaten.vaycray.GetAllThreadsQuery
import com.halfeaten.vaycray.R
import com.halfeaten.vaycray.ViewholderInboxListMessagesBindingModel_
import com.halfeaten.vaycray.ui.user_profile.UserProfileActivity
import java.lang.String


class InboxListEpoxyGroup (
        context : Context,
        currentPosition: Int,
        val item: GetAllThreadsQuery.Result?,
        val clickListener: (item: GetAllThreadsQuery.Result) -> Unit
) : EpoxyModelGroup(R.layout.model_inbox_group, buildModels(context,item, currentPosition, clickListener)) {

    init {
        id("InboxListEpoxyGroup - $currentPosition")
    }

}

fun buildModels(context: Context,item: GetAllThreadsQuery.Result?, currentPosition: Int, clickListener: (item: GetAllThreadsQuery.Result) -> Unit): List<EpoxyModel<*>> {
    val models = ArrayList<EpoxyModel<*>>()
    item?.let {
        var isRead = item.threadItem!!.isRead
        if ((item.threadItem!!.sentBy != item.guest)
                && item.threadItem!!.isRead == false) {
            isRead = false
        }else if(item.threadItem!!.sentBy!!.equals(item.guest)){
            isRead = true
        }else{
            isRead = true
        }
        var status = true
        if(item.threadItem?.type!!.equals("message")){
            status = false
        }
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val langType = preferences.getString("Locale.Helper.Selected.Language", "en")

        models.add(ViewholderInboxListMessagesBindingModel_()
                .id("viewholder- ${item.id}")
                .status(item.threadItem?.type!!)
                .isStatus(status)
                .avatar(item.hostProfile?.picture)
                .content(item.threadItem?.content)
                .createdAt(com.halfeaten.vaycray.util.Utils.inboxDateFormat(item.threadItem?.createdAt!!))
                .hostName(item.hostProfile?.firstName)
                .isRead(isRead)
                .onClick { _ -> clickListener(item) }
                .avatarClick(View.OnClickListener { _ ->
                    UserProfileActivity.openProfileActivity(context!!, it.hostProfile?.profileId!!,true)
                })
            .onBind { model, view, position ->
                var color=view.dataBinding.root.findViewById<TextView>(R.id.txt_content).currentTextColor
                val hexColor = String.format("#%06X", 0xFFFFFF and color)
            }

        )


    }
    return models
}
