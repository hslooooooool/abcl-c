package qsos.core.form.utils

import android.nfc.FormatException
import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : 华清松
 * 表单工具类
 */
object FormUtils {

    /**
     * 根据 typeEnum 时间转换类型，转换具体毫秒数 time 为具体时间，错误时默认为 yyyy-mm-dd 年月日格式
     */
    fun date(time: Long?, type: String?): String {
        val format = try {
            if (TextUtils.isEmpty(type)) {
                SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            } else {
                SimpleDateFormat(type, Locale.CHINA)
            }
        } catch (e: Exception) {
            SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        }
        return if (time == null) "无" else format.format(Date(time))
    }

    /**
     * 根据 typeEnum 时间转换类型，转换具体毫秒数 time 为具体时间，错误时默认为 yyyymmdd 年月日格式
     */
    fun date(date: Date?, type: String?): String {
        val format = try {
            SimpleDateFormat(type, Locale.CHINA)
        } catch (e: FormatException) {
            SimpleDateFormat("yyyy-mm-dd", Locale.CHINA)
        }
        return if (date == null) "无" else format.format(date)
    }

}