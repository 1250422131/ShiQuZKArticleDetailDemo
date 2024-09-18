package com.imcys.shiquzkarticledetaildemo.ui.article.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.imcys.shiquzkarticledetaildemo.http.retrofit.api.ShiQuArticleService
import com.imcys.shiquzkarticledetaildemo.model.ArticleDetailData
import kotlinx.coroutines.launch

class ArticleDetailViewModel(private val shiQuArticleService: ShiQuArticleService) : ViewModel() {

    private lateinit var exoPlayer: ExoPlayer

    private val _articleDetailData = MutableLiveData<ArticleDetailData>()

    val articleDetailData: LiveData<ArticleDetailData>
        get() = _articleDetailData

    private val _currentPage = MutableLiveData(0)

    val currentPage: LiveData<Int>
        get() = _currentPage


    fun initExoPlayer(mExoPlayer: ExoPlayer) {
        exoPlayer = mExoPlayer.apply {
            // 禁止自动连播
            playWhenReady = false
            // 禁止自动播放
            repeatMode = ExoPlayer.REPEAT_MODE_OFF
        }
    }


    fun loadAudio(audioIndex: Int) {
        val url = articleDetailData.value?.contentList?.get(audioIndex)?.audioUrl ?: ""
        exoPlayer.setMediaItem(MediaItem.fromUri(url))
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun pauseAudio() {
        exoPlayer.pause()
    }

    fun resumeAudio() {
        // 判断是否未能播放并且准备就绪
        if (!exoPlayer.isPlaying && exoPlayer.playbackState == ExoPlayer.STATE_READY) {
            exoPlayer.play()
        }
    }

    fun releaseAudio() {
        exoPlayer.release()
    }

    fun updateCurrentPage(page: Int) {
        _currentPage.value = page
    }


    fun loadArticleDetail(aid: Int) {

        viewModelScope.launch {
            shiQuArticleService.getArticleDetail(aid).let {
                if (it.code == 0) {
                    it.data?.let { data -> _articleDetailData.value = data }
                } else {
                    Log.e(this.javaClass.name, "请求失败: ${it.msg}")
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
