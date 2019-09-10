package qsos.core.form.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.form_activity_main.*
import qsos.core.form.FormPath
import qsos.core.form.R
import qsos.core.form.data.FormModelIml
import qsos.core.form.data.FormRepository
import qsos.core.form.db.entity.FormEntity
import qsos.core.form.db.entity.FormItem
import qsos.core.form.utils.FormVerifyUtils
import qsos.core.form.view.adapter.FormAdapter
import qsos.core.form.view.other.FormItemDecoration
import qsos.lib.base.utils.ToastUtils

/**
 * @author : 华清松
 * 表单界面
 */
@Route(group = FormPath.FORM, path = FormPath.MAIN)
class FormActivity(
        @JvmField @Autowired(name = FormPath.FORM_ID) var formId: Long? = -1L,
        @JvmField @Autowired(name = FormPath.FORM_EDIT) var formEdit: Boolean = true,

        override val layoutId: Int = R.layout.form_activity_main,
        override val reload: Boolean = true
) : AbsFormActivity() {
    /**渲染表单项容器*/
    private lateinit var mAdapter: FormAdapter
    /**表单数据实现类*/
    private lateinit var mModel: FormModelIml
    private val mFormList = arrayListOf<FormItem>()
    private var mForm: FormEntity? = null
    /**待上传的表单项ID*/
    private var uploadFileFormItemId: Long = 0L
    private var mAddPosition: Int? = null

    override fun initData(savedInstanceState: Bundle?) {
        mModel = FormModelIml(FormRepository(mContext))
    }

    @SuppressLint("CheckResult")
    override fun initView() {

        if (formId == null || formId == -1L) {
            ToastUtils.showToast(this, "没有此表单")
            finish()
            return
        }

        RxPermissions(this).request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE
        )!!.subscribe {
            if (!it) {
                ToastUtils.showToast(this, "请前往设置开启权限")
                finish()
            }
        }

        form_main_rv.layoutManager = LinearLayoutManager(mContext)

        mAdapter = FormAdapter(mFormList)
        // 添加装饰类
        form_main_rv.addItemDecoration(FormItemDecoration())
        // 设置列表容器
        form_main_rv.adapter = mAdapter

        form_main_btn?.visibility = if (formEdit) View.VISIBLE else View.GONE

        form_main_btn?.setOnClickListener {
            form_main_btn?.isClickable = false
            form_main_btn?.background = ContextCompat.getDrawable(mContext, android.R.color.darker_gray)
            if (mForm != null) {
                mModel.formRepo.postFormStatus.postValue(FormVerifyUtils.verify(mForm!!))
            } else {
                ToastUtils.showToast(this, "提交失败！数据已丢失。")
            }
        }

        mModel.formRepo.dbFormEntity.observe(this, Observer {

            mForm = it

            if (it == null) {
                ToastUtils.showToast(this, "获取表单数据失败")
            } else {
                form_main_btn?.text = mForm!!.submitName ?: "提交"

                mFormList.clear()
                val formItemList = arrayListOf<FormItem>()
                for (item in mForm!!.formItem!!) {
                    if (item.visible) formItemList.add(item)
                }
                mFormList.addAll(formItemList)
                mAdapter.notifyDataSetChanged()
            }
        })

        mModel.formRepo.postFormStatus.observe(this, Observer {
            if (it.pass) {
                val intent = Intent()
                intent.putExtra(FormPath.FORM_ID, mForm!!.id ?: -1L)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                ToastUtils.showToast(this, it.message)
                form_main_btn?.isClickable = true
                form_main_btn?.background = ContextCompat.getDrawable(mContext, android.R.color.darker_gray)
            }
        })

        mModel.formRepo.dbDeleteForm.observe(this, Observer {
            finish()
        })

        // 表单内操作文件监听
        mAdapter.setOnFileListener(object : FormAdapter.OnFileListener {
            @SuppressLint("SetTextI18n")
            override fun getFile(type: String, position: Int) {
                mAddPosition = position
                uploadFileFormItemId = mFormList[position].id!!
                when (type) {
                    // TODO
                }
            }
        })

        mModel.formRepo.addValueToFormItem.observe(this, Observer {
            if (it == null) {
                ToastUtils.showToast(this, "添加失败")
            } else {
                mFormList[mAddPosition!!].formItemValue!!.values!!.add(it)
                mAdapter.notifyItemChanged(mAddPosition!!)
            }
        })
    }

    override fun getData() {
        mModel.getFormByDB(formId!!)
    }

    override fun onBackPressed() {
        if (formEdit) {
            finish()
        } else {
            mModel.deleteForm(mForm!!)
        }
    }

}
