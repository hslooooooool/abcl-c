package qsos.core.form.view.hodler

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.form_item_user.view.*
import kotlinx.android.synthetic.main.form_item_user_item.view.*
import kotlinx.android.synthetic.main.form_normal_title.view.*
import kotlinx.coroutines.CoroutineScope
import qsos.core.form.R
import qsos.core.form.db.FormDatabase
import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.Value
import qsos.core.form.dbComplete
import qsos.core.form.utils.FormConfigHelper
import qsos.core.lib.utils.image.ImageLoaderUtils
import qsos.lib.base.base.adapter.BaseNormalAdapter
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnListItemClickListener
import qsos.lib.base.utils.ToastUtils
import kotlin.coroutines.CoroutineContext

/**
 * @author : 华清松
 * 表单用户列表项视图
 */
@SuppressLint("SetTextI18n")
class FormItemUserHolder(
        itemView: View,
        private val mJob: CoroutineContext,
        private val itemClick: OnListItemClickListener
) : BaseHolder<FormItem>(itemView) {

    override fun setData(data: FormItem, position: Int) {
        val values: ArrayList<Value> = data.formItemValue?.values!!

        itemView.form_item_title.text = "${data.title}"
        itemView.item_form_users_size.text = "${data.formItemValue!!.values!!.size}\t人"

        if (itemView.item_form_users_rv.layoutManager == null) {
            itemView.item_form_users_rv.layoutManager = GridLayoutManager(itemView.context, 4)
            itemView.item_form_users_rv.adapter = BaseNormalAdapter(R.layout.form_item_user_item, values,
                    setHolder = { holder, value, p ->
                        val user = value.user!!
                        ImageLoaderUtils.display(holder.itemView.context, holder.itemView.item_form_user_icon, user.userAvatar)
                        holder.itemView.item_form_user_name.text = user.userName
                        holder.itemView.item_form_user_delete.visibility = if (!value.limitEdit) View.VISIBLE else View.INVISIBLE
                        holder.itemView.item_form_user_delete.setOnClickListener {
                            CoroutineScope(mJob).dbComplete {
                                db = { FormDatabase.getInstance().formItemValueDao.delete(data.formItemValue!!.values!![p]) }
                                onSuccess = {
                                    data.formItemValue!!.values!!.removeAt(p)
                                    itemView.item_form_users_size.text = "${data.formItemValue!!.values!!.size}\t人"
                                    itemView.item_form_users_rv.adapter?.notifyDataSetChanged()
                                }
                                onFail = {
                                    ToastUtils.showToastLong(holder.itemView.context, "删除失败 ${it.message}")
                                }
                            }
                        }
                        holder.itemView.item_form_user_icon.setOnClickListener {
                            FormConfigHelper.previewUser(it.context, p, values.map { it.user!! })
                        }
                    })
        } else {
            itemView.item_form_users_rv.adapter!!.notifyDataSetChanged()
        }

        itemView.form_item_title.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
        itemView.item_form_users_size.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
    }

}
