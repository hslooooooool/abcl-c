package qsos.app.demo.player

import qsos.core.player.config.IPlayerConfig
import timber.log.Timber

/**
 * @author : 华清松
 * 媒体预览配置
 */
class PlayerConfig : IPlayerConfig {
    override fun previewImage() {
        Timber.tag("媒体预览代理").i("图片")
    }
}