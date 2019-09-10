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
 * 表单时间列表项视图
 */
class ItemFormTimeHolder(
        itemView: View,
        private val itemClick: OnListItemClickListener
) : BaseHolder<FormItem>(itemView) {

    override fun setData(data: FormItem, position: Int) {

        itemView.item_form_key.text = data.title!!

        var time = ""
        if (data.formItemValue!!.values != null) {
            if (data.formItemValue!!.values!!.size == 1) {
                if (data.formItemValue!!.values!![0].time!!.timeStart > 0L) {
                    time = FormUtils.date(data.formItemValue!!.values!![0].time!!.timeStart, data.formItemValue!!.values!![0].limitType)
                }
            } else if (data.formItemValue!!.values!!.size == 2) {
                if (data.formItemValue!!.values!![0].time!!.timeStart > 0L && data.formItemValue!!.values!![1].time!!.timeStart > 0L) {
                    time = FormUtils.date(data.formItemValue!!.values!![0].time!!.timeStart, data.formItemValue!!.values!![0].limitType) +
                            "\t至\t" +
                            FormUtils.date(data.formItemValue!!.values!![1].time!!.timeStart, data.formItemValue!!.values!![1].limitType)

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
