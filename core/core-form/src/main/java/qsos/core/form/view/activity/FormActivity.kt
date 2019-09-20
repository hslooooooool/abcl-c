package qsos.core.form.view.activity

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import qsos.core.form.FormPath
import qsos.core.form.R
import qsos.core.form.data.FormModelIml
import qsos.core.form.db.entity.FormEntity
import qsos.core.form.dbComplete
import qsos.core.form.view.fragment.FormFragment
import qsos.lib.base.base.activity.BaseActivity
import qsos.lib.base.utils.ToastUtils

/**
 * @author : 华清松
 * 表单界面
 */
@Route(group = FormPath.FORM, path = FormPath.MAIN)
class FormActivity(
        @JvmField @Autowired(name = FormPath.FORM_ID) var formId: Long? = -1L,
        override val layoutId: Int = R.layout.form_activity_demo,
        override val reload: Boolean = true
) : BaseActivity() {
    private lateinit var mFormFragment: FormFragment

    override fun initData(savedInstanceState: Bundle?) {}

    override fun initView() {
        if (formId == null || formId == -1L) {
            finish()
            return
        }
        mFormFragment = FormFragment(formId)
        supportFragmentManager.beginTransaction().add(R.id.form_demo_frg, FormFragment(formId), "$formId").commit()
    }

    override fun getData() {}

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
}
