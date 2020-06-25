package com.kchen52.yetanothertranslinkapp.network
import com.google.gson.annotations.SerializedName


class TranslinkBusResponseBody : ArrayList<TranslinkBusResponseBodyItem>()

data class TranslinkBusResponseBodyItem(
    @SerializedName("Destination")
    val destination: String,
    @SerializedName("Direction")
    val direction: String,
    @SerializedName("Latitude")
    val latitude: Double,
    @SerializedName("Longitude")
    val longitude: Double,
    @SerializedName("Pattern")
    val pattern: String,
    @SerializedName("RecordedTime")
    val recordedTime: String,
    @SerializedName("RouteMap")
    val routeMap: RouteMap,
    @SerializedName("RouteNo")
    val routeNo: String,
    @SerializedName("TripId")
    val tripId: Int,
    @SerializedName("VehicleNo")
    val vehicleNo: String
)

data class RouteMap(
    @SerializedName("Href")
    val href: String
)