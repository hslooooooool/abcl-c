package qsos.core.form.view.hodler

import android.view.View
import kotlinx.android.synthetic.main.form_item_time.view.*
import kotlinx.android.synthetic.main.form_normal_title.view.*
import qsos.core.form.db.entity.FormItem
import qsos.core.form.utils.DateUtils
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnItemListener

/**
 * @author : 华清松
 * 表单时间列表项视图
 */
class FormItemTimeHolder(
        itemView: View,
        private val itemClick: OnItemListener<Any?>
) : BaseHolder<FormItem>(itemView) {

    override fun bind(data: FormItem, position: Int) {

        itemView.form_item_title.text = data.title!!

        var time = ""
        if (data.formItemValue!!.values != null) {
            if (data.formItemValue!!.values!!.size == 1) {
                if (data.formItemValue!!.values!![0].time!!.timeStart > 0L) {
                    time = DateUtils.date(data.formItemValue!!.values!![0].time!!.timeStart, data.formItemValue!!.values!![0].limitType)
                }
            } else if (data.formItemValue!!.values!!.size == 2) {
                if (data.formItemValue!!.values!![0].time!!.timeStart > 0L && data.formItemValue!!.values!![1].time!!.timeStart > 0L) {
                    time = DateUtils.date(data.formItemValue!!.values!![0].time!!.timeStart, data.formItemValue!!.values!![0].limitType) +
                            "\t至\t" +
                            DateUtils.date(data.formItemValue!!.values!![1].time!!.timeStart, data.formItemValue!!.values!![1].limitType)

                }
            }
        }

        itemView.item_form_time.hint = "请设置"
        itemView.item_form_time.text = time

        itemView.form_item_title.setOnClickListener {
            itemClick.onClick(it, position, data)
        }
        itemView.item_form_time.setOnClickListener {
            itemClick.onClick(it, position, data)
        }
    }

}
