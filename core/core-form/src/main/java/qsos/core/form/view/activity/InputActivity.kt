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
 * @description : 表单输入页
 */
@Route(group = FormPath.FORM, path = FormPath.ITEM_INPUT)
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
                itemValue?.input_value = content
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showToast(this, "你未输入内容")
                }
                if (item!!.form_item_required && content.length < limitMin || content.length > limitMax) {
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
            limitMin = item!!.form_item_value!!.limit_min ?: 0
            limitMax = item!!.form_item_value!!.limit_max ?: 0

            if (item!!.form_item_value != null
                    && item!!.form_item_value!!.values != null
                    && item!!.form_item_value!!.values!!.isNotEmpty()) {
                itemValue = item!!.form_item_value!!.values!![0]
            }

            if (TextUtils.isEmpty(itemValue?.input_value)) {
                itemValue?.input_value = ""
            }
            et_form_input.setText(itemValue?.input_value)
            et_form_input.hint = item?.form_item_hint ?: "点击输入"

            if (limitMax > 0) {
                et_form_input.filters = arrayOf(InputFilter.LengthFilter(limitMax))
            }

            val limit = when {
                limitMax < 1 -> "字数不限"
                limitMax >= 1 && limitMin < 1 -> "字数限制：最多输入 $limitMax 字"
                else -> "字数限制：" + limitMin + "\t-\t" + limitMax + "字"
            }
            tv_form_input_hint.text = item?.form_item_hint + limit

            et_form_input.setSelection(itemValue?.input_value?.length ?: 0)

            if (it.form_item_status == 0) {
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
