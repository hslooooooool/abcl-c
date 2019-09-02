package qsos.lib.netservice.data

/**
 * @author : 华清松
 * 自行定义与拓展网络请求状态对象
 */
data class BaseHttpStatus(override val stateCode: Int, override val stateMsg: String) : IHttpStatusCode {
    companion object {
        fun base(status: HttpStatusEnum): BaseHttpStatus {
            return BaseHttpStatus(status.code, status.msg)
        }
    }
}

/**
 * @author : 华清松
 * 网络请求状态参数
 */
interface IHttpStatusCode {
    /**请求状态码*/
    val stateCode: Int
    /**请求回执信息*/
    val stateMsg: String
}

/**
 * @author : 华清松
 * 自行定义与拓展网络请求状态枚举，根据定义的回执码，判断网络请求结果
 */
enum class HttpStatusEnum(val code: Int, val msg: String) : IHttpStatusCode {
    NO_NET(-2, "网络连接失败") {
        override val stateCode = code
        override val stateMsg = msg
    },
    ERROR(-1, "请求失败") {
        override val stateCode = code
        override val stateMsg = msg
    },
    LOADING(0, "加载中") {
        override val stateCode = code
        override val stateMsg = msg
    },
    FINISH(1, "请求完成") {
        override val stateCode = code
        override val stateMsg = msg
    },
    SUCCESS(200, "请求成功") {
        override val stateCode = code
        override val stateMsg = msg
    }
}
