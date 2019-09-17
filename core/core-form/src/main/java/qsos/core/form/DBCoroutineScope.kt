package qsos.core.form

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * @author : 华清松
 * Kotlin协程配置数据库请求处理逻辑
 */
open class DBCoroutineScope {

    class Dsl<ResultType> {
        var db: (() -> ResultType?)? = null
        var onSuccess: ((ResultType?) -> Any?)? = null
        var onFail: ((Exception) -> Any?)? = {
            Timber.tag("数据库操作日志").e(it)
        }
    }
}

/**
 * @author : 华清松
 * 常用的数据库协程请求
 */
fun <ResultType> CoroutineScope.db(
        dsl: DBCoroutineScope.Dsl<ResultType>.() -> Unit
) {
    val dbCoroutine = DBCoroutineScope.Dsl<ResultType>()
    dbCoroutine.dsl()
    this.launch(Dispatchers.Main) {
        val work = async(Dispatchers.IO) {
            try {
                dbCoroutine.db?.invoke()
            } catch (e: Exception) {
                dbCoroutine.onFail?.invoke(e)
                null
            }
        }
        work.await()?.let {
            dbCoroutine.onSuccess?.invoke(it)
        }
    }
}
