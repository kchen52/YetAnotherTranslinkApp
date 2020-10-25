package com.kchen52.yetanothertranslinkapp.buslist

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.kchen52.yetanothertranslinkapp.R
import com.kchen52.yetanothertranslinkapp.data.BusListBus
import com.kchen52.yetanothertranslinkapp.database.BusListDatabase
import com.kchen52.yetanothertranslinkapp.repository.BusListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class BusListViewModel(
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + Job()),
    private val busListRepository: BusListRepository = BusListRepository(
        Room.databaseBuilder(
            context,
            BusListDatabase::class.java, "bus-list-database"
        ).build()
    )
): ViewModel() {

    fun getRequestedBuses(): Flow<List<BusListBus>> = flow {
        emit(busListRepository.getAllBuses())
    }
}