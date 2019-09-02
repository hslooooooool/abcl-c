package qsos.lib.netservice.file

import io.reactivex.Flowable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * @author : 华清松
 * 文件下载接口
 */
interface ApiDownloadFile {

    /**
     * 单文件下载
     * @param url 待下载的文件链接
     * @return ResponseBody
     */
    @Streaming
    @GET
    fun downloadFile(
            @Url url: String
    ): Flowable<ResponseBody>

}