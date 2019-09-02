package qsos.core.lib.utils.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import qsos.lib.base.base.BaseApplication

/**
 * @author : 华清松
 * SharedPreferences 工具类
 */
object SharedPreUtils {

    private const val SHARED_PRE = "QSOS_SHARED_PRE"

    private val mShared: SharedPreferences = BaseApplication.appContext.getSharedPreferences(SHARED_PRE, Context.MODE_PRIVATE)
    @SuppressWarnings
    val mEdit = mShared.edit()

    /**保存数据
     * @param value 支持String、Boolean、Long、Int、Float、Set<String>，实体类将转为json保存
     * */
    fun <T> save(key: String, value: T) {
        when (value) {
            is String -> {
                mShared.edit().putString(key, value).apply()
            }
            is Boolean -> {
                mShared.edit().putBoolean(key, value).apply()
            }
            is Long -> {
                mShared.edit().putLong(key, value).apply()
            }
            is Int -> {
                mShared.edit().putInt(key, value).apply()
            }
            is Float -> {
                mShared.edit().putFloat(key, value).apply()
            }
            is Set<*> -> {
                mShared.edit().putStringSet(key, value as Set<String>).apply()
            }
            else -> {
                mShared.edit().putString(key, Gson().toJson(value)).apply()
            }
        }
    }

    fun remove(context: Context?, key: String) {
        mShared.edit().remove(key).apply()
    }

    fun clear(context: Context?) {
        mShared.edit().clear().apply()
    }
}