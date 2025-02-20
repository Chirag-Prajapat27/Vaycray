package com.halfeaten.vaycray.data.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencyRates")
data class CurrencyRates(
        @PrimaryKey
        val currencyCode: String,
        val rate: Double
)