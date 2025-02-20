package com.halfeaten.vaycray.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.halfeaten.vaycray.data.local.db.dao.DefaultListingDao
import com.halfeaten.vaycray.data.local.db.dao.InboxMsgDao
import com.halfeaten.vaycray.data.model.db.DefaultListing
import com.halfeaten.vaycray.data.model.db.Message


@Database(entities = [(Message::class), (DefaultListing::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun defaultListingDao(): DefaultListingDao

    abstract fun InboxMessage(): InboxMsgDao


}
