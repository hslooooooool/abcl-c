package qsos.core.form.view.hodler

import android.annotation.SuppressLint
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.form_item_input.view.*
import kotlinx.android.synthetic.main.form_normal_title.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import qsos.core.form.db
import qsos.core.form.db.FormDatabase
import qsos.core.form.db.entity.FormItem
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnListItemClickListener
import timber.log.Timber

/**
 * @author : 华清松
 * 表单文本列表项视图
 */
class ItemFormInputHolder(
        itemView: View,
        private val itemClick: OnListItemClickListener
) : BaseHolder<FormItem>(itemView) {

    override fun setData(data: FormItem, position: Int) {

        itemView.item_form_title.text = data.title

        if (!data.formItemValue!!.values!!.isNullOrEmpty()) {
            itemView.item_form_input.setText(data.formItemValue!!.values!![0].text!!.content)
        }

        itemView.item_form_input.isEnabled = data.editable
        itemView.item_form_input.hint = data.notice ?: "点击输入"

        if (data.formItemValue!!.limitMax != null && data.formItemValue!!.limitMax!! > 0) {
            itemView.item_form_input.filters = arrayOf(InputFilter.LengthFilter(data.formItemValue!!.limitMax!!))
        }

        itemView.item_form_input.addTextChangedListener(object : TextWatcher {

            @SuppressLint("BinaryOperationInTimber")
            override fun afterTextChanged(p0: Editable) {
                val content = itemView.item_form_input.text.toString()
                data.formItemValue!!.values!![0].text!!.content = content
                CoroutineScope(Dispatchers.Main + Job()).db<Long> {
                    db = { FormDatabase.getInstance().formItemValueDao.insert(data.formItemValue!!.values!![0]) }
                    onSuccess = {
                        Timber.tag("数据库插入").i("更新输入值$it")
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })

        itemView.item_form_title.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
    }

}
