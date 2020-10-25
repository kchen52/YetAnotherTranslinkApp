package com.kchen52.yetanothertranslinkapp.repository

import com.kchen52.yetanothertranslinkapp.data.BusRoute
import com.kchen52.yetanothertranslinkapp.database.BusRouteDatabase

class BusListRepository(
    val database: BusRouteDatabase
) {
    fun getAllBuses(): List<BusRoute> {
        return database.busRouteDao().getAll()
    }
}