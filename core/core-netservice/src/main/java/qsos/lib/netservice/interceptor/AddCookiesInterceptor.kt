package qsos.lib.netservice.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import qsos.lib.base.utils.LogUtil
import java.io.IOException

/**
 * @author 华清松
 * 添加 COOKIE 拦截器
 */
class AddCookiesInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.addHeader("device", "Android")
        builder.addHeader("applicationId", "ABCL-LIB")
        LogUtil.i("Cookie拦截器", "Header >> device=Android \n applicationId=ABCL-LIB")
        return chain.proceed(builder.build())
    }
}