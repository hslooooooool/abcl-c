package qsos.core.player.config

import android.content.Context
import android.content.DialogInterface
import android.widget.Button
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import qsos.core.lib.utils.dialog.AbsBottomDialog
import qsos.core.lib.utils.dialog.BottomDialog
import qsos.core.lib.utils.dialog.BottomDialogUtils
import qsos.core.player.PlayerPath
import qsos.core.player.R
import qsos.core.player.audio.AudioPlayerHelper
import qsos.core.player.data.*

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
        // 视频预览，无默认实现
    }

    override fun previewAudio(context: Context, position: Int, list: List<PreAudioEntity>) {
        val path = list[position].path
        val playType: AudioPlayerHelper.PlayType = when {
            path.startsWith("https") || path.startsWith("ftp") -> AudioPlayerHelper.PlayType.URL
            else -> AudioPlayerHelper.PlayType.PATH
        }
        var mAudioPlayerHelper: AudioPlayerHelper? = null
        BottomDialogUtils.showCustomerView(context, R.layout.audio_play_dialog,
                object : BottomDialog.ViewListener {
                    override fun bindView(dialog: AbsBottomDialog) {
                        val state = dialog.findViewById<TextView>(R.id.audio_state)
                        val action = dialog.findViewById<Button>(R.id.audio_action)
                        state.text = "开始播放"
                        mAudioPlayerHelper = AudioPlayerHelper().init(
                                AudioPlayerHelper.PlayBuild(context, playType, list[position].path,
                                        object : AudioPlayerHelper.PlayerListener {
                                            override fun onPlayerStop() {
                                                state.text = "完成播放"
                                                action.isEnabled = true
                                            }
                                        }
                                )
                        )
                        action.setOnClickListener {
                            action.isEnabled = false
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