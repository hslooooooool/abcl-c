package qsos.core.form.view.hodler

import android.view.View
import kotlinx.android.synthetic.main.form_item_text.view.*
import kotlinx.android.synthetic.main.form_normal_title.view.*
import qsos.core.form.db.entity.FormItem
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnItemListener

/**
 * @author : 华清松
 * 表单文本列表项视图
 */
class FormItemTextHolder(
        itemView: View,
        private val itemClick: OnItemListener<Any?>
) : BaseHolder<FormItem>(itemView) {

    override fun bind(data: FormItem, position: Int) {

        itemView.form_item_title.text = data.title
        if (data.formItemValue!!.values != null && data.formItemValue!!.values!!.isNotEmpty()) {
            itemView.item_form_text.text = data.formItemValue!!.values!![0].text?.content
        }

        itemView.form_item_title.setOnClickListener {
            itemClick.onClick(it, position, data)
        }
        itemView.item_form_text.setOnClickListener {
            itemClick.onClick(it, position, data)
        }

    }

}
