package qsos.core.form.view.hodler

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.form_item_user.view.*
import kotlinx.android.synthetic.main.form_item_users.view.*
import kotlinx.android.synthetic.main.form_normal_title.view.*
import qsos.core.form.R
import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.FormUserEntity
import qsos.core.lib.utils.image.ImageLoaderUtils
import qsos.lib.base.base.adapter.BaseNormalAdapter
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnListItemClickListener

/**
 * @author : 华清松
 * 表单用户列表项视图
 */
class ItemFormUserHolder(
        itemView: View,
        private val itemClick: OnListItemClickListener
) : BaseHolder<FormItem>(itemView) {

    @SuppressLint("SetTextI18n")
    override fun setData(data: FormItem, position: Int) {
        itemView.item_form_key.text = "${data.title}"

        if (data.formItemValue?.values != null) {
            itemView.tv_item_form_users_size.text = "${data.formItemValue!!.values?.size}\t人"
            itemView.rv_item_form_users.layoutManager = GridLayoutManager(itemView.context, 5)
            val users = arrayListOf<FormUserEntity>()
            data.formItemValue?.values!!.forEach {
                users.add(FormUserEntity(it.user!!.userName!!, "${it.user!!.userDesc}", it.user!!.userAvatar))
            }
            itemView.rv_item_form_users.adapter = BaseNormalAdapter(R.layout.form_item_user, users,
                    setHolder = { holder, user, _ ->
                        ImageLoaderUtils.display(holder.itemView.context, itemView.iv_item_user, user.userAvatar)
                        holder.itemView.tv_item_user.text = user.userName
                    })
        }

        /**监听*/
        itemView.item_form_key.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
        itemView.tv_item_form_users_size.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
    }

}
