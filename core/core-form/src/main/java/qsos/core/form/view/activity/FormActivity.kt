package qsos.core.form.view.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import qsos.core.form.FormPath
import qsos.core.form.R
import qsos.core.form.data.FormModelIml
import qsos.core.form.db.entity.FormEntity
import qsos.core.form.dbComplete
import qsos.core.form.view.fragment.FormFragment
import qsos.core.lib.base.AbsDisposeActivity
import qsos.lib.base.utils.ToastUtils

/**
 * @author : 华清松
 * 表单界面
 */
@Route(group = FormPath.FORM, path = FormPath.MAIN)
class FormActivity(
        @JvmField @Autowired(name = FormPath.FORM_ID) var formId: Long? = -1L
) : AbsDisposeActivity(R.layout.form_activity_demo, true) {

    private lateinit var mFormFragment: FormFragment

    override fun initData(savedInstanceState: Bundle?) {}

    override fun initView() {
        RxPermissions(this).request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
        ).subscribe({
            if (it) {
                if (formId == null || formId == -1L) {
                    finish()
                }
                mFormFragment = FormFragment()
                val mBundle = Bundle()
                mBundle.putLong("formId", formId!!)
                mFormFragment.arguments = mBundle
                supportFragmentManager.beginTransaction().add(R.id.form_demo_frg, mFormFragment, "$formId").commit()
            } else {
                ToastUtils.showToastLong(mContext, "权限开启失败，无法使用此功能")
            }
        }, {
            it.printStackTrace()
        }).also {
            addDispose(it)
        }
    }

    override fun getData(loadMore: Boolean) {}

    override fun onBackPressed() {
        CoroutineScope(Dispatchers.Main).dbComplete {
            db = {
                FormModelIml().deleteForm(FormEntity(id = formId))
            }
            onSuccess = {
                super.onBackPressed()
            }
            onFail = {
                it.printStackTrace()
                ToastUtils.showToast(mContext, "数据错误 ${it.message}")
                super.onBackPressed()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val allFragments = supportFragmentManager.fragments
        for (fragment in allFragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}
