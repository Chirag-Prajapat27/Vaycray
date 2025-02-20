package com.halfeaten.vaycray.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.halfeaten.vaycray.data.model.db.DefaultListing

@Dao
interface DefaultListingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(defaultListing: DefaultListing)
}