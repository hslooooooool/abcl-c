package qsos.core.form.view.hodler

import android.view.View
import kotlinx.android.synthetic.main.form_item_location.view.*
import kotlinx.android.synthetic.main.form_normal_title.view.*
import qsos.core.form.db.entity.FormItem
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnListItemClickListener

/**
 * @author : 华清松
 * 表单位置列表项视图
 */
class FormItemLocationHolder(
        itemView: View,
        private val itemClick: OnListItemClickListener
) : BaseHolder<FormItem>(itemView) {

    override fun setData(data: FormItem, position: Int) {

        itemView.form_item_title.text = data.title
        itemView.item_form_location.text = data.formItemValue!!.value?.location?.locName ?: ""

        itemView.form_item_title.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
        itemView.item_form_location.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }

    }

}
