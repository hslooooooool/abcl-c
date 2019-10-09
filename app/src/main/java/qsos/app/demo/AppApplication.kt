package qsos.app.demo

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import qsos.app.demo.config.FormConfig
import qsos.app.demo.config.PlayerConfig
import qsos.core.exception.GlobalException
import qsos.core.exception.GlobalExceptionHelper
import qsos.core.form.utils.FormConfigHelper
import qsos.core.lib.config.BaseConfig
import qsos.core.player.PlayerConfigHelper
import qsos.lib.base.base.BaseApplication
import qsos.lib.base.utils.rx.RxBus
import timber.log.Timber

/**
 * @author : 华清松
 * AppApplication
 */
open class AppApplication(
        override var debugARouter: Boolean = true,
        override var debugTimber: Boolean = true
) : BaseApplication(), LifecycleOwner {
    init {
        // 设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(android.R.color.white, android.R.color.black)
            return@setDefaultRefreshHeaderCreator ClassicsHeader(context) as RefreshHeader
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
        /**BASE_URL配置*/
        BaseConfig.BASE_URL = "http://192.168.1.10:8084"
        BaseConfig.PROVIDER = "qsos.app.demo.provider"

        /**Timber 日志*/
        Timber.plant(GlobalExceptionHelper.CrashReportingTree())
        /**全局异常捕获处理*/
        Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHelper)
        RxBus.toFlow(GlobalExceptionHelper.ExceptionEvent::class.java).subscribe {
            dealGlobalException(it.exception)
        }

        /**配置表单文件操作代理实现*/
        FormConfigHelper.init(FormConfig())
        /**配置媒体预览操作代理实现，这里不初始化，则使用默认实现*/
        PlayerConfigHelper.init(PlayerConfig())
    }

    /**TODO 统一处理异常，如重新登录、强制下线、异常反馈、网络检查*/
    private fun dealGlobalException(ex: GlobalException) {

    }
}