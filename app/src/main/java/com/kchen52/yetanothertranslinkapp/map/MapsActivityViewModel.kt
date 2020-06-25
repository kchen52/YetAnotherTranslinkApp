package com.kchen52.yetanothertranslinkapp.map

import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.kchen52.yetanothertranslinkapp.R
import com.kchen52.yetanothertranslinkapp.network.HeaderInterceptor
import com.kchen52.yetanothertranslinkapp.network.TranslinkApi
import com.kchen52.yetanothertranslinkapp.network.TranslinkBusResponseBody
import com.kchen52.yetanothertranslinkapp.network.TranslinkBusResponseBodyItem
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
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
        MapsActivityState.DataState(TranslinkBusResponseBody(), "")
    )

    fun onIntent(intent: MapsActivityIntents) {
        if (intent is MapsActivityIntents.LoadBuses) {
            state.onNext(MapsActivityState.LoadingState)
            // Start getting buses
            coroutineScope.launch {
                val buses = try { fetchBuses() ?: TranslinkBusResponseBody()
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    TranslinkBusResponseBody()
                }
                state.onNext(MapsActivityState.DataState(buses, Calendar.getInstance().time.toString()))
            }
        }
    }

    suspend fun fetchBuses(): TranslinkBusResponseBody? {
        return translinkApi.getBuses(
            "translink_api_key",
            "99"
        ).body()
    }
}
