package qsos.core.form.utils

import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : 华清松
 * 表单工具类
 */
object DateUtils {

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

}