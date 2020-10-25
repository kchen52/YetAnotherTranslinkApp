package com.kchen52.yetanothertranslinkapp.repository

import androidx.room.Database
import androidx.room.Room
import com.kchen52.yetanothertranslinkapp.data.BusListBus
import com.kchen52.yetanothertranslinkapp.database.BusListDatabase

class BusListRepository(
    val database: BusListDatabase
) {
    fun getAllBuses(): List<BusListBus> {
        return listOf(BusListBus("TEST", true))
        //return database.busListDao().getAll()
    }
}