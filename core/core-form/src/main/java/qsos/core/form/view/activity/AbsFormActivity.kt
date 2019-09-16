package qsos.core.form.view.activity

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import qsos.core.lib.base.IDisposable
import qsos.lib.base.base.activity.BaseActivity

/**
 * @author : 华清松
 * 表单基础 Activity
 */
abstract class AbsFormActivity : BaseActivity(), IDisposable {
    override var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()

    override fun dispose() {
        mCompositeDisposable?.dispose()
    }

    override fun addDispose(disposable: Disposable) {
        mCompositeDisposable?.add(disposable)
    }

    override fun onDestroy() {
        dispose()
        super.onDestroy()
    }
}