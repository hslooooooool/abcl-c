package qsos.core.player.config

import android.content.Context
import qsos.core.player.audio.AudioPlayerHelper
import qsos.core.player.data.PreAudioEntity
import qsos.core.player.data.PreDocumentEntity
import qsos.core.player.data.PreImageEntity
import qsos.core.player.data.PreVideoEntity
import qsos.lib.base.callback.OnTListener

/**
 * @author : 华清松
 * 媒体预览配置清单
 */
interface IPlayerConfig {

    /**预览图片
     * @param position 预览图片位置
     * @param list 图片集合
     * */
    fun previewImage(context: Context, position: Int = 0, list: List<PreImageEntity>)

    /**预览视频
     * @param position 预览视频位置
     * @param list 视频集合
     * */
    fun previewVideo(context: Context, position: Int = 0, list: List<PreVideoEntity>)

    /**预览音频
     * @param position 预览音频位置
     * @param list 音频集合
     * @param onPlayerListener 播放状态监听
     * */
    fun previewAudio(
            context: Context, position: Int = 0,
            list: List<PreAudioEntity>,
            onPlayerListener: OnTListener<AudioPlayerHelper.State>? = null
    )

    /**预览文档
     * @param data 文档
     * */
    fun previewDocument(context: Context, data: PreDocumentEntity)
}