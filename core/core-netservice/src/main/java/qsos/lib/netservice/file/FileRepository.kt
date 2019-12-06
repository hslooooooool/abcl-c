package qsos.lib.netservice.file

import android.annotation.SuppressLint
import android.text.TextUtils
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import qsos.core.lib.config.CoreConfig
import qsos.core.lib.utils.file.FileUtils
import qsos.core.lib.utils.json.DateDeserializer
import qsos.core.lib.utils.json.DateSerializer
import qsos.lib.base.callback.OnTListener
import qsos.lib.netservice.ApiEngine
import qsos.lib.netservice.data.BaseResponse
import qsos.lib.netservice.data.HttpStatusEnum
import qsos.lib.netservice.expand.retrofit
import qsos.lib.netservice.interceptor.AddCookiesInterceptor
import qsos.lib.netservice.interceptor.DownloadInterceptor
import qsos.lib.netservice.interceptor.NetworkInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
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
        mCoroutineContext.cancel()
    }

    override fun downloadFile(fileEntity: HttpFileEntity, listener: OnTListener<HttpFileEntity>) {
        if (TextUtils.isEmpty(fileEntity.url)) {
            fileEntity.progress = -1
            fileEntity.loadSuccess = false
            fileEntity.loadMsg = "下载失败，链接错误"
            listener.back(fileEntity)
        } else {
            val saveName = fileEntity.filename ?: FileUtils.getFileNameByPath(fileEntity.url)
            val savePath = fileEntity.path ?: "${FileUtils.DOWNLOAD_PATH}/$saveName"
            CoroutineScope(mCoroutineContext).launch(Dispatchers.Main) {
                fileEntity.progress = 0
                fileEntity.loadSuccess = false
                fileEntity.loadMsg = "开始下载"
                listener.back(fileEntity)

                val file = File(savePath)
                file.createNewFile()
                fileEntity.path = file.absolutePath

                val mGsonBuilder = GsonBuilder()
                mGsonBuilder.registerTypeAdapter(Date::class.java, DateDeserializer()).setDateFormat("yyyy-MM-dd HH:mm:ss").create()
                mGsonBuilder.registerTypeAdapter(Date::class.java, DateSerializer()).setDateFormat("yyyy-MM-dd HH:mm:ss").create()
                val mGsonConverterFactory: GsonConverterFactory = GsonConverterFactory.create(mGsonBuilder.create())

                val mBuild = Retrofit.Builder()
                        .baseUrl(CoreConfig.BASE_URL)
                        .addConverterFactory(mGsonConverterFactory)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

                val mClient = OkHttpClient.Builder()
                // 请求超时设置
                mClient.connectTimeout(8, TimeUnit.SECONDS)
                // 网络连接性拦截器
                mClient.addInterceptor(NetworkInterceptor())
                // COOKIE拦截器
                mClient.addInterceptor(AddCookiesInterceptor())
                mClient.addInterceptor(DownloadInterceptor(object : ProgressListener {
                    override fun progress(progress: Int, length: Long, success: Boolean) {
                        if (progress > fileEntity.progress) {
                            fileEntity.progress = progress
                            fileEntity.loadSuccess = false
                            fileEntity.loadMsg = "下载中$progress%"
                            listener.back(fileEntity)
                        }
                    }
                }))
                try {
                    val deferred = async(Dispatchers.IO) {
                        try {
                            mBuild.client(mClient.build()).build()
                                    .create(ApiDownloadFile::class.java)
                                    .downloadFile(fileEntity.url!!)
                                    .execute()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                    val response = deferred.await()
                    val body = response?.body()
                    if (response?.isSuccessful == true && body != null) {
                        val fileIO = async(Dispatchers.IO) {
                            FileUtils.writeBodyToFile(fileEntity.path!!, body)
                        }
                        val mFile = fileIO.await()
                        if (mFile == null) {
                            fileEntity.progress = -1
                            fileEntity.loadSuccess = false
                            fileEntity.loadMsg = "下载失败"
                        } else {
                            fileEntity.progress = 100
                            fileEntity.loadSuccess = true
                            fileEntity.loadMsg = "下载完成"
                        }
                        listener.back(fileEntity)
                    } else {
                        fileEntity.progress = -1
                        fileEntity.loadSuccess = false
                        fileEntity.loadMsg = "下载失败"
                        listener.back(fileEntity)
                    }
                } catch (e: Exception) {
                    fileEntity.progress = -1
                    fileEntity.loadSuccess = false
                    fileEntity.loadMsg = "下载失败"
                    listener.back(fileEntity)
                }
            }
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
            val part = MultipartBody.Part.createFormData("file", uploadFile.name, uploadBody)
            CoroutineScope(mCoroutineContext).retrofit<BaseResponse<HttpFileEntity>> {
                api = ApiEngine.createService(ApiUploadFile::class.java).uploadFile(part)
                onStart {
                    fileEntity.progress = 0
                    listener.back(fileEntity)
                }
                onSuccess {
                    if (it?.code == 200 && it.data != null) {
                        fileEntity.loadSuccess = true
                        fileEntity.progress = 100
                        fileEntity.url = it.data.url
                        fileEntity.avatar = it.data.avatar
                        listener.back(fileEntity)
                    } else {
                        fileEntity.url = null
                        fileEntity.progress = -1
                        fileEntity.loadSuccess = false
                        listener.back(fileEntity)
                    }
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
                val part: MultipartBody.Part = MultipartBody.Part.createFormData("file", uploadFile.name, uploadBody)
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