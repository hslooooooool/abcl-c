package vip.qsos.exception

import android.net.ParseException
import android.os.Environment
import com.google.gson.JsonParseException
import org.json.JSONException
import qsos.lib.base.base.BaseApplication
import qsos.lib.base.utils.rx.RxBus
import retrofit2.HttpException
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : 华清松
 * 全局异常处理帮助类
 */
object GlobalExceptionHelper : Thread.UncaughtExceptionHandler {
    /**年月日*/
    private val mDayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    /**年月日时分秒*/
    private val mTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    /**缓存文件保存路径 data\crash\*/
    private val mExceptionPath: String = "${BaseApplication.appContext.externalCacheDir?.absoluteFile
            ?: Environment.getDataDirectory().path}" + "${File.separator}crash${File.separator}"

    override fun uncaughtException(t: Thread, e: Throwable) {
        caughtException(e)
    }

    /**捕获异常并处理*/
    fun caughtException(e: Throwable, deal: (e: ExceptionEvent) -> Unit? = {
        Timber.tag("网络服务异常").e(e)
    }) {
        val mExceptionEvent: ExceptionEvent = when (e) {
            is GlobalException.ServerException -> {
                Timber.tag("网络服务异常").e(e)
                ExceptionEvent(GlobalException(GlobalExceptionType.ServerException, e), "服务接口访问方式可能错误")
            }
            is ConnectException -> {
                Timber.tag("网络连接异常").e(e)
                ExceptionEvent(GlobalException(GlobalExceptionType.ConnectException, e), "网络连接故障")
            }
            is SocketTimeoutException -> {
                Timber.tag("网络连接超时").e(e)
                ExceptionEvent(GlobalException(GlobalExceptionType.TimeoutException, e), "服务器响应超时")
            }
            is NullPointerException -> {
                Timber.tag("空指针异常").e(e)
                ExceptionEvent(GlobalException(GlobalExceptionType.NullPointerException, e), "空指针异常")
            }
            is JsonParseException, is JSONException, is ParseException -> {
                Timber.tag("Json解析异常").e(e)
                ExceptionEvent(GlobalException(GlobalExceptionType.JsonException, e), "Json解析异常")
            }
            is HttpException -> handleHttpException(e)
            else -> {
                Timber.tag("未知异常").e(e)
                ExceptionEvent(GlobalException(GlobalExceptionType.OtherException, e), "未知异常")
            }
        }
        deal(mExceptionEvent)
        saveCrashFile(mExceptionEvent.name, mExceptionEvent.exception.exception.toString())
        RxBus.send(mExceptionEvent)
    }

    /**错误码详见 @see https://blog.csdn.net/Gjc_csdn/article/details/80449996 */
    private fun handleHttpException(e: HttpException): ExceptionEvent {
        Timber.tag("网络服务异常").w(e.message())
        return when (e.code()) {
            400 -> {
                Timber.tag("网络服务异常").w("服务接口访问错误")
                ExceptionEvent(GlobalException(GlobalExceptionType.ServerException, e), "服务接口访问错误")
            }
            401 -> {
                Timber.tag("网络服务异常").w("未授权访问")
                ExceptionEvent(GlobalException(GlobalExceptionType.HttpException, e), "未授权访问")
            }
            403 -> {
                Timber.tag("网络服务异常").w("服务请求被拒绝")
                ExceptionEvent(GlobalException(GlobalExceptionType.ServerException, e), "服务请求被拒绝")
            }
            404 -> {
                Timber.tag("网络服务异常").w("服务接口不存在")
                ExceptionEvent(GlobalException(GlobalExceptionType.ServerException, e), "服务接口不存在")
            }
            405 -> {
                Timber.tag("网络服务异常").w("服务接口已被禁用")
                ExceptionEvent(GlobalException(GlobalExceptionType.ServerException, e), "服务接口已被禁用")
            }
            500 -> {
                Timber.tag("网络服务异常").w("服务器出现问题")
                ExceptionEvent(GlobalException(GlobalExceptionType.ServerException, e), "服务器出现问题")
            }
            501 -> {
                Timber.tag("网络服务异常").w("服务接口访问方式可能错误")
                ExceptionEvent(GlobalException(GlobalExceptionType.ServerException, e), "服务接口访问方式可能错误")
            }
            503 -> {
                Timber.tag("网络服务异常").w("服务暂时无法访问")
                ExceptionEvent(GlobalException(GlobalExceptionType.ServerException, e), "服务暂时无法访问")
            }
            504 -> {
                Timber.tag("网络服务异常").w("服务响应超时")
                ExceptionEvent(GlobalException(GlobalExceptionType.ServerException, e), "服务响应超时")
            }
            else -> {
                Timber.tag("网络服务异常").w("服务未知异常")
                ExceptionEvent(GlobalException(GlobalExceptionType.ServerException, e), "服务未知异常")
            }
        }
    }

    /**
     * 保存错误信息到文件中
     */
    @Throws(Exception::class)
    private fun saveCrashFile(type: String, value: String) {
        Timber.tag("存储异常日志").i("异常写入中...")
        val sb = StringBuffer()
        try {
            val date = mTimeFormat.format(Date())
            sb.append("\r\n[$date]\n")
            sb.append("$type\n$value\n")
        } catch (e: Exception) {
            sb.append("异常写入错误...\r\n")
        }
        writeFile(sb.toString())
    }

    @Throws(Exception::class)
    private fun writeFile(sb: String) {
        val time = mDayFormat.format(Date())
        val fileName = "crash-$time.txt"
        val dir = File(mExceptionPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val fos = FileOutputStream(mExceptionPath + fileName, true)
        val osw = OutputStreamWriter(fos, "utf-8")
        osw.write(sb)
        osw.flush()
        osw.close()
    }

    /**
     * @author : 华清松
     * 全局异常数据实体，使用RxBus监听，建议在Application或MainActivity中监听即可
     */
    data class ExceptionEvent(
            val exception: GlobalException,
            val name: String
    ) : RxBus.RxBusEvent<GlobalException> {

        override fun message(): GlobalException? {
            return exception
        }

        override fun name(): String {
            return name
        }

    }
}