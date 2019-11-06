package qsos.core.player

import android.content.Context
import qsos.core.player.audio.AudioPlayerHelper
import qsos.core.player.config.DefPlayerConfig
import qsos.core.player.config.IPlayerConfig
import qsos.core.player.data.PreAudioEntity
import qsos.core.player.data.PreDocumentEntity
import qsos.core.player.data.PreImageEntity
import qsos.core.player.data.PreVideoEntity
import qsos.lib.base.callback.OnTListener

/**
 * @author : 华清松
 * 媒体预览操作帮助类
 */
object PlayerConfigHelper : IPlayerConfig {
    override fun previewImage(context: Context, position: Int, list: List<PreImageEntity>) {
        mPlayerConfig?.previewImage(context, position, list)
    }

    override fun previewVideo(context: Context, position: Int, list: List<PreVideoEntity>) {
        mPlayerConfig?.previewVideo(context, position, list)
    }

    override fun previewAudio(
            context: Context, position: Int,
            list: List<PreAudioEntity>,
            onPlayerListener: OnTListener<AudioPlayerHelper.State>?
    ) {
        mPlayerConfig?.previewAudio(context, position, list, onPlayerListener)
    }

    override fun previewDocument(context: Context, data: PreDocumentEntity) {
        mPlayerConfig?.previewDocument(context, data)
    }

    private var mPlayerConfig: IPlayerConfig? = null
        get() = if (field == null) DefPlayerConfig() else field

    /**初始化自定义媒体文件预览配置，否者使用默认配置
     * @see DefPlayerConfig
     * */
    fun init(playerConfig: IPlayerConfig) {
        this.mPlayerConfig = playerConfig
    }

}