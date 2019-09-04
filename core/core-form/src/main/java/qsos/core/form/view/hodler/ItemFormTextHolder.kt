package qsos.core.form.view.hodler

import android.view.View
import kotlinx.android.synthetic.main.form_item_text.view.*
import kotlinx.android.synthetic.main.form_normal_title.view.*
import qsos.core.form.db.entity.FormItem
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnListItemClickListener

/**
 * @author : 华清松
 * @description : 表单输入列表项视图
 */
class ItemFormTextHolder(
        itemView: View,
        private val itemClick: OnListItemClickListener
) : BaseHolder<FormItem>(itemView) {

    override fun setData(data: FormItem, position: Int) {

        /*绑定数据*/
        itemView.item_form_key.text = data.form_item_key
        if (data.form_item_value!!.values != null && data.form_item_value!!.values!!.isNotEmpty()) {
            itemView.item_form_text.text = data.form_item_value!!.values!![0].input_value
        }

        /*监听*/
        itemView.item_form_key.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
        itemView.item_form_text.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }

    }

}
