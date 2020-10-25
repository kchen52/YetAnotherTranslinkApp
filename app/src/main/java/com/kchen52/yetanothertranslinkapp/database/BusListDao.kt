package com.kchen52.yetanothertranslinkapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kchen52.yetanothertranslinkapp.data.BusListBus

@Dao
interface BusListDao {
    @Query("SELECT * FROM buslistbus")
    fun getAll(): List<BusListBus>

    @Insert
    fun insertAll(buses: List<BusListBus>)
}