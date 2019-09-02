package qsos.core.lib.utils.json

import android.annotation.SuppressLint
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : 华清松
 * JSON中时间格式转换，消除后台返回时间格式不定引发的转换问题
 */
@SuppressLint("SimpleDateFormat")
class DateDeserializer : JsonDeserializer<Date> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date? {
        return try {
            // 年月日 时分秒格式
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(json.asJsonPrimitive.asString, ParsePosition(0))
        } catch (e: Exception) {
            // 毫秒数格式
            Date(json.asJsonPrimitive.asLong)
        }
    }
}