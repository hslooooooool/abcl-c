package qsos.core.file.audio

/**
 * @author : 华清松
 * @description : 录音数据，录制中实时更新
 */
data class AudioRecordData(
        /**已录制时长，秒*/
        var recordTime: Int = -1,
        /**录制状态*/
        var recordState: AudioRecordState = AudioRecordState.PREPARE,
        /**录音存储位置*/
        var audioPath: String = ""
)

/**
 * @author : 华清松
 * 录音状态
 */
enum class AudioRecordState(val value: String) {
    PREPARE("准备中..."),
    RECORDING("正在录制，请说话"),

    CANCEL_WANT("意向取消"),
    CANCEL_REFUSE("取消意向，正在录制"),

    CANCEL("录音取消"),
    FINISH("完成录音"),

    ERROR("录音失败"),
}

/**
 * @author : 华清松
 * 录音初始化配置
 */
data class AudioRecordConfig(
        var limitMinTime: Int = 1,
        var limitMaxTime: Int = 60,
        var audioFormat: AudioFormat = AudioFormat.AMR
) {
    open class Builder {
        private var limitMinTime: Int = 1
        private var limitMaxTime: Int = 60
        private var audioFormat: AudioFormat = AudioFormat.AMR

        open fun setLimitMinTime(minTime: Int): Builder {
            this.limitMinTime = minTime
            return this
        }

        open fun setLimitMaxTime(maxTime: Int): Builder {
            this.limitMaxTime = maxTime
            return this
        }

        open fun setAudioFormat(format: AudioFormat): Builder {
            this.audioFormat = format
            return this
        }

        open fun build(): AudioRecordConfig {
            return AudioRecordConfig(limitMinTime, limitMaxTime, audioFormat)
        }
    }
}

/**
 * @author : 华清松
 * 录制格式，支持 amr wav 格式，默认 amr 格式
 */
enum class AudioFormat {
    AMR, WAV
}

/**
 * @author : 华清松
 * 录音控制接口
 */
interface IControlView {
    /**开始录音*/
    fun start()

    /**意向取消*/
    fun cancelWant()

    /**放弃意向*/
    fun cancelRefuse()

    /**取消录音*/
    fun cancel()

    /**完成录音*/
    fun finish()
}