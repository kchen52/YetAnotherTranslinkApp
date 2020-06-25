package com.kchen52.yetanothertranslinkapp.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds any necessary headers for communicating with the Translink API
 */
class HeaderInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.request()
            .newBuilder()
            .apply { addHeader("Accept", "application/json") }
            .run {
                chain.proceed(build())
            }
    }

}