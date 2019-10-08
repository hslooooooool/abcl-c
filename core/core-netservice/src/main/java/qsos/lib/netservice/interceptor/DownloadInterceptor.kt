package qsos.lib.netservice.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import qsos.lib.netservice.file.DownloadBody
import qsos.lib.netservice.file.ProgressListener
import java.io.IOException

/**
 * @author : 华清松
 * 下载拦截器
 */
class DownloadInterceptor(private val listener: ProgressListener) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        return originalResponse.newBuilder()
                .body(DownloadBody(originalResponse.body()!!, listener))
                .build()
    }

}
