package qsos.app.demo

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import qsos.core.lib.config.BaseConfig
import qsos.lib.base.base.BaseApplication
import qsos.lib.base.utils.rx.RxBus
import timber.log.Timber
import vip.qsos.exception.GlobalException
import vip.qsos.exception.GlobalExceptionHelper
import vip.qsos.exception.GlobalExceptionType

/**
 * @author : 华清松
 * AppApplication
 */
open class AppApplication : BaseApplication(), LifecycleOwner {
    init {
        // 设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(android.R.color.white, android.R.color.black)
            return@setDefaultRefreshHeaderCreator ClassicsHeader(context)
        }
        // 设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            layout.setPrimaryColorsId(android.R.color.white, android.R.color.black)
            return@setDefaultRefreshFooterCreator ClassicsFooter(context)
        }
    }

    override fun getLifecycle(): Lifecycle {
        return LifecycleRegistry(this)
    }

    @SuppressLint("CheckResult")
    override fun onCreate() {
        super.onCreate()

        BaseConfig.DEBUG = true
        BaseConfig.BASE_URL = "http://192.168.1.11:8084"

        /**Timber 日志*/
        Timber.plant(Timber.DebugTree())
        /**全局异常捕获处理*/
        Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHelper)

        RxBus.toFlow(GlobalExceptionHelper.ExceptionEvent::class.java)
                .subscribe {
                    dealGlobalException(it.exception)
                }
    }

    /**统一处理异常，如重新登录、强制下线、异常反馈、网络检查*/
    private fun dealGlobalException(ex: GlobalException) {
        when (ex.exceptionType) {
            GlobalExceptionType.HttpException -> {
                /**在此处理，应在弹出对应处置窗口*/
            }
            GlobalExceptionType.ConnectException -> {
                /**在此处理，应在弹出网络检测窗口，前往网络设置*/
            }
            GlobalExceptionType.JsonException, GlobalExceptionType.ServerException, GlobalExceptionType.TimeoutException -> {
                /**在此无需处理，应在具体请求页面进行交互*/
            }
            GlobalExceptionType.NullPointerException, GlobalExceptionType.OtherException -> {
                /**在此处理，应在弹出异常提交窗口，将异常发送给服务器*/
            }
        }
    }
}