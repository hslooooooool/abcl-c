package qsos.core.form.view.activity

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import qsos.core.lib.base.IDisposable
import qsos.lib.base.base.activity.BaseActivity

/**
 * @author : 华清松
 * 表单基础 Activity
 */
abstract class AbsFormActivity : BaseActivity(), IDisposable {
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