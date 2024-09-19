package com.imcys.shiquzkarticledetaildemo.ui.article.detail

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.imcys.shiquzkarticledetaildemo.R
import com.imcys.shiquzkarticledetaildemo.adapter.MainHomeFragmentAdapter
import com.imcys.shiquzkarticledetaildemo.base.BaseActivity
import com.imcys.shiquzkarticledetaildemo.databinding.ActivityArticleDetailBinding
import com.imcys.shiquzkarticledetaildemo.databinding.ItemArticlePlayConfigBinding
import com.imcys.shiquzkarticledetaildemo.model.ArticleSettingInfo
import com.imcys.shiquzkarticledetaildemo.model.ArticleSettingType
import org.koin.androidx.viewmodel.ext.android.viewModel


class ArticleDetailActivity : BaseActivity<ActivityArticleDetailBinding>() {

    private val viewModel: ArticleDetailViewModel by viewModel()
    private lateinit var viewPage2Adapter: MainHomeFragmentAdapter
    private val gestureAnimator by lazy {
        ObjectAnimator.ofFloat(
            binding.articleDetailPageGestureImage,
            "translationX",
            -200f,
            0f
        ).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE // 无限重复
            repeatMode = ObjectAnimator.REVERSE // 反向重复
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left, systemBars.top, systemBars.right, systemBars.bottom
            )
            insets
        }

        // 初始化播放器
        initExoPlayer()

        // 初始化数据
        initView()

        // 绑定数据
        bindLiveData()

        // 加载数据
        viewModel.loadArticleDetail(6)
    }

    private fun initExoPlayer() {
        viewModel.initExoPlayer(ExoPlayer.Builder(this).build())
    }

    private fun bindLiveData() {
        viewModel.articleDetailData.observe(this) {
            val fragments = mutableListOf<Fragment>()
            it.contentList.forEach { content ->
                fragments.add(ArticleDetailContentFragment.newInstance(content, viewModel))
            }
            binding.apply {
                viewPage2Adapter.fragments = fragments
                viewPage2Adapter.notifyDataSetChanged()
                articleDetailTitleTv.text = it.title
            }
        }

        viewModel.currentPlayState.observe(this) { state ->
            if (state == Player.STATE_ENDED) {
                updateShowPageTip(true)
            }
        }


        viewModel.currentPage.observe(this) {
            val totalPage = viewModel.articleDetailData.value?.contentList?.size ?: 0
            binding.articleDetailPageProgressTv.text = "${it + 1} / $totalPage"
            binding.articleDetailProgressBar.max = totalPage
            binding.articleDetailProgressBar.progress = it + 1
        }

    }

    private fun initView() {
        initContent()
        initSettingView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSettingView() {
        binding.apply {
            (articlePlaySettingCard[0] as ConstraintLayout).forEach {
                it.setOnTouchListener { view, motionEvent ->

                    // 判断按下和放开
                    if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                        val textView = it as TextView
                        val drawable = textView.compoundDrawables[1]
                        val wrappedDrawable = DrawableCompat.wrap(drawable)
                        DrawableCompat.setTint(
                            wrappedDrawable,
                            resources.getColor(R.color.primary)
                        )
                        textView.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            wrappedDrawable,
                            null,
                            null
                        )
                        textView.setTextColor(resources.getColor(R.color.primary))

                    } else if (motionEvent.action == MotionEvent.ACTION_UP) {

                        val textView = it as TextView
                        val drawable = textView.compoundDrawables[1]
                        val wrappedDrawable = DrawableCompat.wrap(drawable)


                        DrawableCompat.setTint(
                            wrappedDrawable,
                            resources.getColor(R.color.black)
                        )

                        textView.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            wrappedDrawable,
                            null,
                            null
                        )
                        textView.setTextColor(resources.getColor(R.color.black))

                    }
                    false
                }


                // 点击事件

                it.setOnClickListener { view ->
                    when (view.id) {
                        R.id.article_play_setting_speed_model_tv -> {
                            // 倍速播放
                            val settingInfo = ArticleSettingInfo(
                                title = "播放倍速",
                                value = 1f,
                                type = ArticleSettingType.SPEED,
                                itemList = listOf(
                                    ArticleSettingInfo.ArticleSettingItemInfo(
                                        name = "0.5倍速",
                                        value = 0.5f
                                    ),
                                    ArticleSettingInfo.ArticleSettingItemInfo(
                                        name = "1倍速",
                                        value = 1f
                                    ),
                                    ArticleSettingInfo.ArticleSettingItemInfo(
                                        name = "1.5倍速", value = 1.5f
                                    )
                                )
                            )

                            articlePlayConfigCard.visibility = View.VISIBLE
                            articlePlaySettingCard.visibility = View.GONE

                            articlePlayConfigRv.linear().setup {
                                addType<ArticleSettingInfo.ArticleSettingItemInfo<Float>>(R.layout.item_article_play_config)
                                onBind {
                                    val model =
                                        getModel<ArticleSettingInfo.ArticleSettingItemInfo<Float>>()
                                    getBinding<ItemArticlePlayConfigBinding>().apply {
                                        configNameTv.text = model.name
                                    }
                                }

                                onClick(R.id.cardView) {

                                }

                            }.models = settingInfo.itemList
                            articlePlayConfigRv.layoutManager = GridLayoutManager(this@ArticleDetailActivity, 3)

                        }
                    }

                }


            }
        }
    }


    private fun updateShowPageTip(isShow: Boolean) {
        binding.apply {
            if (isShow) {
                articleDetailPageTipGroup.visibility = View.VISIBLE
                gestureAnimator.start()
            } else {
                articleDetailPageTipGroup.visibility = View.GONE
                gestureAnimator.cancel()
            }
        }
    }

    private fun initContent() {
        binding.apply {
            viewPage2Adapter =
                MainHomeFragmentAdapter(supportFragmentManager, lifecycle, listOf())
            articleDetailVp2.adapter = viewPage2Adapter
            // 监听滚动
            articleDetailVp2.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // 加载音频
                    viewModel.loadAudio(position)
                    // 更新当前页码
                    viewModel.updateCurrentPage(position)
                    // 隐藏提示
                    updateShowPageTip(false)
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }
            })
        }
    }

    override fun onResume() {
        viewModel.resumeAudio()
        super.onResume()
    }

    override fun onStop() {
        viewModel.pauseAudio()
        super.onStop()
    }

    override fun onDestroy() {
        viewModel.releaseAudio()
        super.onDestroy()
    }


    override fun getViewBinding(): ActivityArticleDetailBinding =
        ActivityArticleDetailBinding.inflate(layoutInflater)
}