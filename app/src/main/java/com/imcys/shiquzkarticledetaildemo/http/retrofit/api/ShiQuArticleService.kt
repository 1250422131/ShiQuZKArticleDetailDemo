package com.imcys.shiquzkarticledetaildemo.http.retrofit.api

import com.imcys.shiquzkarticledetaildemo.model.ApiResponse
import com.imcys.shiquzkarticledetaildemo.model.ArticleDetailData
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * 绘本详情的API
 */
interface ShiQuArticleService {

    @GET("knowledge/article/getArticleDetail")
    suspend fun getArticleDetail(@Query("aid") aid: Int): ApiResponse<ArticleDetailData>


}