package qsos.app.demo.config

import android.app.Activity
import android.content.Context
import qsos.core.lib.utils.file.FileUtils
import qsos.core.player.config.DefPlayerConfig
import qsos.core.player.config.IPlayerConfig
import qsos.core.player.data.PreAudioEntity
import qsos.core.player.data.PreDocumentEntity
import qsos.core.player.data.PreImageEntity
import qsos.core.player.data.PreVideoEntity
import timber.log.Timber
import java.io.File

/**
 * @author : 华清松
 * 文件预览具体实现
 */
class PlayerConfig : IPlayerConfig {
    private val mDefPlayerConfig: DefPlayerConfig = DefPlayerConfig()
    override fun previewImage(context: Context, position: Int, list: List<PreImageEntity>) {
        mDefPlayerConfig.previewImage(context, position, list)
    }

    override fun previewVideo(context: Context, position: Int, list: List<PreVideoEntity>) {
        // 自行实现视频播放，如使用 节操播放器 等，这里采用本地软件打开
        try {
            FileUtils.openFileByPhone(context as Activity, File(list[position].path))
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.tag("文件预览").e(e)
        }
    }

    override fun previewAudio(context: Context, position: Int, list: List<PreAudioEntity>) {
        mDefPlayerConfig.previewAudio(context, position, list)
    }

    override fun previewDocument(context: Context, data: PreDocumentEntity) {
        // 测试数据 mTestUrl = "http://resource.qsos.vip/test.mp4"
        val mTestUrl = data.path
        when {
            mTestUrl.startsWith("http") || mTestUrl.startsWith("ftp") -> {
                // 网络文件，先下载再调用本地软件打开

            }
            else -> {
                // 本地文件，调用本地软件打开
                try {
                    FileUtils.openFileByPhone(context as Activity, File(data.path))
                } catch (e: Exception) {
                    e.printStackTrace()
                    Timber.tag("文件预览").e(e)
                }
            }
        }
    }
}