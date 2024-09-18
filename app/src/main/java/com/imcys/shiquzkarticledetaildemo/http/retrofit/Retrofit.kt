package com.imcys.shiquzkarticledetaildemo.http.retrofit

import com.imcys.shiquzkarticledetaildemo.common.utils.HttpsUtils
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory


private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(DebugCookieInterceptor()).apply {
        val sSLSocketFactory = HttpsUtils.getSslSocketFactory().sSLSocketFactory
        val x509TrustManager = HttpsUtils.getSslSocketFactory().trustManager
        sslSocketFactory(sSLSocketFactory, x509TrustManager)
    }
    .build()

val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .addConverterFactory(
        Json.asConverterFactory(
            MediaType.get("application/json; charset=UTF8")
        )
    )
    .baseUrl("https://shiqu.zhilehuo.com/")
    .build()


class DebugCookieInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = request.newBuilder()
            .addHeader("Cookie", "sid=i5VMMK2c7EEm5qK597kJeDqrel7NKCRqSQRGQn8mJBs%3d")
            .build()
        return chain.proceed(newRequest)
    }
}
