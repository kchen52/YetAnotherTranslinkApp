package com.kchen52.yetanothertranslinkapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a bus entry in the bus list, as well as whether it's been enabled.
 */
@Entity
data class BusListBus(
    @PrimaryKey val name: String,
    val checked: Boolean
)