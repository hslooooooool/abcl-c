package qsos.core.player.audio

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import qsos.lib.base.utils.ToastUtils

/**
 * @author : 华清松
 * 语音消息播放工具类
 */
open class AudioPlayerHelper {
    private val mMediaPlayer = MediaPlayer()
    private var mPlayerListener: PlayerListener? = null
    private lateinit var mPlayerModeManager: PlayerModeManager
    private lateinit var mBuild: PlayBuild

    /**是否已停止*/
    private var hasStop = false
    /**当前播放状态*/
    private var currentState = State.STOP
    private val handler = Handler(Looper.getMainLooper())

    interface PlayerListener {
        fun onPlayerStop()
    }

    /**初始化语音播放控制器*/
    open fun init(build: PlayBuild): AudioPlayerHelper {
        this.mBuild = build
        mPlayerModeManager = PlayerModeManager()
        mPlayerModeManager.init(build.context)
        mPlayerModeManager.setSpeakerOn(true)
        mPlayerModeManager.onStop()

        currentState = State.STOP

        setOnListener()
        return this
    }

    /**播放完毕监听*/
    private fun setOnListener() {
        mPlayerListener = mBuild.listener
        mMediaPlayer.setOnPreparedListener {
            if (hasStop) {
                mMediaPlayer.stop()
                currentState = State.STOP
                mPlayerListener?.onPlayerStop()
            } else {
                mMediaPlayer.start()
                currentState = State.PLAYING
            }
        }
        mMediaPlayer.setOnCompletionListener {
            currentState = State.STOP
            mPlayerModeManager.onStop()
            mPlayerListener?.onPlayerStop()
        }
        mMediaPlayer.setOnErrorListener { _, _, _ ->
            currentState = State.STOP
            mPlayerModeManager.onStop()
            mPlayerListener?.onPlayerStop()
            false
        }
    }

    /**播放音频文件*/
    open fun play() {
        if (TextUtils.isEmpty(mBuild.value)) {
            return
        }
        if (currentState == State.PREPARING) {
            return
        }
        if (currentState == State.PLAYING) {
            mMediaPlayer.stop()
        }
        currentState = State.PREPARING
        mPlayerModeManager.onPlay()
        try {
            if (mPlayerModeManager.isReceiver()) {
                // 听筒时延迟0.5S中播放
                handler.postDelayed({
                    startPlay(mBuild)
                }, 500)
            } else {
                startPlay(mBuild)
            }
        } catch (e: Exception) {
            ToastUtils.showToast(mBuild.context, "播放出错了 $e")
        }
    }

    private fun startPlay(build: PlayBuild) {
        mMediaPlayer.reset()
        when (build.type) {
            PlayType.PATH -> {
                /**播放本地音频文件*/
                mMediaPlayer.setDataSource(build.value)
            }
            PlayType.URL -> {
                /**播放网络音频文件*/
                mMediaPlayer.setDataSource(build.context, Uri.parse(build.value))
            }
        }
        mMediaPlayer.prepareAsync()
        hasStop = false
    }

    fun stop() {
        if (currentState == State.STOP) return
        if (currentState == State.PREPARING) {
            hasStop = true
        } else if (currentState == State.PLAYING) {
            mMediaPlayer.stop()
        }
        mPlayerModeManager.onStop()
        currentState = State.STOP
        mPlayerListener?.onPlayerStop()

        destroy()
    }

    fun destroy() {
        if (currentState == State.STOP) return
        mMediaPlayer.reset()
        mMediaPlayer.release()
    }

    private enum class State {
        PREPARING,//准备中
        PLAYING,//播放中
        STOP//停止中
    }

    /**播放类型*/
    enum class PlayType {
        /**本地*/
        PATH,
        /**网络*/
        URL
    }

    /**
     * @param type 播放类型
     * @param value 播放路径或链接，根据类型判断
     * @param listener 播放结束监听
     * */
    data class PlayBuild(
            var context: Context,
            var type: PlayType,
            var value: String,
            var listener: PlayerListener? = null
    )

}