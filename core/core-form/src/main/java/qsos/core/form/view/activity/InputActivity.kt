package qsos.core.form.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.form_input.*
import qsos.core.form.FormPath
import qsos.core.form.R
import qsos.core.form.data.FormModelIml
import qsos.core.form.data.FormRepository
import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.Value
import qsos.lib.base.utils.ToastUtils

/**
 * @author : 华清松
 * 表单输入页
 */
@Route(group = FormPath.FORM, path = FormPath.FORM_ITEM_INPUT)
class InputActivity : AbsFormActivity() {

    /**表单数据实现类*/
    private lateinit var formModelIml: FormModelIml

    @Autowired(name = "item_id")
    @JvmField
    var itemId: Long? = 0

    private var item: FormItem? = null
    private var itemValue: Value? = null

    private var limitMin = 0
    private var limitMax = 1000

    override val layoutId: Int = R.layout.form_input
    override val reload: Boolean = false

    override fun initData(savedInstanceState: Bundle?) {
        formModelIml = FormModelIml(FormRepository(mContext))
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {

        btn_form_input_btn.setOnClickListener {
            if (itemValue != null) {
                val content = et_form_input.text.toString()
                itemValue?.text?.content = content
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showToast(this, "你未输入内容")
                }
                if (item!!.require && content.length < limitMin || content.length > limitMax) {
                    ToastUtils.showToast(this, "字数不符合要求")
                }
                formModelIml.updateValue(itemValue!!)
            }
        }

        formModelIml.formRepo.updateValueStatus.observe(this, Observer {
            finish()
        })

        formModelIml.formRepo.dbFormItem.observe(this, Observer {
            item = it
            limitMin = item!!.formItemValue!!.limitMin ?: 0
            limitMax = item!!.formItemValue!!.limitMax ?: 0

            if (item!!.formItemValue != null
                    && item!!.formItemValue!!.values != null
                    && item!!.formItemValue!!.values!!.isNotEmpty()) {
                itemValue = item!!.formItemValue!!.values!![0]
            }

            if (TextUtils.isEmpty(itemValue?.text?.content)) {
                itemValue?.text?.content = ""
            }
            et_form_input.setText(itemValue?.text?.content)
            et_form_input.hint = item?.notice ?: "点击输入"

            if (limitMax > 0) {
                et_form_input.filters = arrayOf(InputFilter.LengthFilter(limitMax))
            }

            val limit = when {
                limitMax < 1 -> "字数不限"
                limitMax >= 1 && limitMin < 1 -> "字数限制：最多输入 $limitMax 字"
                else -> "字数限制：" + limitMin + "\t-\t" + limitMax + "字"
            }
            tv_form_input_hint.text = item?.notice + limit

            et_form_input.setSelection(itemValue?.text?.content?.length ?: 0)

            if (!it.editable) {
                btn_form_input_btn.visibility = View.INVISIBLE
                et_form_input.isEnabled = false
                tv_form_input_hint.visibility = View.INVISIBLE
            }
        })

        getData()

    }

    override fun getData() {
        if (itemId != null) {
            formModelIml.getFormItemByDB(itemId!!)
        } else {
            ToastUtils.showToast(this, "无法获取数据")
            finish()
        }
    }
}
