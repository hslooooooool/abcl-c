package qsos.lib.netservice

import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @author : 华清松
 * 网络配置清单，通过 ApiEngine.init 传入集成此接口的 Class 即可
 * @see ApiEngine.init
 */
interface IHttpConfig {

    /**获取RetrofitBuilder自有配置
     * @see Retrofit.Builder
     * */
    fun getRetrofitBuilder(): Retrofit.Builder

    /**获取OkHttpBuilder自有配置
     * @see OkHttpClient.Builder
     * */
    fun getOkHttpBuilder(): OkHttpClient.Builder

}