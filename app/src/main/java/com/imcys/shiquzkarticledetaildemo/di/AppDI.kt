package com.imcys.shiquzkarticledetaildemo.di

import com.imcys.shiquzkarticledetaildemo.http.retrofit.api.ShiQuArticleService
import com.imcys.shiquzkarticledetaildemo.http.retrofit.retrofit
import com.imcys.shiquzkarticledetaildemo.ui.article.detail.ArticleDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    factory { retrofit.create(ShiQuArticleService::class.java) }
    viewModel { ArticleDetailViewModel(get()) }
}