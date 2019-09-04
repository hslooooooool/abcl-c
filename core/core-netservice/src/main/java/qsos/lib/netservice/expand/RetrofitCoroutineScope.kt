package qsos.lib.netservice.expand

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import qsos.lib.netservice.data.BaseHttpStatus
import qsos.lib.netservice.data.HttpLiveData
import qsos.lib.netservice.data.HttpStatusEnum
import retrofit2.Call
import java.net.ConnectException

/**
 * @author : 华清松
 * Kotlin协程配置Retrofit请求处理逻辑
 */
class RetrofitCoroutineScope {

    class Dsl<ResultType> : AbsRetrofitCoroutineScope.Dsl<ResultType>() {
        var api: (Call<ResultType>)? = null

        override fun clean() {
            super.clean()
            api = null
        }
    }

    class DslWithLiveData<ResultType> : AbsRetrofitCoroutineScope.DslWithLiveData<ResultType>() {
        var api: (Call<ResultType>)? = null
        var data: (HttpLiveData<ResultType>)? = null
        override fun clean() {
            super.clean()
            api = null
            data = null
        }
    }

    class DslWithSuccess<ResultType> : AbsRetrofitCoroutineScope.DslWithSuccess<ResultType>() {
        var api: (Call<ResultType>)? = null
        var data: (HttpLiveData<ResultType>)? = null
        override fun clean() {
            super.clean()
            api = null
            data = null
        }
    }
}

/**
 * @author : 华清松
 * 常用的Retrofit协程请求，方便简单的接口调用，统一的请求状态管理，自行对请求状态进行处理
 */
fun <ResultType> CoroutineScope.retrofit(
        dsl: RetrofitCoroutineScope.Dsl<ResultType>.() -> Unit
) {
    val retrofitCoroutine = RetrofitCoroutineScope.Dsl<ResultType>()
    retrofitCoroutine.dsl()
    this.launch(Dispatchers.Main) {
        var httpStatus = BaseHttpStatus(200, "请求成功")
        retrofitCoroutine.onStart?.invoke()
        retrofitCoroutine.api?.let { api ->
            // IO线程执行网络请求
            val work = async(Dispatchers.IO) {
                try {
                    api.execute()
                } catch (e: ConnectException) {
                    httpStatus = BaseHttpStatus(404, e.message, e)
                    null
                } catch (e: Exception) {
                    httpStatus = BaseHttpStatus(-1, e.message, e)
                    null
                }
            }
            // 协程关闭时，取消任务
            work.invokeOnCompletion {
                if (work.isCancelled) {
                    api.cancel()
                    retrofitCoroutine.clean()
                }
            }

            val response = work.await()
            if (response == null) {
                retrofitCoroutine.onFailed?.invoke(httpStatus.hashCode(), httpStatus.statusMsg, httpStatus.statusError)
            } else {
                if (response.isSuccessful) {
                    retrofitCoroutine.onSuccess?.invoke(response.body())
                } else {
                    retrofitCoroutine.onFailed?.invoke(response.code(), response.errorBody().toString(), null)
                }
            }

            retrofitCoroutine.onComplete?.invoke()
        }
    }
}

/**
 * @author : 华清松
 * 常用的Retrofit协程请求，方便简单的接口调用，统一的请求状态管理，通过LiveData直接更新UI
 */
fun <ResultType> CoroutineScope.retrofitWithLiveData(
        dsl: RetrofitCoroutineScope.DslWithLiveData<ResultType>.() -> Unit
) {
    val retrofitCoroutine = RetrofitCoroutineScope.DslWithLiveData<ResultType>()
    retrofitCoroutine.dsl()
    this.launch(Dispatchers.Main) {
        retrofitCoroutine.api?.let { api ->
            var httpStatus = BaseHttpStatus(200, "请求成功")
            retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.LOADING))
            val work = async(Dispatchers.IO) {
                try {
                    api.execute()
                } catch (e: ConnectException) {
                    httpStatus = BaseHttpStatus(404, e.message, e)
                    null
                } catch (e: Exception) {
                    httpStatus = BaseHttpStatus(-1, e.message, e)
                    null
                }
            }
            work.invokeOnCompletion {
                if (work.isCancelled) {
                    api.cancel()
                    retrofitCoroutine.clean()
                }
            }
            val response = work.await()
            if (response == null) {
                retrofitCoroutine.data?.httpState?.postValue(httpStatus)
            } else {
                if (response.isSuccessful) {
                    retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.SUCCESS))
                    retrofitCoroutine.data?.postValue(response.body())
                } else {
                    retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus(response.code(), response.errorBody().toString()))
                }
            }
        }
    }
}

/**
 * @author : 华清松
 * 常用的Retrofit协程请求，方便简单的接口调用，统一的请求状态管理，成功后直接执行onSuccess中的Function
 */
fun <ResultType> CoroutineScope.retrofitWithSuccess(
        dsl: RetrofitCoroutineScope.DslWithSuccess<ResultType>.() -> Unit
) {
    val retrofitCoroutine = RetrofitCoroutineScope.DslWithSuccess<ResultType>()
    retrofitCoroutine.dsl()
    this.launch(Dispatchers.Main) {
        var httpStatus = BaseHttpStatus(200, "请求成功")
        retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.LOADING))
        retrofitCoroutine.api?.let { api ->
            val work = async(Dispatchers.IO) {
                try {
                    api.execute()
                } catch (e: ConnectException) {
                    httpStatus = BaseHttpStatus(404, e.message, e)
                    null
                } catch (e: Exception) {
                    httpStatus = BaseHttpStatus(-1, e.message, e)
                    null
                }
            }
            work.invokeOnCompletion {
                if (work.isCancelled) {
                    api.cancel()
                    retrofitCoroutine.clean()
                }
            }
            val response = work.await()
            if (response == null) {
                retrofitCoroutine.data?.httpState?.postValue(httpStatus)
            } else {
                if (response.isSuccessful) {
                    retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.SUCCESS))
                    retrofitCoroutine.onSuccess?.invoke(response.body())
                } else {
                    retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus(response.code(), response.errorBody().toString()))
                }
            }
        }
    }
}


