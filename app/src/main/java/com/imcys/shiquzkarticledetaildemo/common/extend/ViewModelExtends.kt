package com.imcys.shiquzkarticledetaildemo.common.extend

import androidx.lifecycle.ViewModel
import com.imcys.shiquzkarticledetaildemo.model.ApiResponse
import retrofit2.HttpException
import java.net.SocketTimeoutException

suspend fun <T> ViewModel.requestApi(job: suspend () -> ApiResponse<T>): ApiResponse<T> {
    val catchResult = runCatching { job() }
    return if (catchResult.isSuccess) {
        ApiResponse(200, catchResult.getOrNull()?.data, "请求成功")
    } else {
        when (val exception = catchResult.exceptionOrNull()) {
            is HttpException -> {
                ApiResponse(exception.code(), null, exception.message())
            }

            is SocketTimeoutException -> {
                ApiResponse(408, null, "请求超时")
            }

            else -> {
                ApiResponse(500, null, "请求失败:${exception?.message}")
            }
        }

    }
}