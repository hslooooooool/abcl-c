package qsos.lib.netservice.file

import okhttp3.MultipartBody
import qsos.lib.netservice.data.BaseResponse
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * @author : 华清松
 * 文件上传接口
 */
interface ApiUploadFile {
    companion object {
        const val GROUP = "/upload"
    }

    /**
     * 单文件上传
     * @param file 待上传的文件
     * @return BaseHttpResult<String> 文件在服务器的路径，因设计为去除host的路径，
     * 建议：保证后期服务器地址更换带来的数据不一致问题，如访问路径是 http://www.baidu.com/file/20190502/head.png
     * 则返回的路径应是 file/20190502/head.png ，host由协商确定
     */
    @Multipart
    @POST("$GROUP/file")
    fun uploadFile(
            @Part file: MultipartBody.Part
    ): Call<BaseResponse<HttpFileEntity>>

    /**
     * 多文件上传
     * @param files 待上传的文件列表
     * @return BaseHttpResult<String> 文件在服务器的路径，因设计为去除host的路径，
     * 建议：保证后期服务器地址更换带来的数据不一致问题，如访问路径是 http://www.baidu.com/file/20190502/head.png
     * 则返回的路径应是 file/20190502/head.png ，host由协商确定
     */
    @Multipart
    @POST("$GROUP/files")
    fun uploadFiles(
            @Part files: List<MultipartBody.Part>
    ): Call<BaseResponse<List<HttpFileEntity>>>

}