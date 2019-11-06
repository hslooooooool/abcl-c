package qsos.core.player.config

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.widget.Button
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import qsos.core.lib.utils.dialog.AbsBottomDialog
import qsos.core.lib.utils.dialog.BottomDialog
import qsos.core.lib.utils.dialog.BottomDialogUtils
import qsos.core.lib.utils.file.FileUtils
import qsos.core.player.PlayerPath
import qsos.core.player.R
import qsos.core.player.audio.AudioPlayerHelper
import qsos.core.player.data.*
import qsos.lib.base.callback.OnTListener
import timber.log.Timber
import java.io.File

/**
 * @author : 华清松
 * 媒体预览默认配置
 */
class DefPlayerConfig : IPlayerConfig {
    override fun previewImage(context: Context, position: Int, list: List<PreImageEntity>) {
        val data = Gson().toJson(PreFileEntity(position, list))
        ARouter.getInstance().build(PlayerPath.GALLERY)
                .withString(PlayerPath.GALLERY_DATA, data)
                .withTransition(R.anim.activity_in_center, R.anim.activity_out_center)
                .navigation(context)
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

    override fun previewAudio(
            context: Context, position: Int,
            list: List<PreAudioEntity>,
            onPlayerListener: OnTListener<AudioPlayerHelper.State>?
    ) {
        val path = list[position].path
        val playType: AudioPlayerHelper.PlayType = when {
            path.startsWith("https") || path.startsWith("ftp") -> AudioPlayerHelper.PlayType.URL
            else -> AudioPlayerHelper.PlayType.PATH
        }
        var mAudioPlayerHelper: AudioPlayerHelper? = null
        BottomDialogUtils.showCustomerView(context, R.layout.audio_play_dialog,
                object : BottomDialog.ViewListener {
                    override fun bindView(dialog: AbsBottomDialog) {
                        val mState = dialog.findViewById<TextView>(R.id.audio_state)
                        val mAction = dialog.findViewById<Button>(R.id.audio_action)
                        mState.text = "开始播放"
                        mAudioPlayerHelper = AudioPlayerHelper().init(
                                AudioPlayerHelper.PlayBuild(context, playType, list[position].path,
                                        object : AudioPlayerHelper.PlayerListener {
                                            override fun onState(state: AudioPlayerHelper.State) {
                                                onPlayerListener?.back(state)
                                                if (state == AudioPlayerHelper.State.STOP) {
                                                    mState.text = "完成播放"
                                                    mAction.isEnabled = true
                                                }
                                            }
                                        }
                                )
                        )
                        mAction.setOnClickListener {
                            mAction.isEnabled = false
                            mAudioPlayerHelper?.play()
                        }
                    }
                }, true,
                DialogInterface.OnDismissListener {
                    mAudioPlayerHelper?.stop()
                }
        )
    }

    override fun previewDocument(context: Context, data: PreDocumentEntity) {
        // 文件预览，本地打开，无默认实现
    }

}