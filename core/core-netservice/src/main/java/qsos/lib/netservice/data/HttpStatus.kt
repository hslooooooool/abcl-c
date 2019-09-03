package qsos.lib.netservice.data

import java.net.ConnectException

/**
 * @author : 华清松
 * 自行定义与拓展网络请求状态对象
 */
data class BaseHttpStatus(
        override val statusCode: Int,
        override val statusMsg: String,
        override val statusError: Throwable? = null
) : IHttpStatusCode {

    companion object {
        fun base(status: HttpStatusEnum): BaseHttpStatus {
            return BaseHttpStatus(status.code, status.msg, status.statusError)
        }
    }
}

/**
 * @author : 华清松
 * 网络请求状态参数
 */
interface IHttpStatusCode {
    /**请求状态码*/
    val statusCode: Int
    /**请求回执信息*/
    val statusMsg: String?
    /**请求异常*/
    val statusError: Throwable?
}

/**
 * @author : 华清松
 * 自行定义与拓展网络请求状态枚举，根据定义的回执码，判断网络请求结果
 */
enum class HttpStatusEnum(val code: Int, val msg: String) : IHttpStatusCode {
    NO_NET(-2, "网络连接失败") {
        override val statusCode = code
        override val statusMsg = msg
        override val statusError = ConnectException("网络连接失败")
    },
    ERROR(-1, "请求失败") {
        override val statusCode = code
        override val statusMsg = msg
        override val statusError = Exception("请求失败，未知异常")
    },
    LOADING(0, "加载中") {
        override val statusCode = code
        override val statusMsg = msg
        override val statusError: Throwable? = null
    },
    SUCCESS(200, "请求成功") {
        override val statusCode = code
        override val statusMsg = msg
        override val statusError: Throwable? = null
    }
}
