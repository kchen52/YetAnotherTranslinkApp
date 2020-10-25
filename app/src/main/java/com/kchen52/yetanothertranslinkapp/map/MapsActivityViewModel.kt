package com.kchen52.yetanothertranslinkapp.map

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.kchen52.yetanothertranslinkapp.R
import com.kchen52.yetanothertranslinkapp.network.HeaderInterceptor
import com.kchen52.yetanothertranslinkapp.network.TranslinkApi
import com.kchen52.yetanothertranslinkapp.network.TranslinkBusResponseBody
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

@ExperimentalCoroutinesApi
class MapsActivityViewModel(
    private val context: Context,
    private val coroutineScope: CoroutineScope
): ViewModel() {
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(HeaderInterceptor()).build()

    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .baseUrl("https://api.translink.ca/")
        .build()
    private val translinkApi = retrofit.create(TranslinkApi::class.java)

    private val state: MutableStateFlow<MapsActivityState> = MutableStateFlow(
        MapsActivityState.DataState(
            TranslinkBusResponseBody(),
            "",
            null,
            null,
            null
        )
    )

    fun getMapsActivityState(): StateFlow<MapsActivityState> {
        return state
    }

    fun onIntent(intent: MapsActivityIntents) {
        if (intent is MapsActivityIntents.LoadBuses) {
            if (intent.buses.isEmpty()) {
                state.value = MapsActivityState.ErrorState(
                    Exception("Please select at least one bus from the settings menu.")
                )
                return
            }
            state.value = MapsActivityState.LoadingState
            // Start getting buses
            coroutineScope.launch {
                val buses = try { fetchBuses(intent.buses) ?: TranslinkBusResponseBody()
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    TranslinkBusResponseBody()
                }
                // No camera change, pass null values for long, lat, and zoom
                state.value = MapsActivityState.DataState(
                    buses,
                    Calendar.getInstance().time.toString(),
                    null,
                    null,
                    null
                )
            }
        }
    }

    suspend fun fetchBuses(buses: IntArray): TranslinkBusResponseBody? {
        return translinkApi.getBuses(
            context.getString(R.string.translink_api_default),
            buses.joinToString()
        ).body()
    }
}
