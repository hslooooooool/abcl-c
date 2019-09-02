package qsos.lib.netservice.file

/**
 * @author : 华清松
 * 文件上传/下载进度监听接口
 */
interface ProgressListener {

    /**@param progress 已有长度
     * @param length 总长度
     * @param success 是否完成
     */
    fun progress(progress: Int, length: Long, success: Boolean)
}