package com.imcys.shiquzkarticledetaildemo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 接口返回数据
 */
@Serializable
data class ApiResponse<T>(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val data: T?,
    @SerialName("msg")
    val msg: String
)