package qsos.lib.netservice.mock

import android.content.Context
import android.os.Environment
import okhttp3.*
import java.io.*
import java.util.*
import java.util.concurrent.TimeUnit

class MockApiInterceptor constructor(
        private val appContext: Context
) : Interceptor {

    private val mMockDataMap: MutableMap<String, IMockData> = HashMap()

    fun addMockData(mockData: AbstractMockData): MockApiInterceptor {
        mMockDataMap[mockData.key()] = mockData
        return this
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val key = request.method() + request.url().url().path
        val mock = mMockDataMap[key]
        return if (mock != null && mock.mock()) {
            requestMockData(request, mock)
        } else {
            chain.proceed(request)
        }
    }

    private fun requestMockData(request: Request, mock: IMockData): Response {
        sleep(mock.requestTime)
        val jsonData = readMockData(mock.path())
        return Response.Builder()
                .code(200)
                .message(jsonData)
                .request(request)
                .protocol(Protocol.HTTP_1_0)
                .body(
                        ResponseBody.create(
                                MediaType.parse("application/json"),
                                jsonData.toByteArray(charset("UTF-8"))
                        )
                )
                .addHeader("content-type", "application/json")
                .build()
    }

    private fun readMockData(path: String): String {
        var mockData: String
        val sb = StringBuilder()
        val inputStream: InputStream?
        var reader: BufferedReader? = null
        try {
            inputStream = getMockData(path)
            reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            mockData = sb.toString()
        } catch (e: Exception) {
            mockData = ""
        } finally {
            try {
                reader?.close()
            } catch (e: Exception) {
                mockData = ""
            }
        }
        return mockData
    }

    @Throws(IOException::class)
    private fun getMockData(relativePath: String): InputStream {
        val inputStream: InputStream
        inputStream = if (IMockData.dataBySdCard) {
            val file = File(Environment.getExternalStorageState(), relativePath)
            FileInputStream(file)
        } else {
            appContext.assets.open(relativePath)
        }
        return inputStream
    }

    private fun sleep(time: Long) {
        if (time > 0) {
            TimeUnit.MILLISECONDS.sleep(time)
        }
    }

}