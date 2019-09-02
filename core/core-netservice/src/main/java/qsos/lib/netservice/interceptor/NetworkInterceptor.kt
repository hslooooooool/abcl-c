package qsos.lib.netservice.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import qsos.core.lib.utils.net.NetUtil
import java.io.IOException
import java.net.ConnectException

/**
 * @author 华清松
 * 网络请求拦截器，用于请求前监控网络连接状态
 */
class NetworkInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!NetUtil.netWorkIsConnected) {
            throw ConnectException()
        }
        return chain.proceed(chain.request())
    }
}