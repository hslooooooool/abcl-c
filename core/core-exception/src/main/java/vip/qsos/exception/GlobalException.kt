package vip.qsos.exception

/**
 * @author : 华清松
 * 全局异常实体
 */
data class GlobalException(
        var exceptionType: GlobalExceptionType,
        var exception: Throwable
) {
    /**服务器主动发出的异常，通常为业务限制异常，应在具体页面内部处理*/
    class ServerException(var code: Int, var msg: String = "服务器错误") : RuntimeException("服务器错误:code=$code\tmsg=$msg") {
        override fun toString(): String {
            return "服务器异常 >>>>>> code=$code\tmsg=$msg\n"
        }
    }

    override fun toString(): String {
        return "全局异常$exceptionType >>>>>>${exception.localizedMessage}\n"
    }
}

/**
 * @author : 华清松
 * 全局异常类型枚举，取值范围应被全局保留，其它错误类型请使用保留值以外的数值
 * @param minCode code 值最小范围
 * @param maxCode code 值最大范围
 */
enum class GlobalExceptionType(private val minCode: Int, private val maxCode: Int) {
    NullPointerException(-1, -1),
    ServerException(0, 100),
    HttpException(100, 500),
    JsonException(600, 600),
    ConnectException(601, 601),
    TimeoutException(602, 602),
    OtherException(700, 999);
}