package qsos.core.lib.utils.data

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import qsos.lib.base.base.BaseApplication

/**
 * @author : 华清松
 * SharedPreferences 工具类
 */
object SharedPreUtils {

    private const val SHARED_PRE = "QSOS_SHARED_PRE"

    private val mShared: SharedPreferences = BaseApplication.appContext.getSharedPreferences(SHARED_PRE, Context.MODE_PRIVATE)

    val mEdit = mShared.edit()

    /**保存数据
     * @param value 支持String、Boolean、Long、Int、Float、Set<String>，实体类将转为json保存
     * */
    fun <T> save(key: String, value: T) {
        when (value) {
            is String -> {
                mEdit.putString(key, value).apply()
            }
            is Boolean -> {
                mEdit.putBoolean(key, value).apply()
            }
            is Long -> {
                mEdit.putLong(key, value).apply()
            }
            is Int -> {
                mEdit.putInt(key, value).apply()
            }
            is Float -> {
                mEdit.putFloat(key, value).apply()
            }
            is Set<*> -> {
                mEdit.putStringSet(key, value as Set<String>).apply()
            }
            else -> {
                mEdit.putString(key, Gson().toJson(value)).apply()
            }
        }
    }

    fun <T> find(key: String, def: T): T? {
        return when (def) {
            is String -> {
                val v = mShared.getString(key, def)
                v
            }
            is Boolean -> {
                val v = mShared.getBoolean(key, def)
                v
            }
            is Long -> {
                val v = mShared.getLong(key, def)
                v
            }
            is Int -> {
                val v = mShared.getInt(key, def)
                v
            }
            is Float -> {
                val v = mShared.getFloat(key, def)
                v
            }
            is Set<*> -> {
                val v = mShared.getStringSet(key, def as Set<String>)
                v
            }
            else -> {
                val type = object : TypeToken<T>() {}.type
                val json = mShared.getString(key, null)
                val v = if (TextUtils.isEmpty(json)) {
                    def
                } else {
                    try {
                        Gson().fromJson<T>(mShared.getString(key, null), type)
                    } catch (e: Exception) {
                        def
                    }
                }
                v
            }
        } as T?
    }

    fun remove(context: Context?, key: String) {
        mEdit.remove(key).apply()
    }

    fun clear(context: Context?) {
        mEdit.clear().apply()
    }
}