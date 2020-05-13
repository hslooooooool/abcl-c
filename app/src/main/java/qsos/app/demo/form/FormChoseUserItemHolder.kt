package qsos.app.demo.form

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.app_form_users_item.view.*
import qsos.app.demo.R
import qsos.core.lib.utils.image.ImageLoaderUtils
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnItemListener

/**
 * @author : 华清松
 * 表单用户列表项视图
 */
class FormChoseUserItemHolder(
        itemView: View,
        private val itemClick: OnItemListener<Any?>
) : BaseHolder<UserEntity>(itemView) {

    private val context: Context = itemView.context

    override fun bind(data: UserEntity, position: Int) {

        itemView.form_user_name.text = data.userName
        itemView.form_user_desc.text = data.userDesc

        itemView.form_user_ll.isEnabled = !data.limitEdit
        val color = if (data.checked) {
            ContextCompat.getColor(itemView.context, R.color.gray_low)
        } else {
            ContextCompat.getColor(itemView.context, R.color.white)
        }
        itemView.form_user_ll.setBackgroundColor(color)

        ImageLoaderUtils.display(context, itemView.form_user_head, data.userAvatar)

        if (!data.limitEdit) {
            itemView.form_user_ll.setOnClickListener {
                itemClick.onClick(it, position, data)
            }
        }
    }

}
