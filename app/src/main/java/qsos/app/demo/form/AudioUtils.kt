package qsos.app.demo.form

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import qsos.app.demo.R
import qsos.core.file.audio.AudioFormat
import qsos.core.file.audio.AudioRecordConfig
import qsos.core.file.audio.AudioRecordController
import qsos.core.file.audio.AudioRecordState
import qsos.core.lib.utils.dialog.AbsBottomDialog

/**
 * @author : 华清松
 * 录音工具
 */
@SuppressLint("SetTextI18n")
object AudioUtils {
    private var fileObservable: PublishSubject<String>? = null
    /**录音按键上下移动距离*/
    private var moveY = 0F
    private lateinit var mAudioRecordController: AudioRecordController

    /**开启录音
     * @param dialog 录音弹窗
     * @param config 录音配置，限制最小最大录制时长，设置录音格式，支持 AMR 与 WAV 格式
     * */
    fun record(
            dialog: AbsBottomDialog,
            config: AudioRecordConfig = AudioRecordConfig.Builder()
                    .setLimitMinTime(1)
                    .setLimitMaxTime(30)
                    .setAudioFormat(AudioFormat.AMR)
                    .build()
    ): Observable<String> {
        fileObservable = PublishSubject.create()
        mAudioRecordController = AudioRecordController(config)
        val stateView = dialog.findViewById<TextView>(R.id.audio_state)
        dialog.findViewById<ImageView>(R.id.audio_action).setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                /**按下手指*/
                MotionEvent.ACTION_DOWN -> {
                    moveY = motionEvent.y
                    /**开始录音*/
                    mAudioRecordController.start()
                }
                /**抬起手指*/
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    mAudioRecordController.finish()
                }
                /**移动手指*/
                MotionEvent.ACTION_MOVE -> {
                    if ((moveY - motionEvent.y) > 200) {
                        mAudioRecordController.cancelWant()
                    } else {
                        mAudioRecordController.cancelRefuse()
                    }
                }
            }
            true
        }
        mAudioRecordController.mAudioPublisher.subscribe {
            when (it.recordState) {
                AudioRecordState.PREPARE -> {
                    stateView.text = it.recordState.value + "\t\t时长:\t\t${it.recordTime} 秒"
                }
                AudioRecordState.RECORDING -> {
                    stateView.text = it.recordState.value + "\t\t时长:\t\t${it.recordTime} 秒"
                }
                AudioRecordState.CANCEL_WANT -> {
                    stateView.text = it.recordState.value + "\t\t时长:\t\t${it.recordTime} 秒"
                }
                AudioRecordState.CANCEL_REFUSE -> {
                    stateView.text = it.recordState.value + "\t\t时长:\t\t${it.recordTime} 秒"
                }
                AudioRecordState.CANCEL -> {
                    stateView.text = it.recordState.value + "\t\t时长:\t\t${it.recordTime} 秒"
                    fileObservable?.onComplete()
                    if (dialog.isVisible) dialog.dismiss()
                }
                AudioRecordState.ERROR -> {
                    stateView.text = it.recordState.value + "\t\t时长:\t\t${it.recordTime} 秒"
                    fileObservable?.onError(Throwable("录音错误"))
                    fileObservable?.onComplete()
                    if (dialog.isVisible) dialog.dismiss()
                }
                AudioRecordState.FINISH -> {
                    stateView.text = it.recordState.value + "\t\t时长:${it.recordTime} 秒"
                    fileObservable?.onNext(it.audioPath)
                    fileObservable?.onComplete()
                    if (dialog.isVisible) dialog.dismiss()
                }
            }
        }.takeUnless {
            !dialog.isVisible
        }
        return fileObservable!!
    }

}