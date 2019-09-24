package qsos.core.exception

import android.net.ParseException
import android.os.Environment
import android.util.Log
import com.google.gson.JsonParseException
import org.json.JSONException
import qsos.lib.base.base.BaseApplication
import qsos.lib.base.utils.rx.RxBus
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

    /**捕获异常并处理，可通过以下方式接受异常，便于全局自定义处理机制
     * RxBus.toFlow(GlobalExceptionHelper.ExceptionEvent::class.java).subscribe{}
     *
     * @see RxBus.toFlow
     * @param e 抛出的异常
     * @param deal 自行传入后续处理，可空
     * */
    fun caughtException(e: Throwable, deal: (e: ExceptionEvent) -> Unit? = {}) {
        Timber.e(e)
        val mExceptionEvent: ExceptionEvent = when (e) {
            is ConnectException -> {
                ExceptionEvent(GlobalException(404, e))
            }
            is SocketTimeoutException -> {
                ExceptionEvent(GlobalException(504, e))
            }
            is NullPointerException -> {
                ExceptionEvent(GlobalException(0, e))
            }
            is JsonParseException, is JSONException, is ParseException -> {
                ExceptionEvent(GlobalException(-2, e))
            }
            else -> {
                ExceptionEvent(GlobalException(e))
            }
        }
        // 自行处理
        deal(mExceptionEvent)
        // 传递出去，统一处理
        RxBus.send(mExceptionEvent)
    }

    /**Timber日志输出*/
    open class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            super.log(priority, tag, message, t)
            if (priority > Log.INFO) saveCrashFile(tag ?: "异常捕获", "$message ${t.toString()}")
        }
    }

    /**
     * 保存错误信息到文件中
     */
    private fun saveCrashFile(type: String, value: String) {
        val sb = StringBuffer()
        val date = mTimeFormat.format(Date())
        sb.append("\r\n[$date]\n$type\n")
        sb.append("$value\n\n----------------------------华丽分割线----------------------------\n\n")
        writeFile(sb.toString())
    }

    private fun writeFile(sb: String) {
        try {
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
            Timber.tag("存储异常日志").i("以上异常已记录到: ${mExceptionPath + fileName}")
        } catch (e: Exception) {
            Timber.tag("存储异常日志").i("以上异常记录失败")
        }
    }

    /**
     * @author : 华清松
     * 全局异常数据实体，使用RxBus监听，建议在Application或MainActivity中监听即可
     */
    data class ExceptionEvent(
            val exception: GlobalException
    ) : RxBus.RxBusEvent<GlobalException> {

        override fun message(): GlobalException? {
            return exception
        }

        override fun name(): String {
            return "异常码:${exception.code}"
        }

        override fun toString(): String {
            var ss = super.toString() + "\n"
            ss += " code=${exception.code}"
            ss += " msg=${exception.msg}"
            ss += " error=${exception.error?.toString()}"
            return ss
        }
    }
}