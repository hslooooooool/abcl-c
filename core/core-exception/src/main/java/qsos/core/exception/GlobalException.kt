package qsos.core.exception

/**
 * @author : 华清松
 * 全局异常参数实体
 */
interface IGlobalException {
    /**异常码
     * 定义：
     * -9~9保留为框架使用
     * -2JSON解析异常 -1未知 0空指针
     * 100~600保留为HTTP(S)请求码
     * */
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

    constructor(error: Throwable) {
        this.code = -1
        this.msg = message ?: error.message ?: "未知异常"
        this.error = error
    }

    constructor(code: Int, error: Throwable? = null) {
        this.code = code
        this.msg = message ?: error?.message ?: "未知异常"
        this.error = error
    }

    constructor(code: Int, msg: String?, error: Throwable? = null) {
        this.code = code
        this.msg = msg ?: message ?: error?.message ?: "未知异常"
        this.error = error
    }
}