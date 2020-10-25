package com.kchen52.yetanothertranslinkapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kchen52.yetanothertranslinkapp.data.BusListBus

@Database(entities = [BusListBus::class], version = 1)
abstract class BusListDatabase: RoomDatabase() {
    abstract fun busListDao(): BusListDao
}