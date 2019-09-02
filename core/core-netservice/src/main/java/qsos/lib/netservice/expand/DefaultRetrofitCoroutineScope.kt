package qsos.lib.netservice.expand

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import qsos.core.lib.utils.net.NetUtil
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
    if (!NetUtil.netWorkIsConnected) {
        retrofitCoroutine.onFailed?.invoke(HttpStatusEnum.NO_NET.code, HttpStatusEnum.NO_NET.msg)
    } else {
        this.launch(Dispatchers.Main) {
            retrofitCoroutine.api?.let { api ->
                // IO线程执行网络请求
                val work = async(Dispatchers.IO) {
                    retrofitCoroutine.onStart?.invoke()
                    try {
                        // 协程内同步进行网络请求
                        api.execute()
                    } catch (e: ConnectException) {
                        // 网络连接异常
                        retrofitCoroutine.onFailed?.invoke(HttpStatusEnum.NO_NET.code, HttpStatusEnum.NO_NET.msg)
                        null
                    } catch (e: Exception) {
                        // 其它异常
                        e.printStackTrace()
                        retrofitCoroutine.onFailed?.invoke(HttpStatusEnum.ERROR.code, e.message.toString())
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
                retrofitCoroutine.onComplete?.invoke()
                response?.let {
                    // 网络请求完成后执行判断
                    if (response.isSuccessful) {
                        // 服务器处理成功
                        if (response.code() == 200) {
                            retrofitCoroutine.onSuccess?.invoke(response.body()?.data)
                        } else {
                            retrofitCoroutine.onFailed?.invoke(response.body()!!.code, response.body()?.msg
                                    ?: "服务器异常")
                        }
                    } else {
                        // 服务器处理失败，按服务标准对异常进行统一处理，回执码如：400,401,403,404,500,501,504等
                        retrofitCoroutine.onFailed?.invoke(response.code(), response.errorBody().toString())
                    }
                }
            }
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
    if (!NetUtil.netWorkIsConnected) {
        retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.NO_NET))
    } else {
        this.launch(Dispatchers.Main) {
            retrofitCoroutine.api?.let { api ->
                val work = async(Dispatchers.IO) {
                    retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.LOADING))
                    try {
                        api.execute()
                    } catch (e: ConnectException) {
                        retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.NO_NET))
                        null
                    } catch (e: Exception) {
                        e.printStackTrace()
                        retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.ERROR))
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
                retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.FINISH))
                response?.let {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.SUCCESS))
                            retrofitCoroutine.data?.postValue(response.body())
                        } else {
                            retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus(response.body()!!.code, response.body()?.msg
                                    ?: "服务器异常"))
                        }
                    } else {
                        retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.ERROR))
                    }
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
    if (!NetUtil.netWorkIsConnected) {
        retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.NO_NET))
    } else {
        this.launch(Dispatchers.Main) {
            retrofitCoroutine.api?.let { api ->
                val work = async(Dispatchers.IO) {
                    retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.LOADING))
                    try {
                        api.execute()
                    } catch (e: ConnectException) {
                        retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.NO_NET))
                        return@async null
                    } catch (e: Exception) {
                        e.printStackTrace()
                        retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.ERROR))
                        return@async null
                    }
                }
                work.invokeOnCompletion {
                    if (work.isCancelled) {
                        api.cancel()
                        retrofitCoroutine.clean()
                    }
                }
                val response = work.await()
                retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.FINISH))
                response?.let {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.SUCCESS))
                            retrofitCoroutine.onSuccess?.invoke(response.body()?.data)
                        } else {
                            retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus(response.body()!!.code, response.body()?.msg
                                    ?: "服务器异常"))
                        }

                    } else {
                        retrofitCoroutine.data?.httpState?.postValue(BaseHttpStatus.base(HttpStatusEnum.ERROR))
                    }
                }
            }
        }
    }
}


