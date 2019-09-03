package vip.qsos.exception

/**
 * @author : 华清松
 * 全局异常参数实体
 */
interface IGlobalException {
    /**异常码*/
    var code: Int
    /**异常信息*/
    var msg: String?
    /**全局异常*/
    var error: Throwable?
}

/**
 * @author : 华清松
 * 全局异常实体
 */
class GlobalException : IGlobalException, Exception {
    override var code: Int = 200
    override var msg: String? = ""
    override var error: Throwable? = null

    constructor()
    constructor(error: Throwable){
        this.code = -1
        this.msg =  message ?: "未知异常"
        this.error = error
    }
    constructor(code: Int, msg: String?, error: Throwable? = null) {
        this.code = code
        this.msg = msg ?: message ?: "未知异常"
        this.error = error
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