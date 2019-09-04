package qsos.lib.netservice.expand

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import qsos.lib.netservice.data.BaseHttpLiveData
import qsos.lib.netservice.data.BaseHttpStatus
import qsos.lib.netservice.data.BaseResponse
import qsos.lib.netservice.data.HttpStatusEnum
import retrofit2.Call
import java.net.ConnectException

/**
 * @author : 华清松
 * 默认实现，请求结果必须为 BaseResponse
 * @see BaseResponse
 * Kotlin协程配置Retrofit请求处理逻辑
 */
class DefaultRetrofitCoroutineScope {

    class DslByDef<ResultType> : AbsRetrofitCoroutineScope.Dsl<ResultType>() {
        var api: (Call<BaseResponse<ResultType>>)? = null
        override fun clean() {
            super.clean()
            api = null
        }
    }

    class DslByLiveDataWithDef<ResultType> : AbsRetrofitCoroutineScope.DslWithLiveData<ResultType>() {
        var api: (Call<BaseResponse<ResultType>>)? = null
        var data: (BaseHttpLiveData<ResultType>)? = null
        override fun clean() {
            super.clean()
            api = null
        }
    }

    class DslWithSuccessByDef<ResultType> : AbsRetrofitCoroutineScope.DslWithSuccess<ResultType>() {
        var api: (Call<BaseResponse<ResultType>>)? = null
        var data: (BaseHttpLiveData<ResultType>)? = null
        override fun clean() {
            super.clean()
            api = null
        }
    }
}

/**
 * @author : 华清松
 * 默认实现，请求结果必须为 BaseResponse
 * @see BaseResponse
 * 常用的Retrofit协程请求，方便简单的接口调用，统一的请求状态管理，自行对请求状态进行处理
 */
fun <ResultType> CoroutineScope.retrofitByDef(
        dslByDef: DefaultRetrofitCoroutineScope.DslByDef<ResultType>.() -> Unit
) {
    val retrofitCoroutine = DefaultRetrofitCoroutineScope.DslByDef<ResultType>()
    retrofitCoroutine.dslByDef()
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
            work.invokeOnCompletion {
                if (work.isCancelled) {
                    // 释放缓存
                    api.cancel()
                    retrofitCoroutine.clean()
                }
            }
            val response = work.await()
            if (response == null) {
                retrofitCoroutine.onFailed?.invoke(httpStatus.statusCode, httpStatus.statusMsg, httpStatus.statusError)
            } else {
                if (response.isSuccessful) {
                    retrofitCoroutine.onSuccess?.invoke(response.body()?.data)
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
 * 默认实现，请求结果必须为 BaseResponse
 * @see BaseResponse
 * 常用的Retrofit协程请求，方便简单的接口调用，统一的请求状态管理，通过LiveData直接更新UI
 */
fun <ResultType> CoroutineScope.retrofitWithLiveDataByDef(
        dslByDef: DefaultRetrofitCoroutineScope.DslByLiveDataWithDef<ResultType>.() -> Unit
) {
    val retrofitCoroutine = DefaultRetrofitCoroutineScope.DslByLiveDataWithDef<ResultType>()
    retrofitCoroutine.dslByDef()
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
fun <ResultType> CoroutineScope.retrofitWithSuccessByDef(
        dslByDef: DefaultRetrofitCoroutineScope.DslWithSuccessByDef<ResultType>.() -> Unit
) {
    val retrofitCoroutine = DefaultRetrofitCoroutineScope.DslWithSuccessByDef<ResultType>()
    retrofitCoroutine.dslByDef()
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
                    retrofitCoroutine.onSuccess?.invoke(response.body()?.data)
                } else {
                    retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus(response.code(), response.errorBody().toString()))
                }
            }
        }
    }
}


