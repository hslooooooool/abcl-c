package qsos.core.form.view.hodler

import android.view.View
import kotlinx.android.synthetic.main.form_item_check.view.*
import kotlinx.android.synthetic.main.form_normal_title.view.*
import qsos.core.form.db.entity.FormItem
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnItemListener

/**
 * @author : 华清松
 * 表单文本列表项视图
 */
class FormItemCheckHolder(
        itemView: View,
        private val itemClick: OnItemListener<Any?>
) : BaseHolder<FormItem>(itemView) {

    override fun bind(data: FormItem, position: Int) {
        itemView.form_item_title.text = data.title
        var text = ""
        when {
            data.formItemValue!!.values!!.size == 1 -> {
                text = data.formItemValue!!.value!!.check!!.ckName ?: ""
            }
            data.formItemValue!!.limitMax == 1 -> {
                for (v in data.formItemValue!!.values!!) {
                    if (v.check!!.ckChecked) {
                        text = v.check!!.ckName ?: ""
                        break
                    }
                }
            }
            else -> {
                data.formItemValue!!.values!!.forEach { v ->
                    if (v.check!!.ckChecked) {
                        text += v.check!!.ckName + ";"
                    }
                }
            }
        }
        itemView.form_item_check.hint = data.notice
        itemView.form_item_check.text = text
        itemView.form_item_title.setOnClickListener {
            itemClick.onClick(it, position, data)
        }
        itemView.form_item_check.setOnClickListener {
            itemClick.onClick(it, position, data)
        }

    }

}
