package qsos.lib.netservice.file

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import qsos.lib.base.callback.OnTListener
import qsos.lib.netservice.data.BaseResponse
import kotlin.coroutines.CoroutineContext

/**
 * @author : 华清松
 * 文件上传与下载帮助类
 */
class FileHelper : IFileModel {

    private val mJob: CoroutineContext = Dispatchers.Main + Job()
    private lateinit var imlFileModel: IFileModel

    override fun downloadFile(fileEntity: HttpFileEntity, listener: OnTListener<HttpFileEntity>) {
        if (this::imlFileModel.isInitialized) {
            imlFileModel.downloadFile(fileEntity, listener)
        } else {
            imlFileModel = FileRepository(mJob)
            imlFileModel.downloadFile(fileEntity, listener)
        }
    }

    override fun uploadFile(fileEntity: HttpFileEntity, listener: OnTListener<HttpFileEntity>) {
        if (this::imlFileModel.isInitialized) {
            imlFileModel.uploadFile(fileEntity, listener)
        } else {
            imlFileModel = FileRepository(mJob)
            imlFileModel.uploadFile(fileEntity, listener)
        }
    }

    override fun uploadFile(fileEntityList: List<HttpFileEntity>, listener: OnTListener<BaseResponse<List<HttpFileEntity>>>) {
        if (this::imlFileModel.isInitialized) {
            imlFileModel.uploadFile(fileEntityList, listener)
        } else {
            imlFileModel = FileRepository(mJob)
            imlFileModel.uploadFile(fileEntityList, listener)
        }
    }

    override fun clear() {
        mJob.cancel()
    }
}