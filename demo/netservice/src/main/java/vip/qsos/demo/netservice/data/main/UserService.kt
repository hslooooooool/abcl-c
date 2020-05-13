package vip.qsos.demo.netservice.data.main

import android.content.Context
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import qsos.lib.netservice.data.BaseResponse
import qsos.lib.netservice.mock.MockApiInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import vip.qsos.demo.netservice.data.mock.UserMockData
import java.util.concurrent.TimeUnit

interface UserService {

    companion object {

        private const val ENDPOINT = "http://127.0.0.1/"
        var appContext: Context? = null
        val INSTANCE: UserService by lazy {
            val mClient = OkHttpClient.Builder()
            mClient.connectTimeout(8, TimeUnit.SECONDS)
            appContext?.let {
                val interceptor = MockApiInterceptor(it)
                interceptor.addMockData(UserMockData())
                mClient.addInterceptor(interceptor)
            }
            Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .callFactory(mClient.build())
                    .addConverterFactory(GsonConverterFactory.create(Gson()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(UserService::class.java)
        }
    }

    @GET("api/user")
    suspend fun getUserInfo(): BaseResponse<UserInfo>

}