package com.imcys.shiquzkarticledetaildemo.ui.article.detail

import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.imcys.shiquzkarticledetaildemo.http.retrofit.api.ShiQuArticleService
import com.imcys.shiquzkarticledetaildemo.model.ArticleDetailData
import com.imcys.shiquzkarticledetaildemo.model.ArticleSettingInfo
import com.imcys.shiquzkarticledetaildemo.model.ArticleSettingType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.fixedRateTimer


class ArticleDetailViewModel(private val shiQuArticleService: ShiQuArticleService) : ViewModel() {

    private lateinit var exoPlayer: ExoPlayer

    private val _articleDetailData = MutableLiveData<ArticleDetailData>()

    val articleDetailData: LiveData<ArticleDetailData>
        get() = _articleDetailData

    private val _currentPage = MutableLiveData(0)

    val currentPage: LiveData<Int>
        get() = _currentPage

    private val _currentPlayerTime = MutableLiveData<Int>()

    val currentPlayerTime: LiveData<Int>
        get() = _currentPlayerTime


    private val _currentPlayWhenReady = MutableLiveData<Boolean>()

    val currentPlayWhenReady: LiveData<Boolean>
        get() = _currentPlayWhenReady

    private val _currentPlayState = MutableLiveData<Int>()

    val currentPlayState: LiveData<Int>
        get() = _currentPlayState

    private val _currentSpeed = MutableLiveData(1f)

    val currentSpeed: LiveData<Float>
        get() = _currentSpeed

    private val _currentArticleSpeedSetting = MutableLiveData(
        ArticleSettingInfo(
            title = "播放倍速",
            value = 1f,
            type = ArticleSettingType.SPEED,
            itemList = listOf(
                ArticleSettingInfo.ArticleSettingItemInfo(
                    name = "慢",
                    value = 0.75f
                ),
                ArticleSettingInfo.ArticleSettingItemInfo(
                    name = "标准",
                    value = 1f
                ),
                ArticleSettingInfo.ArticleSettingItemInfo(
                    name = "快", value = 1.5f
                )
            )
        )
    )

    val currentArticleSpeedSetting: LiveData<ArticleSettingInfo<Float>>
        get() = _currentArticleSpeedSetting

    private var exoPlayerTimer: Timer? = null

    fun updateCurrentArticleSetting(articleSettingInfo: ArticleSettingInfo<Float>?) {
        _currentArticleSpeedSetting.postValue(articleSettingInfo)
    }

    @OptIn(UnstableApi::class)
    fun initExoPlayer(mExoPlayer: ExoPlayer) {
        exoPlayer = mExoPlayer.apply {
            // 禁止自动连播
            playWhenReady = false
            // 禁止自动播放
            repeatMode = ExoPlayer.REPEAT_MODE_OFF
            addListener(object : Player.Listener {

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    _currentPlayState.postValue(playbackState)
                }

                override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                    _currentPlayWhenReady.postValue(playWhenReady)
                    super.onPlayWhenReadyChanged(playWhenReady, reason)
                }
            })
        }
    }


    fun updateCurrentSpeed(speed: Float) {
        exoPlayer.playbackParameters = PlaybackParameters(speed)
        _currentSpeed.postValue(speed)
    }


    fun playOrPauseAudio() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            if (_currentPlayState.value == Player.STATE_ENDED) {
                // 重置播放
                exoPlayer.seekTo(0)
            }
            exoPlayer.play()
        }
    }


    fun loadAudio(audioIndex: Int) {
        val url = articleDetailData.value?.contentList?.get(audioIndex)?.audioUrl ?: ""
        exoPlayer.setMediaItem(MediaItem.fromUri(url))
        exoPlayer.prepare()
        exoPlayer.play()
        _currentPlayWhenReady.postValue(true)


        exoPlayerTimer?.cancel() // 确保先取消之前的定时器

        viewModelScope.launch(Dispatchers.IO) {
            exoPlayerTimer = fixedRateTimer("音频播放监控", false, 0, 10) {
                viewModelScope.launch(Dispatchers.Main) {
                    val initTime =
                        _articleDetailData.value?.contentList?.get(audioIndex)?.sentenceByXFList?.first()?.wb
                            ?: 0
                    _currentPlayerTime.postValue(100 - initTime + exoPlayer.currentPosition.toInt())
                }
            }
        }

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
