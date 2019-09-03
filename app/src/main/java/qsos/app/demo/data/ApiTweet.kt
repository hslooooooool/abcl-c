package qsos.app.demo.data

import io.reactivex.Observable
import qsos.lib.netservice.data.BaseResponse
import retrofit2.Call
import retrofit2.http.*

/**
 * @author : 华清松
 * 朋友圈接口
 */
interface ApiTweet {

    @POST("/add")
    fun add(): Call<BaseResponse<EmployeeBeen>>

    @DELETE("/delete")
    fun delete(): Observable<BaseResponse<String>>

    @DELETE("/clear")
    suspend fun clear(): BaseResponse<String>

    @GET("/one")
    fun one(): Call<BaseResponse<EmployeeBeen>>

    @GET("/list")
    fun list(): Call<BaseResponse<List<EmployeeBeen>>>

    @PUT("/update")
    fun put(@Body em: EmployeeBeen): Call<BaseResponse<EmployeeBeen>>

}