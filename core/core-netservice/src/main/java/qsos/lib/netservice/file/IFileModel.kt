package qsos.lib.netservice.file

import qsos.core.lib.utils.file.FileUtils
import qsos.lib.base.callback.OnTListener
import qsos.lib.netservice.data.BaseResponse

/**
 * @author : 华清松
 * 文件数据接口
 */
interface IFileModel {

    /**下载文件
     * @param fileEntity 需传入文件的下载地址 fileEntity.url。想将文件保存到指定目录，需同时传入
     * fileEntity.path，否者将保存到默认目录 FileUtils.DOWNLOAD_PATH
     * @param listener 文件下载进度监听，进度更新在IO线程，结果更新在UI线程，注意判断
     *
     * @see FileUtils.DOWNLOAD_PATH
     * @see HttpFileEntity.path
     * */
    fun downloadFile(fileEntity: HttpFileEntity, listener: OnTListener<HttpFileEntity>)

    /**上传单文件
     * @param url 上传地址
     * @param fileEntity 上传的文件。需传入文件的上传本地路径 fileEntity.path。可携带上传标识，比如聊天文件上传，
     * 携带聊天数据 HttpFileEntity.adjoin=ChatEntity(id=1)，上传成功通过携带的聊天数据刷新对应的聊天文件上传进度与消息发送状态。
     * @param listener 文件上传进度监听，进度更新在IO线程，结果更新在UI线程，注意判断
     *
     * @see HttpFileEntity.path
     * @see HttpFileEntity.adjoin
     * */
    fun uploadFile(url: String, fileEntity: HttpFileEntity, listener: OnTListener<HttpFileEntity>)

    /**上传多文件
     * @param url 上传地址
     * @param fileEntityList 上传的文件列表。需传入文件的上传本地路径 fileEntity.path。可携带上传标识，比如聊天文件上传，
     * 携带聊天数据 HttpFileEntity.adjoin=ChatEntity(id=1)，上传成功通过携带的聊天数据刷新对应的聊天文件上传进度与消息发送状态。
     * @param listener 文件上传进度监听，进度更新在IO线程，结果更新在UI线程，注意判断
     *
     * @see HttpFileEntity.path
     * @see HttpFileEntity.adjoin
     * */
    fun uploadFile(url: String, fileEntityList: List<HttpFileEntity>, listener: OnTListener<BaseResponse<List<HttpFileEntity>>>)

    /**停止所有请求，清除缓存*/
    fun clear()
}