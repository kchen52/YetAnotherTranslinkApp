package com.kchen52.yetanothertranslinkapp.map

import com.kchen52.yetanothertranslinkapp.network.TranslinkBusResponseBody

sealed class MapsActivityState {
    class DataState(
        val buses: TranslinkBusResponseBody,
        val timeUpdated: String,
        val longitude: Float?,
        val latitude: Float?,
        val zoom: Float?
    ): MapsActivityState()
    class ErrorState(val exception: Exception): MapsActivityState()
    object LoadingState: MapsActivityState()
}