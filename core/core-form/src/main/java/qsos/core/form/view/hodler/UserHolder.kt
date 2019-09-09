package qsos.core.form.view.hodler

import android.view.View
import kotlinx.android.synthetic.main.form_item_user.view.*
import qsos.core.form.db.entity.FormUserEntity
import qsos.core.lib.utils.image.ImageLoaderUtils
import qsos.lib.base.base.holder.BaseHolder

/**
 * @author : 华清松
 * 用户 列表项布局
 */
class UserHolder(itemView: View) : BaseHolder<FormUserEntity>(itemView) {

    override fun setData(data: FormUserEntity, position: Int) {
        ImageLoaderUtils.display(itemView.context, itemView.iv_item_user, data.userAvatar)
        itemView.tv_item_user.text = data.userName
    }
}