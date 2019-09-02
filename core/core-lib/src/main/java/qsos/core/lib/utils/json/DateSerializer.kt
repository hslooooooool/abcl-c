package qsos.core.lib.utils.json

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer

import java.lang.reflect.Type
import java.util.Date

/**
* @author : 华清松
* JSON中时间格式转换，消除后台返回时间格式不定引发的转换问题
*/
class DateSerializer : JsonSerializer<Date> {

    override fun serialize(src: Date, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.time)
    }

}