package qsos.core.form.view.fragment

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import qsos.core.lib.base.IDisposable
import qsos.lib.base.base.fragment.BaseFragment

/**
 * @author : 华清松
 * 表单界面Fragment
 */
abstract class AbsFormFragment : BaseFragment(), IDisposable {
    override val isOrientation: Boolean = true
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