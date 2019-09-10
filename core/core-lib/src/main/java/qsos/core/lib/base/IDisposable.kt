package qsos.core.lib.base

import io.reactivex.disposables.CompositeDisposable

/**
 * @author : 华清松
 * 用于存放Rx请求与资源释放处理
 */
interface IDisposable {
    /**存放Rx请求*/
    var mCompositeDisposable: CompositeDisposable?

    /**释放Rx请求*/
    fun dispose()
}