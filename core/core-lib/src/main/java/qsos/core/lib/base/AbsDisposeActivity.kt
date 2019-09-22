package qsos.core.lib.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import qsos.lib.base.base.activity.BaseActivity

/**
 * @author : 华清松
 * AbsDisposeActivity
 */
abstract class AbsDisposeActivity : BaseActivity(), IDisposable {
    override var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()
    val mJob = Dispatchers.Main + Job()

    override fun dispose() {
        mCompositeDisposable?.dispose()
    }

    override fun addDispose(disposable: Disposable) {
        mCompositeDisposable?.add(disposable)
    }

    override fun onDestroy() {
        dispose()
        mJob.cancel()
        super.onDestroy()
    }
}