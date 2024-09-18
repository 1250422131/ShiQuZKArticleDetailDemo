package com.imcys.shiquzkarticledetaildemo.ui.article.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.imcys.shiquzkarticledetaildemo.adapter.ArticleDetailContentAdapter
import com.imcys.shiquzkarticledetaildemo.base.BaseFragment
import com.imcys.shiquzkarticledetaildemo.databinding.FragmentArticleDetailContentBinding
import com.imcys.shiquzkarticledetaildemo.model.ArticleDetailData


class ArticleDetailContentFragment(private val content: ArticleDetailData.Content) :
    BaseFragment<FragmentArticleDetailContentBinding>() {


    private val articleDetailContentAdapter = ArticleDetailContentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initView()
        return binding.root
    }

    private fun initView() {
        initContentRecyclerView()
        initContent()
    }

    private fun initContentRecyclerView() {
        binding.apply {
            articleDetailContentRv.adapter = articleDetailContentAdapter
            articleDetailContentRv.layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }
            articleDetailContentAdapter.dataList = content.sentenceByXFList

            articleDetailContentAdapter.notifyDataSetChanged()
        }
    }

    private fun initContent() {
        binding.apply {

            // 设置封面图片
            val factory =
                DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

            Glide.with(this@ArticleDetailContentFragment).load(content.imgUrl)
                .transition(withCrossFade(factory))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(articleDetailContentCoverImage)


        }
    }

    override fun getViewBinding(): FragmentArticleDetailContentBinding =
        FragmentArticleDetailContentBinding.inflate(layoutInflater)

    companion object {

        @JvmStatic
        fun newInstance(content: ArticleDetailData.Content) =
            ArticleDetailContentFragment(content)
    }
}