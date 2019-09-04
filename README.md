# 概要
最新版本
[![](https://jitpack.io/v/hslooooooool/abcl-c.svg)](https://jitpack.io/#hslooooooool/abcl-c)

引用C层全部功能：
```
dependencies {
    implementation 'com.github.hslooooooool.abcl-c:0.1'
}
```
或单独引用以下功能

## 功能模块
独立功能模块涵盖所有可单独实现的功能，涵盖以下功能：
- 网络请求[![](https://jitpack.io/v/hslooooooool/abcl-c.svg)](https://jitpack.io/#hslooooooool/abcl-c)
```
dependencies {
    implementation 'com.github.hslooooooool.abcl-c:core-netservice:0.1'
}
```
你的AppApplication如下配置
```
open class AppApplication : BaseApplication(){

    override fun onCreate() {
        super.onCreate()

        /**BASE_URL配置*/
        BaseConfig.BASE_URL = "http://192.168.1.11:8084"
    }

}
```
- 图片加载
- 异常捕获[![](https://jitpack.io/v/hslooooooool/abcl-c.svg)](https://jitpack.io/#hslooooooool/abcl-c)
```
dependencies {
    implementation 'com.github.hslooooooool.abcl-c:core-exception:0.1'
}
```
你的AppApplication如下配置
```
open class AppApplication : BaseApplication(){

    override fun onCreate() {
        super.onCreate()

        /**Timber日志*/
        Timber.plant(GlobalExceptionHelper.CrashReportingTree())
        /**全局异常捕获处理*/
        Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHelper)
        RxBus.toFlow(GlobalExceptionHelper.ExceptionEvent::class.java)
                .subscribe {
                    dealGlobalException(it.exception)
                }
    }

    /**统一处理异常，如重新登录、强制下线、异常反馈、网络检查*/
    private fun dealGlobalException(ex: GlobalException) {

    }
}
```
- 埋点统计
- 缓存管理
- 文件上传与下载(网络请求模块包含)[![](https://jitpack.io/v/hslooooooool/abcl-c.svg)](https://jitpack.io/#hslooooooool/abcl-c)
```
dependencies {
    implementation 'com.github.hslooooooool.abcl-c:core-netservice:0.1'
}
```
- 文件选择（拍照、录像、录音、文件选择）
- 文件解压缩
- 文件读写工具
- web容器
- JsBridge调用

# 项目清单
- [ABCL安卓快速开发框架](https://github.com/hslooooooool/abcl)
- [ABCL安卓快速开发框架之L层](https://github.com/hslooooooool/abcl-l)
- [ABCL安卓快速开发框架之C层](https://github.com/hslooooooool/abcl-c)