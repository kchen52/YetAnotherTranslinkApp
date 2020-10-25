package com.kchen52.yetanothertranslinkapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kchen52.yetanothertranslinkapp.data.BusRoute

@Dao
interface BusListDao {
    @Query("SELECT * FROM busroute")
    fun getAll(): List<BusRoute>

    @Insert
    fun insertAll(buses: List<BusRoute>)
}