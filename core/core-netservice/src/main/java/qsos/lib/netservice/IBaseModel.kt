package qsos.lib.netservice

import kotlin.coroutines.CoroutineContext

/**
 * @author : 华清松
 * 使用ViewModel与kotlin协程进行网络请求时，在ViewModel类中配置CoroutineContext，且与ViewModel生命周期关联
 */
interface IBaseModel {
    val mJob: CoroutineContext
}