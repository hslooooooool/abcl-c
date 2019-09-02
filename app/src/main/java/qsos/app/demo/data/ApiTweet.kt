package qsos.app.demo.data

import io.reactivex.Observable
import qsos.lib.netservice.data.BaseResponse
import retrofit2.Call
import retrofit2.http.GET

/**
 * @author : 华清松
 * 朋友圈接口
 */
interface ApiTweet {

    @GET("/add")
    fun add(): Call<BaseResponse<EmployeeBeen>>

    @GET("/delete")
    fun delete(): Observable<BaseResponse<String>>

    @GET("/one")
    fun one(): Call<BaseResponse<EmployeeBeen>>

    @GET("/list")
    fun list(): Call<BaseResponse<List<EmployeeBeen>>>

}