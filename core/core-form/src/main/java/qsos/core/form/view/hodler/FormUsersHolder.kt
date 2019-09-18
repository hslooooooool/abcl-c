package qsos.core.form.view.hodler

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.form_user.view.*
import qsos.core.form.db.entity.FormUserEntity
import qsos.core.lib.utils.image.ImageLoaderUtils
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnListItemClickListener

/**
 * @author : 华清松
 * 表单用户列表项视图
 */
class FormUsersHolder(
        itemView: View,
        private val itemClick: OnListItemClickListener
) : BaseHolder<FormUserEntity>(itemView) {

    private val context: Context = itemView.context

    override fun setData(data: FormUserEntity, position: Int) {

        itemView.form_user_name.text = data.userName
        itemView.form_user_phone.text = data.userPhone

        itemView.form_user_cb.visibility = if (data.userCb) View.VISIBLE else View.INVISIBLE
        itemView.form_user_cb.setImageResource(if (data.userLimit) android.R.drawable.ic_menu_compass else android.R.drawable.ic_menu_delete)

        ImageLoaderUtils.display(context, itemView.form_user_head, data.userAvatar)

        if (!data.userLimit) {
            itemView.form_user_ll.setOnClickListener {
                itemClick.onItemClick(it, position, data)
            }
        }
    }

}