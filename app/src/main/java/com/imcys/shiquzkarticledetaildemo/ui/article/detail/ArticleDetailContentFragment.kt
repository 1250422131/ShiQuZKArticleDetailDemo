package com.imcys.shiquzkarticledetaildemo.ui.article.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.Player
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.imcys.shiquzkarticledetaildemo.R
import com.imcys.shiquzkarticledetaildemo.adapter.ArticleDetailContentAdapter
import com.imcys.shiquzkarticledetaildemo.base.BaseFragment
import com.imcys.shiquzkarticledetaildemo.databinding.FragmentArticleDetailContentBinding
import com.imcys.shiquzkarticledetaildemo.model.ArticleDetailData
import com.imcys.shiquzkarticledetaildemo.model.ArticleSettingType


class ArticleDetailContentFragment(
    private val content: ArticleDetailData.Content,
    private val viewModel: ArticleDetailViewModel
) :
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
        bindLiveData()
        return binding.root
    }

    private fun bindLiveData() {
        viewModel.apply {
            currentPlayerTime.observe(viewLifecycleOwner) { currentTime ->
                val dyedIndex =
                    content.sentenceByXFList.indexOfFirst {
                        it.wb <= currentTime && it.we >= currentTime
                    }
                val oldIndex = articleDetailContentAdapter.dyedIndex
                articleDetailContentAdapter.dyedIndex = dyedIndex
                articleDetailContentAdapter.notifyItemChanged(oldIndex)
                articleDetailContentAdapter.notifyItemChanged(dyedIndex)
            }

            currentPlayWhenReady.observe(viewLifecycleOwner) { state ->
                // 判断是否在播放
                updateShowPlayState(state)
            }

            currentPlayState.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it == Player.STATE_ENDED) {
                        updateShowPlayState(false)
                    }
                }
            }

            currentArticleSetting.observe(viewLifecycleOwner) {
                if (it.type == ArticleSettingType.FONT_SIZE) {
                    articleDetailContentAdapter.textSize = it.value as Int
                    articleDetailContentAdapter.notifyDataSetChanged()
                }
            }

        }


    }


    private fun updateShowPlayState(isPlay: Boolean) {

        binding.apply {
            if (isPlay) {
                Glide.with(this@ArticleDetailContentFragment)
                    .load(R.drawable.ic_read_details_replay_anim)
                    .into(binding.articleDetailPlayStateImage)
                binding.articleDetailPlayImage.setImageResource(R.drawable.ic_read_start_play)
            } else {
                articleDetailPlayStateImage.setImageResource(R.drawable.ic_read_details_replay)
                articleDetailPlayImage.setImageResource(R.drawable.ic_read_pause_play)
            }
        }
    }

    private fun initView() {
        // 初始化文章内容RV
        initContentRecyclerView()
        // 初始化内容区域
        initContent()
        // 初始化播放按钮
        initPlayerButton()
        // 初始化设置点击区域 -> 点击后阅读设置出现
        initSettingClickView()
    }

    private fun initSettingClickView() {
        binding.apply {
            articleDetailSettingView.setOnClickListener {
                viewModel.showSettingState.value?.let {
                    viewModel.updateShowSettingState(!it)
                }

            }
        }
    }

    private fun initPlayerButton() {

        binding.apply {

            articleDetailPlayImage.setOnClickListener {
                viewModel.playOrPauseAudio()
            }

            articleDetailPlayStateImage.setOnClickListener {
                viewModel.playOrPauseAudio()
            }

        }
    }

    private fun initContentRecyclerView() {
        binding.apply {
            articleDetailContentRv.adapter = articleDetailContentAdapter
            // 禁止动画
            articleDetailContentRv.itemAnimator = null
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
        fun newInstance(content: ArticleDetailData.Content, viewModel: ArticleDetailViewModel) =
            ArticleDetailContentFragment(content, viewModel)
    }
}