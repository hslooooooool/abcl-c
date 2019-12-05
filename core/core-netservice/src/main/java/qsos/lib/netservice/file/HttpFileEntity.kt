package qsos.lib.netservice.file

/**
 * @author : 华清松
 * HTTP文件上传下载处理实体
 */
data class HttpFileEntity(
        /**文件封面路径*/
        var avatar: String? = null,
        /**网络路径*/
        var url: String?,
        /**本地路径*/
        var path: String?,
        /**文件名称*/
        var filename: String?,
        /**百分比进度*/
        var progress: Int = 0
) {
    /**文件类型*/
    var type: String? = null
    /**上传下载成功*/
    var loadSuccess = false
    /**上传下载结果提示信息*/
    var loadMsg: String? = ""
    /**
     * 伴随携带的数据，用于数据传递使用，比如发送消息中的文件时，文件上传成功返回结果后
     * 可通过adjoin得知是哪条消息的文件上传成功，以修改消息状态
     */
    var adjoin: Any? = null
}