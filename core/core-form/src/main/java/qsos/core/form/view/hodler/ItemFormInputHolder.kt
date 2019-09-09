package qsos.core.form.view.hodler

import android.view.View
import kotlinx.android.synthetic.main.form_item_input.view.*
import kotlinx.android.synthetic.main.form_normal_title.view.*
import qsos.core.form.db.entity.FormItem
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnListItemClickListener

/**
 * @author : 华清松
 * 表单文本列表项视图
 */
class ItemFormInputHolder(
        itemView: View,
        private val itemClick: OnListItemClickListener
) : BaseHolder<FormItem>(itemView) {

    override fun setData(data: FormItem, position: Int) {

        itemView.item_form_input.hint = data.notice
        itemView.item_form_key.text = data.title

        if (data.formItemValue!!.values != null && data.formItemValue!!.values!!.isNotEmpty()) {
            itemView.item_form_input.text = data.formItemValue!!.values!![0].text?.content
        }

        itemView.item_form_input.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
        itemView.item_form_key.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
    }

}
