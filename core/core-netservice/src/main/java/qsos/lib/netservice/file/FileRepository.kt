package qsos.lib.netservice.file

import android.annotation.SuppressLint
import android.text.TextUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import qsos.core.lib.utils.file.FileUtils
import qsos.lib.base.callback.OnTListener
import qsos.lib.netservice.ApiEngine
import qsos.lib.netservice.data.BaseResponse
import qsos.lib.netservice.data.HttpStatusEnum
import qsos.lib.netservice.expand.retrofit
import qsos.lib.netservice.expand.retrofitByDef
import kotlin.coroutines.CoroutineContext

/**
 * @author : 华清松
 * 文件上传与下载实现
 */
@SuppressLint("CheckResult")
class FileRepository(
        private val mCoroutineContext: CoroutineContext
) : IFileModel {

    override fun clear() {

    }

    override fun downloadFile(fileEntity: HttpFileEntity, listener: OnTListener<HttpFileEntity>) {
        if (TextUtils.isEmpty(fileEntity.url)) {
            fileEntity.progress = -1
            fileEntity.loadSuccess = false
            fileEntity.loadMsg = "下载失败，链接错误"
            listener.back(fileEntity)
        } else {
            val saveName = fileEntity.filename ?: FileUtils.getFileNameByUrl(fileEntity.url)
            val savePath = fileEntity.path ?: "${FileUtils.DOWNLOAD_PATH}/$saveName"
            fileEntity.path = savePath
            ApiEngine.createService(ApiDownloadFile::class.java).downloadFile(fileEntity.url!!)
                    .subscribeOn(Schedulers.io())
                    .map {
                        FileUtils.writeBodyToFile(savePath, it)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                fileEntity.progress = 100
                                fileEntity.loadSuccess = true
                                fileEntity.loadMsg = "下载完成"
                                listener.back(fileEntity)
                            },
                            {
                                fileEntity.progress = -1
                                fileEntity.loadSuccess = false
                                fileEntity.loadMsg = "下载失败，${it.message}"
                                listener.back(fileEntity)
                            }
                    )
        }
    }

    override fun uploadFile(fileEntity: HttpFileEntity, listener: OnTListener<HttpFileEntity>) {
        val uploadFile = FileUtils.getFile(fileEntity.path)
        if (uploadFile != null) {
            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), uploadFile)
            val uploadBody = UploadBody(requestBody, object : ProgressListener {
                override fun progress(progress: Int, length: Long, success: Boolean) {
                    fileEntity.progress = progress
                    fileEntity.loadSuccess = false
                    listener.back(fileEntity)
                }
            })
            val part = MultipartBody.Part.createFormData("uploadFile", uploadFile.name, uploadBody)
            CoroutineScope(mCoroutineContext).retrofitByDef<HttpFileEntity> {
                api = ApiEngine.createService(ApiUploadFile::class.java).uploadFile(part)
                onStart {
                    fileEntity.progress = 0
                    listener.back(fileEntity)
                }
                onSuccess {
                    fileEntity.loadSuccess = true
                    fileEntity.progress = 100
                    fileEntity.url = it!!.url
                    listener.back(fileEntity)
                }
                onFailed { _, _, _ ->
                    fileEntity.url = null
                    fileEntity.progress = -1
                    fileEntity.loadSuccess = false
                    listener.back(fileEntity)
                }
            }
        } else {
            fileEntity.url = null
            fileEntity.progress = -1
            fileEntity.loadSuccess = false
            listener.back(fileEntity)
        }
    }

    override fun uploadFile(fileEntityList: List<HttpFileEntity>, listener: OnTListener<BaseResponse<List<HttpFileEntity>>>) {
        val parts = arrayListOf<MultipartBody.Part>()
        fileEntityList.forEach { fileEntity ->
            val uploadFile = FileUtils.getFile(fileEntity.path)
            if (uploadFile != null) {
                val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), uploadFile)
                val uploadBody = UploadBody(requestBody, object : ProgressListener {
                    override fun progress(progress: Int, length: Long, success: Boolean) {
                        fileEntity.progress = progress
                        fileEntity.loadSuccess = false
                        listener.back(BaseResponse(HttpStatusEnum.LOADING.code, HttpStatusEnum.LOADING.msg, fileEntityList))
                    }
                })
                val part: MultipartBody.Part = MultipartBody.Part.createFormData("uploadFile", uploadFile.name, uploadBody)
                parts.add(part)
            }
        }
        CoroutineScope(mCoroutineContext).retrofit<BaseResponse<List<HttpFileEntity>>> {
            api = ApiEngine.createService(ApiUploadFile::class.java).uploadFiles(parts)
            onSuccess {
                listener.back(it!!)
            }
            onFailed { code, msg, _ ->
                listener.back(BaseResponse(code, msg))
            }
        }
    }
}