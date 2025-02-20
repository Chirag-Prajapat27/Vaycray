package com.halfeaten.vaycray.data.local.db

import androidx.paging.DataSource
import com.halfeaten.vaycray.data.model.db.DefaultListing
import com.halfeaten.vaycray.data.model.db.Message
import io.reactivex.rxjava3.core.Observable

interface DbHelper {


    fun insertDefaultListing(defaultListing: DefaultListing) : Observable<Boolean>


    //Message
    fun loadAllMessage(): DataSource.Factory<Int, Message>

    fun deleteMessage(): Observable<Boolean>


}