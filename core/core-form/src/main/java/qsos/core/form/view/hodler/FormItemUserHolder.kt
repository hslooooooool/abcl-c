package qsos.core.form.view.hodler

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.form_item_user.view.*
import kotlinx.android.synthetic.main.form_item_user_item.view.*
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
@SuppressLint("SetTextI18n")
class FormItemUserHolder(
        itemView: View,
        private val itemClick: OnListItemClickListener
) : BaseHolder<FormItem>(itemView) {

    override fun setData(data: FormItem, position: Int) {
        itemView.form_item_title.text = "${data.title}"
        itemView.item_form_users_size.text = "${data.formItemValue!!.values!!.size}\t人"
        itemView.item_form_users_rv.layoutManager = GridLayoutManager(itemView.context, 5)
        val users: ArrayList<FormUserEntity> = data.formItemValue?.values!!.map { FormUserEntity().transValueToThis(it) } as ArrayList<FormUserEntity>
        itemView.item_form_users_rv.adapter = BaseNormalAdapter(R.layout.form_item_user_item, users,
                setHolder = { holder, user, _ ->
                    ImageLoaderUtils.display(holder.itemView.context, itemView.iv_item_user, user.userAvatar)
                    holder.itemView.tv_item_user.text = user.userName
                })

        itemView.form_item_title.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
        itemView.item_form_users_size.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
    }

}
