package qsos.lib.netservice.file

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException
import java.math.BigDecimal

/**
 * @author : 华清松
 * 上传请求 Body
 */
class UploadBody(
        /**实际请求体*/
        private val requestBody: RequestBody,
        /**上传进度监听*/
        private val listener: ProgressListener?
) : RequestBody() {

    private lateinit var bufferedSink: BufferedSink

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return requestBody.contentLength()
    }

    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    /**
     * 重写进行写入
     * @param sink BufferedSink
     * @throws IOException 异常
     */
    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val sk = sink(sink)
        bufferedSink = sk.buffer()
        // 将请求体上传的文件数据写入 sink 流对象，向服务器写数据
        requestBody.writeTo(bufferedSink)
        // NOTICE 必须调用flush
        bufferedSink.flush()
    }

    /**
     * 写入，回调进度接口
     * @param sink Sink
     * @return Sink
     */
    private fun sink(sink: Sink): Sink {
        // 当前写入字节数
        var bytesWritten = 0L
        // 总字节长度，避免多次调用 contentLength() 方法
        val contentLength = contentLength()
        return object : ForwardingSink(sink) {
            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                // 增加当前写入的字节数
                bytesWritten += byteCount
                val mProgress = if (bytesWritten == contentLength) {
                    100
                } else {
                    val temLength = BigDecimal(bytesWritten * 100 / contentLength)
                            .setScale(2, BigDecimal.ROUND_HALF_UP).toInt()

                    if (temLength >= 100) 99 else temLength
                }
                // 进度回调
                listener?.progress(mProgress, contentLength, mProgress == 100)
            }
        }
    }
}