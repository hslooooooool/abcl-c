# 概要
ABCL-C层提供一些独立功能供使用，各功能相互独立，采用kotlin协程进行线程处理。
网络请求提供kotlin协程和RxJava两种方式；
表单组件采用了Room数据库进行操作；
异常日志提供日志拦截与保存，便于上传日志到服务器进行记录分析，采用了Timber日志框架进行操作，拦截了Warm级别以上的日志

- 最新版本
core_vision=[![](https://jitpack.io/v/hslooooooool/abcl-c.svg)](https://jitpack.io/#hslooooooool/abcl-c)

引用C层全部功能：
```
dependencies {
    implementation 'com.github.hslooooooool.abcl-c:core_vision'
}
```
或单独引用以下功能

## 功能模块
独立功能模块涵盖所有可单独实现的功能，涵盖以下功能：
- 网络请求
```
dependencies {
    implementation 'com.github.hslooooooool.abcl-c:core-netservice:core_vision'
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
- 图片加载-Glide封装
- 异常捕获
```
dependencies {
    implementation 'com.github.hslooooooool.abcl-c:core-exception:core_vision'
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
- 文件上传与下载(网络请求模块包含实现案例)
```
dependencies {
    implementation 'com.github.hslooooooool.abcl-c:core-netservice:core_vision'
}
```
- 文件选择（拍照、录像、录音、文件选择）
- 文件解压缩
- 文件读写工具
- 动态表单
提供包括文本展示、文本输入、单选/多选、位置设置、日期设置、人员设置、附件（拍照/图库/视频/语音/文件）设置等功能，采用Room数据库进行数据操作
```
dependencies {
    implementation 'com.github.hslooooooool.abcl-c:core-form:core_vision'
}
```
在你的application中初始化表单功能实现
```
open class AppApplication : BaseApplication(){

    override fun onCreate() {
        super.onCreate()

        ...

        /**配置表单文件操作代理实现*/
        FormConfigHelper.init(FormConfig())
    }
}
```
其中FormConfig为IFormConfig接口实现类，表单中文件、位置等操作将调用此接口进行操作并设置数据，你可参考app模块中FormConfig类的处理，
处理方法传递了表单项ID和其他参数，你可以使用表单项ID直接操作数据库或若不想直接操作数据库可使用传递的参数进行操作，实现案例：
```
class FormConfig : IFormConfig {

    override fun takeCamera(formItemId: Long, onSuccess: (FormValueOfFile) -> Any) {
        Timber.tag("表单文件代理").i("拍照")
        CoroutineScope(Job()).launch(Dispatchers.Main) {
            val takeFile = async(Dispatchers.IO) {
                val file = FormValueOfFile(fileId = "0001", fileName = "拍照", filePath = "/0/data/vip.qsos.demo/temp/logo.png", fileType = ".png", fileUrl = "http://www.qsos.vip/resource/logo.png", fileCover = "http://www.qsos.vip/resource/logo.png")
                file
            }
            val file = takeFile.await()
            onSuccess.invoke(file)
        }
    }

    ...
}
```
- 动态流程
- web容器
- JsBridge调用

# 项目清单
- [ABCL安卓快速开发框架](https://github.com/hslooooooool/abcl)
- [ABCL安卓快速开发框架之L层](https://github.com/hslooooooool/abcl-l)
- [ABCL安卓快速开发框架之C层](https://github.com/hslooooooool/abcl-c)
- [测试使用的后台代码](https://github.com/hslooooooool/ktorm-demo)
