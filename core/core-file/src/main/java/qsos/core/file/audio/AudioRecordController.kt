package qsos.core.file.audio

import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor
import qsos.lib.base.callback.OnTListener
import java.util.*

/**
 * @author : 华清松
 * 录音控制
 * 所需权限
 * Manifest.permission.WRITE_EXTERNAL_STORAGE,
 * Manifest.permission.READ_EXTERNAL_STORAGE,
 * Manifest.permission.RECORD_AUDIO,
 * Manifest.permission.READ_PHONE_STATE
 *
 * @param config 录音配置
 */
class AudioRecordController(private val config: AudioRecordConfig) : IControlView {

    val mAudioPublisher: FlowableProcessor<AudioRecordData> = PublishProcessor.create<AudioRecordData>().toSerialized()
    private var mRecordTimer: Timer? = null
    private var mRecordTimerTask: TimerTask? = null

    /**录制数据*/
    var mRecordData: AudioRecordData = AudioRecordData()

    override fun start() {
        release()

        mRecordData.recordTime = 0
        mRecordData.audioPath = AudioRecordUtils.initAudioPath(config.audioType)

        AudioRecordUtils.startRecord(mRecordData.audioPath, object : OnTListener<Boolean> {
            override fun back(t: Boolean) {
                if (t) {
                    mRecordData.recordState = AudioRecordState.RECORDING
                    mRecordTimer = Timer()
                    mRecordTimerTask = object : TimerTask() {
                        override fun run() {
                            mRecordData.recordTime++
                            if (config.limitMaxTime < mRecordData.recordTime) {
                                /**限定最长录制时间，高于此值自动完成录音*/
                                finish()
                            }
                            mAudioPublisher.onNext(mRecordData)
                        }
                    }
                    mRecordTimer!!.schedule(mRecordTimerTask!!, 1000, 1000)
                } else {
                    mRecordData.recordState = AudioRecordState.ERROR
                    mAudioPublisher.onNext(mRecordData)
                    mAudioPublisher.onComplete()
                }
            }
        })
    }

    override fun cancelWant() {
        mRecordData.recordState = AudioRecordState.CANCEL_WANT
        mAudioPublisher.onNext(mRecordData)
    }

    override fun cancelRefuse() {
        mRecordData.recordState = AudioRecordState.CANCEL_REFUSE
        mAudioPublisher.onNext(mRecordData)
    }

    override fun cancel() {
        mRecordData.recordState = AudioRecordState.CANCEL
        AudioRecordUtils.clearAll()
        mAudioPublisher.onNext(mRecordData)
        mAudioPublisher.onComplete()
    }

    override fun finish() {
        AudioRecordUtils.release()
        AudioRecordUtils.clearTemRaw()
        release()
        when (mRecordData.recordState) {
            AudioRecordState.RECORDING, AudioRecordState.CANCEL_REFUSE, AudioRecordState.FINISH -> {
                if (config.limitMinTime > mRecordData.recordTime) {
                    /**限定最短录制时间，低于此值表示取消录制*/
                    cancel()
                } else {
                    mRecordData.recordState = AudioRecordState.FINISH
                    mAudioPublisher.onNext(mRecordData)
                    mAudioPublisher.onComplete()
                }
            }
            AudioRecordState.PREPARE, AudioRecordState.CANCEL_WANT, AudioRecordState.CANCEL, AudioRecordState.ERROR -> {
                cancel()
            }
        }
    }

    private fun release() {
        mRecordTimerTask?.cancel()
        mRecordTimer?.cancel()
        mRecordTimerTask = null
        mRecordTimer = null
    }
}