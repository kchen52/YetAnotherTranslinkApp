package com.kchen52.yetanothertranslinkapp.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslinkApi {
    /**
     * Returns
     */
    @GET("rttiapi/v1/buses")
    suspend fun getBuses(
        @Query("apiKey") apiKey: String,
        @Query("routeNo") routes: String
    ): Response<TranslinkBusResponseBody>
}