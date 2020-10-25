package com.kchen52.yetanothertranslinkapp.buslist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.kchen52.yetanothertranslinkapp.data.BusRoute
import com.kchen52.yetanothertranslinkapp.database.BusRouteDatabase
import com.kchen52.yetanothertranslinkapp.repository.BusRouteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BusListViewModel(
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + Job()),
    private val busListRepository: BusRouteRepository = BusRouteRepository(
        Room.databaseBuilder(
            context,
            BusRouteDatabase::class.java,
            "bus-route-database.db"
        ).createFromAsset("bus-route-database.db")
        .build()
    )
): ViewModel() {

    fun getRequestedBuses(): Flow<List<BusRoute>> = flow {
        emit(busListRepository.getAllBuses())
    }
}