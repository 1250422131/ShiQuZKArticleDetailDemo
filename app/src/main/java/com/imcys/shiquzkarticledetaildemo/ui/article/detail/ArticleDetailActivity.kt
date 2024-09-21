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
import androidx.viewpager2.widget.ViewPager2
import com.drake.brv.utils.grid
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.imcys.shiquzkarticledetaildemo.R
import com.imcys.shiquzkarticledetaildemo.adapter.MainHomeFragmentAdapter
import com.imcys.shiquzkarticledetaildemo.base.BaseActivity
import com.imcys.shiquzkarticledetaildemo.databinding.ActivityArticleDetailBinding
import com.imcys.shiquzkarticledetaildemo.databinding.ItemArticlePlayConfigBinding
import com.imcys.shiquzkarticledetaildemo.model.ArticleSettingInfo
import com.imcys.shiquzkarticledetaildemo.model.ArticleSettingType
import com.kongzue.dialogx.dialogs.WaitDialog
import com.kongzue.dialogx.interfaces.OnBindView
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

        WaitDialog.show("正在加载").setCustomView(object :
            OnBindView<WaitDialog?>(R.layout.dialog_load) {
            override fun onBind(dialog: WaitDialog?, v: View) {
            }
        })

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

        viewModel.apply {
            articleDetailData.observe(this@ArticleDetailActivity) {
                WaitDialog.dismiss()
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

            currentPlayState.observe(this@ArticleDetailActivity) { state ->
                if (state == Player.STATE_ENDED) {
                    updateShowPageTip(true)
                }
            }
            currentPage.observe(this@ArticleDetailActivity) {
                val totalPage = viewModel.articleDetailData.value?.contentList?.size ?: 0
                binding.articleDetailPageProgressTv.text = "${it + 1} / $totalPage"
                binding.articleDetailProgressBar.max = totalPage
                binding.articleDetailProgressBar.progress = it + 1
            }

            currentArticleSetting.observe(this@ArticleDetailActivity) {
                binding.articlePlayConfigRv.models = it.itemList
                binding.articlePlayConfigTitle.text = it.title
                if (it.type == ArticleSettingType.SPEED) {
                    viewModel.updateCurrentSpeed(it.value as Float)
                    when (it.value) {
                        1f -> {
                            binding.articlePlayConfigTitle.setCompoundDrawablesWithIntrinsicBounds(
                                getDrawable(R.drawable.icon_speed_1),
                                null, null, null
                            )
                        }

                        0.75f -> {
                            binding.articlePlayConfigTitle.setCompoundDrawablesWithIntrinsicBounds(
                                getDrawable(R.drawable.icon_speed_075),
                                null, null, null
                            )
                        }

                        1.25f -> {
                            binding.articlePlayConfigTitle.setCompoundDrawablesWithIntrinsicBounds(
                                getDrawable(R.drawable.icon_speed_125),
                                null, null, null
                            )
                        }
                    }
                } else if (it.type == ArticleSettingType.FONT_SIZE) {
                    when (it.value) {
                        20 -> {
                            binding.articlePlayConfigTitle.setCompoundDrawablesWithIntrinsicBounds(
                                getDrawable(R.drawable.icon_read_setting_medium),
                                null, null, null
                            )
                        }

                        15 -> {
                            binding.articlePlayConfigTitle.setCompoundDrawablesWithIntrinsicBounds(
                                getDrawable(R.drawable.icon_read_setting_small),
                                null, null, null
                            )
                        }

                        25 -> {
                            binding.articlePlayConfigTitle.setCompoundDrawablesWithIntrinsicBounds(
                                getDrawable(R.drawable.icon_read_setting_big),
                                null, null, null
                            )
                        }
                    }
                }
            }

            showSettingState.observe(this@ArticleDetailActivity) {
                if (it) {
                    binding.articlePlaySettingCard.visibility = View.VISIBLE
                } else {
                    binding.articlePlaySettingCard.visibility = View.GONE
                }
                binding.articlePlayConfigCard.visibility = View.GONE
            }

        }
    }

    private fun initView() {
        initContent()
        initSettingView()
        initSettingConfigRV()
    }

    private fun initSettingConfigRV() {
        binding.apply {
            articlePlayConfigRv.grid(3).setup {
                addType<ArticleSettingInfo.ArticleSettingItemInfo<Float>>(R.layout.item_article_play_config)
                onBind {
                    val model =
                        getModel<ArticleSettingInfo.ArticleSettingItemInfo<Any>>()
                    getBinding<ItemArticlePlayConfigBinding>().apply {
                        configNameTv.text = model.name
                        if (model.value == viewModel.currentArticleSetting.value?.value) {
                            configLy.setBackgroundColor(resources.getColor(R.color.primary))
                            configNameTv.setTextColor(resources.getColor(R.color.white))
                        } else {
                            configLy.setBackgroundColor(Color.parseColor("#d9cfea"))
                            configNameTv.setTextColor(resources.getColor(R.color.black))
                        }
                    }
                }

                onClick(R.id.cardView) {
//                    if (viewModel.currentArticleSetting.value?.type == ArticleSettingType.SPEED) {
//
//                    } else {
//
//                    }

                    val model =
                        getModel<ArticleSettingInfo.ArticleSettingItemInfo<Any>>()
                    val newModel =
                        viewModel.currentArticleSetting.value?.copy(value = model.value)
                    viewModel.updateCurrentArticleSetting(newModel)
                }

            }

        }
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
                            viewModel.loadSpeedConfig()
                        }

                        R.id.article_play_setting_font_size_model_tv -> {
                            viewModel.loadFontSizeConfig()
                        }
                    }

                    // 倍速播放
                    articlePlayConfigCard.visibility = View.VISIBLE
                    articlePlaySettingCard.visibility = View.GONE

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