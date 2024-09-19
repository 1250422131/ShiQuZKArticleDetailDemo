package com.imcys.shiquzkarticledetaildemo.model

enum class ArticleSettingType {
    AUTO_PAGE,
    SPEED,
    FONT_SIZE,
}

data class ArticleSettingInfo<T>(
    val type: ArticleSettingType,
    val title: String,
    val value: T,
    val itemList: List<ArticleSettingItemInfo<T>>
) {

    data class ArticleSettingItemInfo<T>(val name: String, val value: T)
}