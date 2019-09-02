package qsos.lib.netservice.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import qsos.lib.base.base.BaseApplication
import qsos.lib.base.utils.LogUtil
import qsos.lib.base.utils.SharedPreUtils
import java.io.IOException

/**
 * @author 华清松
 * 添加 COOKIE 拦截器
 */
class AddCookiesInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val token = SharedPreUtils.getStr(BaseApplication.appContext, "token") ?: ""
        builder.addHeader("token", token)
        builder.addHeader("device", "Android")
        builder.addHeader("applicationId", "ABCL-LIB")
        LogUtil.i("Cookie拦截器", "Header >> token=$token \n device=Android \n applicationId=ABCL-LIB")
        return chain.proceed(builder.build())
    }
}