package qsos.core.form.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.form_activity_main.*
import qsos.core.form.FormPath
import qsos.core.form.R
import qsos.core.form.data.FormModelIml
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
        override val layoutId: Int = R.layout.form_activity_main,
        override val reload: Boolean = true,
        override var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()
) : AbsFormActivity() {
    /**渲染表单项容器*/
    private lateinit var mAdapter: FormAdapter
    /**表单数据实现类*/
    private val mModel: FormModelIml = FormModelIml()
    private val mFormList = arrayListOf<FormItem>()
    private var mForm: FormEntity? = null
    /**待上传的表单项ID*/
    private var uploadFileFormItemId: Long = 0L
    private var mAddPosition: Int? = null

    override fun initData(savedInstanceState: Bundle?) {}

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
        ).subscribe {
            if (!it) {
                ToastUtils.showToastLong(this, "请前往设置开启权限方可使用表单功能")
                finish()
            }
        }

        form_main_rv.layoutManager = LinearLayoutManager(mContext)

        mAdapter = FormAdapter(mFormList)
        // 添加装饰类
        form_main_rv.addItemDecoration(FormItemDecoration())
        // 设置列表容器
        form_main_rv.adapter = mAdapter

        form_main_btn?.setOnClickListener {
            form_main_btn?.isClickable = false
            if (mForm != null) {
                val verify = FormVerifyUtils.verify(mForm!!)
                if (verify.pass) {
                    val intent = Intent()
                    intent.putExtra(FormPath.FORM_ID, mForm!!.id)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    ToastUtils.showToast(this, verify.message)
                    form_main_btn?.isClickable = true
                }
            } else {
                ToastUtils.showToastLong(this, "发生错误，数据已丢失！")
            }
        }

        // 表单内操作文件监听
        mAdapter.setOnFileListener(object : FormAdapter.OnFileListener {

            override fun getFile(type: String, position: Int) {
                mAddPosition = position
                uploadFileFormItemId = mFormList[position].id!!
                when (type) {
                    // TODO
                }
            }
        })

    }

    override fun getData() {
        mCompositeDisposable?.add(mModel.getForm(formId!!).subscribe(
                {
                    mForm = it

                    form_main_btn?.text = mForm!!.submitName ?: "提交"

                    val formItemList = arrayListOf<FormItem>()
                    for (item in mForm!!.formItems!!) {
                        if (item.visible) formItemList.add(item)
                    }
                    mFormList.clear()
                    mFormList.addAll(formItemList)
                    mAdapter.notifyDataSetChanged()
                },
                {
                    it.printStackTrace()
                    ToastUtils.showToastLong(this, "数据错误 ${it.message}")
                }
        ))
    }

    override fun onBackPressed() {
        if (mForm!!.editable) {
            // 编辑模式，删除后退出
            mCompositeDisposable?.add(mModel.deleteForm(mForm!!).subscribe(
                    {
                        finish()
                    },
                    {
                        it.printStackTrace()
                        ToastUtils.showToast(this, "数据错误 ${it.message}")
                        finish()
                    }
            ))
        } else {
            // 预览模式，直接退出
            finish()
        }
    }

    override fun onDestroy() {
        dispose()
        super.onDestroy()
    }

    override fun dispose() {
        mCompositeDisposable?.dispose()
    }

}
