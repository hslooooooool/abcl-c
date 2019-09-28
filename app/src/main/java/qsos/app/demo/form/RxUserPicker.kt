package qsos.app.demo.form

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.alibaba.android.arouter.launcher.ARouter
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import qsos.app.demo.AppPath
import qsos.core.form.FormPath
import qsos.core.lib.base.IDisposable

/**
 * @author : 华清松
 * 用户获取案例
 */
class RxUserPicker : Fragment(), IDisposable {
    /**观察是否绑定Activity*/
    private lateinit var attachedSubject: PublishSubject<Boolean>
    /**选择监听*/
    private lateinit var publishSubject: PublishSubject<Long>
    /**表单项ID*/
    private var mFormItemId: Long = -1L

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

    fun takeUser(formItemId: Long): Observable<Long> {
        this.mFormItemId = formItemId
        initSubjects()
        if (!isAdded) {
            attachedSubject.subscribe {
                startPick()
            }.also {
                addDispose(it)
            }
        } else {
            startPick()
        }
        return publishSubject
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 保留Fragment
        retainInstance = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (::attachedSubject.isInitialized.not() or
                ::publishSubject.isInitialized.not()) {
            initSubjects()
        }
        attachedSubject.onNext(true)
        attachedSubject.onComplete()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FormPath.FORM_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            publishSubject.onNext(mFormItemId)
            publishSubject.onComplete()
        }
    }

    private fun initSubjects() {
        publishSubject = PublishSubject.create()
        attachedSubject = PublishSubject.create()
    }

    private fun startPick() {
        ARouter.getInstance().build(AppPath.FORM_ITEM_USERS)
                .withLong(AppPath.FORM_ITEM_ID, mFormItemId)
                .navigation(activity, FormPath.FORM_REQUEST_CODE)
    }

    companion object {

        private val TAG = RxUserPicker::class.java.name

        /**获取RxUserPicker实例*/
        fun with(fm: FragmentManager): RxUserPicker {
            var rxUserPickerFragment = fm.findFragmentByTag(TAG) as RxUserPicker?
            if (rxUserPickerFragment == null) {
                rxUserPickerFragment = RxUserPicker()
                fm.beginTransaction().add(rxUserPickerFragment, TAG).commit()
            }
            return rxUserPickerFragment
        }
    }

}
