package qsos.core.lib.config

/**
 * @author : 华清松
 * 配置参数
 */
object BaseConfig {
    /**配置是否调试模式
     * 影响范围：OkHttp日志打印，Glide日志打印
     * */
    var DEBUG = true
    /**配置请求地址*/
    var BASE_URL = "http://192.168.1.7:8084/"
    /**共享文件*/
    var PROVIDER = "qsos.core.lib.provider"
}