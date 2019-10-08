package qsos.core.lib.utils.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import qsos.lib.base.base.BaseApplication

/**
 * @author 华清松
 * 网络连接判断工具
 */
object NetUtil {
    private var mConnectivityManager: ConnectivityManager = BaseApplication.appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    /**网络是否连接*/
    var netWorkIsConnected: Boolean = true
        get() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                isConnected()
            }
            return field
        }

    /**是否已连接WIFI，一般用于下载时提示用户*/
    val netWorkIsConnectedByWifi
        get() = isWifi()

    init {
        // 监控网络变化
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mConnectivityManager.requestNetwork(NetworkRequest.Builder().build(), object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    netWorkIsConnected = true
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    netWorkIsConnected = false
                }
            })
        }
    }

    /**判断网络是否连接*/
    @SuppressWarnings
    fun isConnected(): Boolean {
        val info = mConnectivityManager.activeNetworkInfo
        if (null != info && info.isConnected) {
            if (info.detailedState == NetworkInfo.DetailedState.CONNECTED) {
                return true
            }
        }
        return false
    }

    /**判断是否是wifi连接*/
    @SuppressWarnings
    fun isWifi(): Boolean {
        return mConnectivityManager.activeNetworkInfo.subtype == 1
    }

}