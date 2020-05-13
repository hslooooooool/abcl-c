package qsos.lib.netservice.mock

import qsos.lib.netservice.BuildConfig
import java.util.*

interface IMockData {

    companion object {
        var openMockData: Boolean = false
        var dataBySdCard: Boolean = false
    }

    val method: String
    val group: String
    val path: String
    val filename: String
    val requestTime: Long

    fun key(): String

    fun path(): String

    fun mock(): Boolean
}

abstract class AbstractMockData : IMockData {

    override fun key(): String {
        return method.toUpperCase(Locale.ENGLISH) + path
    }

    override fun path(): String {
        return "mock/$group/$filename"
    }

    override fun mock(): Boolean {
        return BuildConfig.DEBUG
    }
}