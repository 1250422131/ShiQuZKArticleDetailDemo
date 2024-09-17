package com.imcys.shiquzkarticledetaildemo.di

import com.imcys.shiquzkarticledetaildemo.retrofit.api.ShiQuArticleService
import com.imcys.shiquzkarticledetaildemo.retrofit.retrofit
import org.koin.dsl.module

val appModule = module {
    factory { retrofit.create(ShiQuArticleService::class.java) }
}