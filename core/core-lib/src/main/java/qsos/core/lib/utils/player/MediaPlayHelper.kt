package qsos.core.lib.utils.player

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import qsos.lib.base.utils.ToastUtils

/**
 * @author : 华清松
 * 音频播放工具类，支持网络与本地播放，支持类型以终端支持为准
 */
object MediaPlayHelper {

    private val mediaPlayer = MediaPlayer()
    private lateinit var playerListener: PlayerListener

    /**是否已停止*/
    private var hasStop = false
    /**当前播放状态*/
    private var currentState = MediaPlayHelper.State.STOP
    private val handler = Handler(Looper.getMainLooper())

    interface PlayerListener {
        fun onPlayerStop()
    }

    /**初始化语音播放控制器*/
    fun init(context: Context): MediaPlayHelper {
        MediaPlayManager.init(context)
        MediaPlayManager.isSpeakerOn()
        MediaPlayManager.isReceiver()
        MediaPlayManager.onPlay()
        MediaPlayManager.onStop()
        MediaPlayManager.setSpeakerOn(true)

        currentState = MediaPlayHelper.State.STOP
        return this
    }

    /**播放完毕监听*/
    fun listener(listener: PlayerListener): MediaPlayHelper {
        playerListener = listener
        mediaPlayer.setOnPreparedListener {
            if (hasStop) {
                mediaPlayer.stop()
                currentState = MediaPlayHelper.State.STOP
                playerListener.onPlayerStop()
            } else {
                mediaPlayer.start()
                currentState = MediaPlayHelper.State.PLAYING
            }
        }
        mediaPlayer.setOnCompletionListener {
            currentState = MediaPlayHelper.State.STOP
            listener.onPlayerStop()
            MediaPlayManager.onStop()
        }
        mediaPlayer.setOnErrorListener { _, _, _ ->
            currentState = MediaPlayHelper.State.STOP
            MediaPlayManager.onStop()
            false
        }
        return this
    }

    /**播放音频文件*/
    fun play(build: PlayBuild) {
        if (TextUtils.isEmpty(build.value)) {
            return
        }
        if (currentState == MediaPlayHelper.State.PREPARING) {
            return
        }
        if (currentState == MediaPlayHelper.State.PLAYING) {
            mediaPlayer.stop()
        }
        currentState = MediaPlayHelper.State.PREPARING
        MediaPlayManager.onPlay()
        try {
            if (MediaPlayManager.isReceiver()) {
                // 听筒时延迟1S中播放
                handler.postDelayed({
                    startPlay(build)
                }, 1000)
            } else {
                startPlay(build)
            }
        } catch (e: Exception) {
            ToastUtils.showToast(build.context, "播放出错了 $e")
        }
    }

    /**开始播放*/
    private fun startPlay(build: PlayBuild) {
        mediaPlayer.reset()
        when (build.type) {
            MediaPlayHelper.PlayType.PATH -> {
                /**播放本地音频文件*/
                mediaPlayer.setDataSource(build.value)
            }
            MediaPlayHelper.PlayType.URL -> {
                /**播放网络音频文件*/
                mediaPlayer.setDataSource(build.context, Uri.parse(build.value))
            }
        }
        mediaPlayer.prepareAsync()
        hasStop = false
    }

    /**停止播放*/
    fun stop() {
        if (currentState == MediaPlayHelper.State.STOP) return
        if (currentState == MediaPlayHelper.State.PREPARING) {
            hasStop = true
        } else if (currentState == MediaPlayHelper.State.PLAYING) {
            mediaPlayer.stop()
        }
        playerListener.onPlayerStop()
        MediaPlayManager.onStop()
        currentState = MediaPlayHelper.State.STOP
    }

    /**销毁，释放资源*/
    fun destroy() {
        if (currentState == MediaPlayHelper.State.STOP) return
        mediaPlayer.reset()
        mediaPlayer.release()
    }

    /**播放状态*/
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
     */
    data class PlayBuild(
            var context: Context,
            var type: PlayType,
            var value: String
    )

}