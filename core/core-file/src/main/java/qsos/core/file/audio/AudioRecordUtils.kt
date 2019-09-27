package qsos.core.file.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import qsos.core.lib.utils.file.FileUtils
import qsos.lib.base.callback.OnTListener
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * @author : 华清松
 * 录音实现
 */
object AudioRecordUtils {
    /**缓冲区字节大小*/
    private var bufferSizeInBytes = 0
    private var mAudioRecord: AudioRecord? = null
    private var mRecordListener: OnTListener<Boolean>? = null
    private var mRecordThread: Thread? = null
    private var mRecording: Boolean = true

    private const val STATE_ERROR = false
    private const val STATE_START = true

    /**采用频率，44100是标准*/
    private const val AUDIO_SAMPLE_RATE = 44100
    private var mAudioBasePath: String = "error"
    private var mRawPath: String = ""
    /**获取编码后的WAV格式音频文件路径*/
    var mWavPath: String = ""
    /**获取编码后的AMR格式音频文件路径*/
    var mAmrPath: String = ""
    /**录音文件路径*/
    private var mAudioPath: String = ""

    /**初始化文件路径*/
    fun initAudioPath(type: AudioType): String {
        mAudioBasePath = "${FileUtils.MEDIA_PATH}/AUDIO_${System.currentTimeMillis()}"
        mRawPath = "$mAudioBasePath.raw"
        mWavPath = "$mAudioBasePath.wav"
        mAmrPath = "$mAudioBasePath.amr"
        mAudioPath = when (type) {
            AudioType.WAV -> mWavPath
            AudioType.AMR -> mAmrPath
        }
        return mAudioPath
    }

    /**开始录音*/
    fun startRecord(audioPath: String, recordListener: OnTListener<Boolean>) {
        this.mAudioPath = audioPath
        this.mRecordListener = recordListener
        mAudioRecord ?: createAudioRecord()
        mAudioRecord?.startRecording()
        mRecordThread = Thread(AudioRecordThread())
        mRecording = true
        mRecordThread?.start()
        mRecordListener?.back(STATE_START)
    }

    fun release() {
        try {
            mRecording = false
            mAudioRecord?.stop()
        } finally {
            mAudioRecord?.release()
            mRecordThread = null
            mAudioRecord = null
            mRecordListener = null
        }
    }

    private fun createAudioRecord(): AudioRecord {
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT)
        mAudioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes)
        return mAudioRecord!!
    }

    /**录音文件读写线程*/
    class AudioRecordThread : Runnable {
        override fun run() {
            try {
                writeDateTOFile()
                copyWaveFile()
            } catch (e: Exception) {
                e.printStackTrace()
                mRecording = false
                mRecordListener?.back(STATE_ERROR)
            }
        }
    }

    private fun writeDateTOFile() {
        if (bufferSizeInBytes == 0) bufferSizeInBytes = 64
        val audioData = ByteArray(bufferSizeInBytes)
        var readSize: Int
        val file = File(mRawPath)
        if (file.exists()) {
            file.delete()
        }
        val fos = FileOutputStream(file)
        while (mRecording && mAudioRecord != null) {
            readSize = mAudioRecord!!.read(audioData, 0, bufferSizeInBytes)
            if (AudioRecord.ERROR_INVALID_OPERATION != readSize) {
                fos.write(audioData)
            }
        }
        fos.close()
    }

    /**将原始录音文件转化格式*/
    @Throws(Exception::class)
    private fun copyWaveFile() {
        val inputStream = FileInputStream(mRawPath)
        val outputStream = FileOutputStream(mAudioPath)
        val totalAudioLen: Long
        val totalDataLen: Long
        val data = ByteArray(bufferSizeInBytes)
        totalAudioLen = inputStream.channel.size()
        totalDataLen = totalAudioLen + 36
        writeWaveFileHeader(outputStream, totalAudioLen, totalDataLen)
        while (inputStream.read(data) != -1) {
            outputStream.write(data)
        }
        outputStream.flush()
        inputStream.close()
        outputStream.close()
    }

    /**完成录音，清除临时缓存的录音源文件*/
    fun clearTemRaw() {
        val file = File(mRawPath)
        if (file.exists()) {
            file.delete()
        }
    }

    /**取消录音，清除已保存的录音文件*/
    fun clearAll() {
        val file = File(mRawPath)
        if (file.exists()) {
            file.delete()
        }
        val file1 = File(mAudioPath)
        if (file1.exists()) {
            file1.delete()
        }
    }

    @Throws(Exception::class)
    private fun writeWaveFileHeader(out: FileOutputStream, totalAudioLen: Long, totalDataLen: Long) {
        val longSampleRate = AUDIO_SAMPLE_RATE.toLong()
        val channels = 2
        val byteRate = (16 * AUDIO_SAMPLE_RATE * channels / 8).toLong()
        val header = ByteArray(44)
        header[0] = 'R'.toByte()
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()
        header[12] = 'f'.toByte()
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (longSampleRate and 0xff).toByte()
        header[25] = (longSampleRate shr 8 and 0xff).toByte()
        header[26] = (longSampleRate shr 16 and 0xff).toByte()
        header[27] = (longSampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = (2 * 16 / 8).toByte()
        header[33] = 0
        header[34] = 16
        header[35] = 0
        header[36] = 'd'.toByte()
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = (totalAudioLen shr 8 and 0xff).toByte()
        header[42] = (totalAudioLen shr 16 and 0xff).toByte()
        header[43] = (totalAudioLen shr 24 and 0xff).toByte()
        out.write(header, 0, 44)
    }

}