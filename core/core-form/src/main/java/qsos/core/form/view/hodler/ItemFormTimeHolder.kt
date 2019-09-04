package qsos.core.form.view.hodler

import android.view.View
import kotlinx.android.synthetic.main.form_item_time.view.*
import kotlinx.android.synthetic.main.form_normal_title.view.*
import qsos.core.form.db.entity.FormItem
import qsos.core.form.utils.FormUtils
import qsos.lib.base.base.holder.BaseHolder

import qsos.lib.base.callback.OnListItemClickListener


/**
 * @author : 华清松
 * @description : 表单时间列表项视图
 */
class ItemFormTimeHolder(
        itemView: View,
        private val itemClick: OnListItemClickListener
) : BaseHolder<FormItem>(itemView) {

    override fun setData(data: FormItem, position: Int) {

        itemView.item_form_key.text = data.form_item_key!!

        var time = ""
        if (data.form_item_value!!.values != null) {
            if (data.form_item_value!!.values!!.size == 1) {
                if (data.form_item_value!!.values!![0].time > 0L) {
                    time = FormUtils.date(data.form_item_value!!.values!![0].time, data.form_item_value!!.values!![0].limit_type)
                }
            } else if (data.form_item_value!!.values!!.size == 2) {
                if (data.form_item_value!!.values!![0].time > 0L && data.form_item_value!!.values!![1].time > 0L) {
                    time = FormUtils.date(data.form_item_value!!.values!![0].time, data.form_item_value!!.values!![0].limit_type) +
                            "\t至\t" +
                            FormUtils.date(data.form_item_value!!.values!![1].time, data.form_item_value!!.values!![1].limit_type)

                }
            }
        }

        itemView.item_form_time.hint = "请设置"
        itemView.item_form_time.text = time

        /*监听*/
        itemView.item_form_time.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
    }

}
