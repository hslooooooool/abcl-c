package qsos.lib.netservice.expand

import qsos.lib.netservice.data.BaseResponse

/**
 * @author : 华清松
 * Kotlin协程配置Retrofit请求处理逻辑，提供两种实现
 *
 * 返回数据统一为 BaseResponse ，采用 DefaultRetrofitCoroutineScope
 *
 * 返回值为 ResultType 本身，采用 RetrofitCoroutineScope
 *
 * 自定义处理逻辑？参考 RetrofitCoroutineScope 或 DefaultRetrofitCoroutineScope
 *
 * @see BaseResponse
 * @see DefaultRetrofitCoroutineScope
 * @see RetrofitCoroutineScope
 */
abstract class AbsRetrofitCoroutineScope {

    open class Dsl<ResultType> {

        /**请求开始*/
        var onStart: (() -> Unit?)? = null
            private set
        /**请求成功*/
        var onSuccess: ((ResultType?) -> Unit)? = null
            private set
        /**请求完成*/
        var onComplete: (() -> Unit?)? = null
            private set
        /**请求失败*/
        var onFailed: ((code: Int, msg: String?, error: Throwable?) -> Unit?)? = null
            private set

        open fun clean() {
            onStart = null
            onSuccess = null
            onComplete = null
            onFailed = null
        }

        open fun onStart(block: () -> Unit) {
            this.onStart = block
        }

        open fun onSuccess(block: (ResultType?) -> Unit) {
            this.onSuccess = block
        }

        open fun onComplete(block: () -> Unit) {
            this.onComplete = block
        }

        open fun onFailed(block: (code: Int, msg: String?, error: Throwable?) -> Unit) {
            this.onFailed = block
        }

    }

    open class DslWithLiveData<ResultType> {
        open fun clean() {}
    }

    open class DslWithSuccess<ResultType> {

        /**请求成功*/
        var onSuccess: ((ResultType?) -> Unit)? = null
            private set

        open fun clean() {
            onSuccess = null
        }

        open fun onSuccess(block: (ResultType?) -> Unit) {
            this.onSuccess = block
        }
    }
}
