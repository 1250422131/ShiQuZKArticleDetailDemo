package com.imcys.shiquzkarticledetaildemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.imcys.shiqulibrarydemo.base.BaseRecyclerViewAdapter
import com.imcys.shiquzkarticledetaildemo.base.CommonViewHolder
import com.imcys.shiquzkarticledetaildemo.databinding.ItemArticleDetailContentBinding
import com.imcys.shiquzkarticledetaildemo.model.ArticleDetailData

class ArticleDetailContentAdapter :
    BaseRecyclerViewAdapter<ItemArticleDetailContentBinding, ArticleDetailData.Content.SentenceByXF>() {

    var dataList = listOf<ArticleDetailData.Content.SentenceByXF>()

    val dyedIndex = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommonViewHolder<ItemArticleDetailContentBinding> {
        val binding = ItemArticleDetailContentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommonViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(
        holder: CommonViewHolder<ItemArticleDetailContentBinding>,
        position: Int
    ) {
        val item = dataList[position]
        holder.binding.apply {
            // 3个步骤 1.替换空格 2.切割字符串 3.重新拼接并且添加分隔符
            itemArticleDetailText.text =
                item.word.replace(Regex("\\s"), "").split("").joinToString(separator = " ")
        }
    }
}