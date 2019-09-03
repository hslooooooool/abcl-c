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

    /**TODO 统一处理异常，如重新登录、强制下线、异常反馈、网络检查*/
    private fun dealGlobalException(ex: GlobalException) {

    }
}