package com.imcys.shiquzkarticledetaildemo.ui.article.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.media3.exoplayer.ExoPlayer
import androidx.viewpager2.widget.ViewPager2
import com.imcys.shiquzkarticledetaildemo.R
import com.imcys.shiquzkarticledetaildemo.adapter.MainHomeFragmentAdapter
import com.imcys.shiquzkarticledetaildemo.base.BaseActivity
import com.imcys.shiquzkarticledetaildemo.databinding.ActivityArticleDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArticleDetailActivity : BaseActivity<ActivityArticleDetailBinding>() {

    private val viewModel: ArticleDetailViewModel by viewModel()
    private lateinit var viewPage2Adapter: MainHomeFragmentAdapter

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
                fragments.add(ArticleDetailContentFragment.newInstance(content))
            }
            binding.apply {
                viewPage2Adapter.fragments = fragments
                viewPage2Adapter.notifyDataSetChanged()
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
    }

    private fun initContent() {
        binding.apply {
            viewPage2Adapter = MainHomeFragmentAdapter(supportFragmentManager, lifecycle, listOf())
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