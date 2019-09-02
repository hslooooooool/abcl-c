package qsos.lib.netservice.data

import androidx.lifecycle.MutableLiveData

/**
 * @author : 华清松
 * 如果数据请求存在多种状态，而这些状态需要被观察，则使用此类代替，
 * 观察的数据将默认持有一个可观测请求状态的 MutableLiveData 即 httpState
 */
class HttpLiveData<T> : MutableLiveData<T>() {
    /**网络请求状态被观察者*/
    val httpState = MutableLiveData<IHttpStatusCode>()
}

/**
 * @author : 华清松
 * 统一的返回实体，根据后台定义选用。如果数据请求存在多种状态，而这些状态需要被观察，则使用此类代替，
 * 观察的数据将默认持有一个可观测请求状态的 MutableLiveData 即 httpState 。
 * 标准模式下请求后返回数据默认包含 code 状态码；msg 请求回执信息；data 请求结果等数据
 */
class BaseHttpLiveData<T> : MutableLiveData<BaseResponse<T>>() {
    /**网络请求状态被观察者*/
    val httpState = MutableLiveData<IHttpStatusCode>()
}

/**
 * @author : 华清松
 * 统一的返回实体对象
 */
data class BaseResponse<T>(
        val code: Int = 200,
        val msg: String? = "请求成功",
        val data: T? = null
) : IHttpResult<T> {

    override fun httpSuccess(): Boolean {
        return code == 200
    }

    override fun httpResult(): T? {
        return data
    }

    override fun httpCode(): Int {
        return code
    }

    override fun httpMsg(): String {
        return msg ?: "请求提示"
    }

}

/**
 * @author : 华清松
 * 统一的网络请求结果数据实体结构
 */
interface IHttpResult<T> {
    /**请求是否成功*/
    fun httpSuccess(): Boolean

    /**请求的结果*/
    fun httpResult(): T?

    /**请求码*/
    fun httpCode(): Int

    /**回执信息*/
    fun httpMsg(): String
}