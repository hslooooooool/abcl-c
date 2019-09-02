package qsos.core.lib.utils.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.text.DateFormat
import java.util.*

object GsonUtils {

    fun <T> initOfDateToLong(string: String, cls: Class<T>): T? {
        val gson: Gson
        val mGsonBuilder = GsonBuilder()
        mGsonBuilder.registerTypeAdapter(Date::class.java, DateDeserializer()).setDateFormat(DateFormat.LONG).create()
        mGsonBuilder.registerTypeAdapter(Date::class.java, DateSerializer()).setDateFormat(DateFormat.LONG).create()
        gson = mGsonBuilder.create()
        return try {
            gson.fromJson(string, cls)
        } catch (e: Exception) {
            null
        }
    }

    fun <T> initOfDateToString(string: String, cls: Class<T>): T? {
        val gson: Gson
        val mGsonBuilder = GsonBuilder()
        mGsonBuilder.registerTypeAdapter(Date::class.java, DateDeserializer()).setDateFormat("yyyy-MM-dd HH:mm:ss").create()
        mGsonBuilder.registerTypeAdapter(Date::class.java, DateSerializer()).setDateFormat("yyyy-MM-dd HH:mm:ss").create()
        gson = mGsonBuilder.create()
        return try {
            gson.fromJson(string, cls)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
