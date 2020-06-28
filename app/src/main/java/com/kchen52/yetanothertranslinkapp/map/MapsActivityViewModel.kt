package com.kchen52.yetanothertranslinkapp.map

import com.google.gson.GsonBuilder
import com.kchen52.yetanothertranslinkapp.network.HeaderInterceptor
import com.kchen52.yetanothertranslinkapp.network.TranslinkApi
import com.kchen52.yetanothertranslinkapp.network.TranslinkBusResponseBody
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MapsActivityViewModel(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + Job())
) {
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(HeaderInterceptor()).build()

    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .baseUrl("https://api.translink.ca/")
        .build()
    private val translinkApi = retrofit.create(TranslinkApi::class.java)

    val state: BehaviorSubject<MapsActivityState> = BehaviorSubject.createDefault(
        MapsActivityState.DataState(
            TranslinkBusResponseBody(),
            "",
            null,
            null,
            null
        )
    )

    fun onIntent(intent: MapsActivityIntents) {
        if (intent is MapsActivityIntents.LoadBuses) {
            if (intent.buses.isEmpty()) {
                state.onNext(MapsActivityState.ErrorState(
                    Exception("Please select at least one bus from the settings menu."))
                )
                return
            }
            state.onNext(MapsActivityState.LoadingState)
            // Start getting buses
            coroutineScope.launch {
                val buses = try { fetchBuses(intent.buses) ?: TranslinkBusResponseBody()
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    TranslinkBusResponseBody()
                }
                state.onNext(
                    // No camera change, pass null values for long, lat, and zoom
                    MapsActivityState.DataState(
                        buses,
                        Calendar.getInstance().time.toString(),
                        null,
                        null,
                        null
                    )
                )
            }
        }
    }

    suspend fun fetchBuses(buses: IntArray): TranslinkBusResponseBody? {
        return translinkApi.getBuses(
            buses.joinToString()
        ).body()
    }
}
