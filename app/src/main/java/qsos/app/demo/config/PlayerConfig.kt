package qsos.app.demo.config

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import qsos.core.lib.utils.dialog.AbsBottomDialog
import qsos.core.lib.utils.dialog.BottomDialog
import qsos.core.lib.utils.dialog.BottomDialogUtils
import qsos.core.lib.utils.file.FileUtils
import qsos.core.player.R
import qsos.core.player.audio.AudioPlayerHelper
import qsos.core.player.config.DefPlayerConfig
import qsos.core.player.config.IPlayerConfig
import qsos.core.player.data.PreAudioEntity
import qsos.core.player.data.PreDocumentEntity
import qsos.core.player.data.PreImageEntity
import qsos.core.player.data.PreVideoEntity
import qsos.lib.base.callback.OnTListener
import qsos.lib.netservice.file.FileHelper
import qsos.lib.netservice.file.HttpFileEntity
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
        mDefPlayerConfig.previewVideo(context, position, list)
    }

    override fun previewAudio(
            context: Context, position: Int,
            list: List<PreAudioEntity>,
            onPlayerListener: OnTListener<AudioPlayerHelper.State>?
    ): AudioPlayerHelper? {
        return mDefPlayerConfig.previewAudio(context, position, list, onPlayerListener)
    }

    override fun previewDocument(context: Context, data: PreDocumentEntity) {
        val mPath = data.path
        when {
            mPath.startsWith("http") || mPath.startsWith("ftp") -> {
                // 网络文件，先下载再调用本地软件打开
                val mFileHelper = FileHelper()
                BottomDialogUtils.showCustomerView(context, R.layout.file_download_dialog,
                        viewListener = object : BottomDialog.ViewListener {
                            override fun bindView(dialog: AbsBottomDialog) {
                                var mFilePath: String? = null
                                val state = dialog.findViewById<TextView>(R.id.file_state)
                                val progress = dialog.findViewById<ProgressBar>(R.id.file_progress)
                                val action = dialog.findViewById<Button>(R.id.file_action)
                                val handler = Handler(Looper.getMainLooper())
                                action.text = "下载"
                                action.setOnClickListener {
                                    when (action.text) {
                                        "下载" -> {
                                            action.text = "取消"
                                            state.text = "开始下载"
                                            progress.progress = 0
                                            mFileHelper.downloadFile(HttpFileEntity(mPath, null, data.name, 0),
                                                    object : OnTListener<HttpFileEntity> {
                                                        override fun back(t: HttpFileEntity) {
                                                            if (t.progress == 100) mFilePath = t.path
                                                            handler.post {
                                                                progress.progress = t.progress
                                                                state.text = t.loadMsg
                                                                if (t.progress == 100) action.text = "打开"
                                                            }
                                                        }
                                                    })
                                        }
                                        "打开" -> {
                                            try {
                                                FileUtils.openFileByPhone(context as Activity, File(mFilePath))
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                Timber.tag("文件预览").e(e)
                                            }
                                        }
                                        "取消" -> {
                                            dialog.dismiss()
                                        }
                                    }
                                }
                            }
                        },
                        cancel = true,
                        dismissListener = DialogInterface.OnDismissListener {
                            mFileHelper.clear()
                        })
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