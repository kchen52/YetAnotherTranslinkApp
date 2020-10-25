package com.kchen52.yetanothertranslinkapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a bus entry in the bus list, as well as whether it's been enabled.
 */
@Entity
data class BusRoute(
    @PrimaryKey val shortName: String,
    @ColumnInfo(defaultValue = "0") val checked: Boolean,
    val longName: String
) {
    fun getFullRouteName(): String {
        return "$shortName $longName"
    }
}