package com.kchen52.yetanothertranslinkapp.map

sealed class MapsActivityIntents {
    class LoadBuses(val buses: IntArray): MapsActivityIntents()
}