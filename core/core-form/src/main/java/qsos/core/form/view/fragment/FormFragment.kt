package qsos.core.form.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.form_main.*
import kotlinx.coroutines.CoroutineScope
import qsos.core.form.FormPath
import qsos.core.form.R
import qsos.core.form.data.FormModelIml
import qsos.core.form.data.IFormModel
import qsos.core.form.db
import qsos.core.form.db.entity.FormEntity
import qsos.core.form.db.entity.FormItem
import qsos.core.form.utils.FormVerifyUtils
import qsos.core.form.view.adapter.FormAdapter
import qsos.core.form.view.other.FormItemDecoration
import qsos.lib.base.utils.ToastUtils

/**
 * @author : 华清松
 * 表单界面Fragment
 */
@SuppressLint("CheckResult")
class FormFragment : AbsFormFragment(R.layout.form_main, false) {

    private var formId: Long? = -1L

    /**表单列表项容器*/
    private lateinit var mAdapter: FormAdapter

    /**表单列表项*/
    private val mFormList = arrayListOf<FormItem>()

    /**表单对象*/
    private var mForm: FormEntity? = null

    /**表单数据实现类*/
    private val mModel: IFormModel = FormModelIml()

    override fun initData(savedInstanceState: Bundle?) {
        formId = arguments?.getLong("formId")
    }

    override fun initView(view: View) {
        if (formId == null || formId == -1L) {
            ToastUtils.showToast(mContext, "没有此表单")
            activity?.finish()
            return
        }

        form_main_rv.layoutManager = LinearLayoutManager(mContext)
        mAdapter = FormAdapter(mFormList, mJob)
        form_main_rv.addItemDecoration(FormItemDecoration())
        form_main_rv.adapter = mAdapter

        form_main_btn?.setOnClickListener {
            form_main_btn?.isClickable = false
            mForm?.let {
                val verify = FormVerifyUtils.verify(mForm!!)
                if (verify.pass) {
                    val intent = Intent()
                    intent.putExtra(FormPath.FORM_ID, mForm!!.id)
                    activity?.setResult(Activity.RESULT_OK, intent)
                    activity?.finish()
                } else {
                    ToastUtils.showToast(mContext, verify.message)
                    form_main_btn?.isClickable = true
                }
            }
        }

        getData()
    }

    override fun getData(loadMore: Boolean) {
        CoroutineScope(mJob).db<FormEntity> {
            db = { mModel.getForm(formId!!) }
            onSuccess = {
                it?.let {
                    mForm = it
                    form_main_btn.visibility = if (it.editable) View.VISIBLE else View.GONE
                    form_main_btn.text = mForm!!.submitName ?: "提交"
                    val formItemList = arrayListOf<FormItem>()
                    for (item in mForm!!.formItems!!) {
                        if (item.visible) formItemList.add(item)
                    }
                    mFormList.clear()
                    mFormList.addAll(formItemList)
                    mAdapter.notifyDataSetChanged()
                }
            }
            onFail = {
                it.printStackTrace()
                ToastUtils.showToastLong(mContext, "数据错误 ${it.message}")
            }
        }
    }

}