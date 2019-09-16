package qsos.core.lib.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * @author : 华清松
 * 用于存放Rx请求与资源释放处理
 */
interface IDisposable {
    /**存放Rx请求*/
    var mCompositeDisposable: CompositeDisposable?

    /**添加Rx请求*/
    fun addDispose(disposable: Disposable)

    /**释放Rx请求*/
    fun dispose()
}