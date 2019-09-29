package qsos.core.player

import qsos.core.player.config.IPlayerConfig

/**
 * @author : 华清松
 * 媒体预览操作帮助类
 */
object PlayerConfigHelper : IPlayerConfig {

    override fun previewImage() {
        mPlayerConfig?.previewImage()
    }

    private var mPlayerConfig: IPlayerConfig? = null

    fun init(playerConfig: IPlayerConfig) {
        this.mPlayerConfig = playerConfig
    }
}