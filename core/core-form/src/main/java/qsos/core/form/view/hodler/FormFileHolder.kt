package qsos.core.form.view.hodler

import android.view.View
import kotlinx.android.synthetic.main.form_item_file_item.view.*
import qsos.core.form.R
import qsos.core.form.db.entity.Value
import qsos.core.lib.utils.image.ImageLoaderUtils
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnListItemClickListener

/**
 * @author : 华清松
 * @description : 图片文件布局
 */
class FormFileHolder(
        itemView: View,
        private val itemClick: OnListItemClickListener
) : BaseHolder<Value>(itemView) {

    override fun setData(data: Value, position: Int) {
        when (data.file_type) {
            "IMAGE" -> {
                ImageLoaderUtils.display(itemView.context, itemView.iv_item_form_file_icon, data.file_url)
            }
            "VIDEO" -> {
                ImageLoaderUtils.display(itemView.context, itemView.iv_item_form_file_icon, R.drawable.take_video)
            }
            "AUDIO" -> {
                ImageLoaderUtils.display(itemView.context, itemView.iv_item_form_file_icon, R.drawable.take_audio)
            }
            "FILE" -> {
                ImageLoaderUtils.display(itemView.context, itemView.iv_item_form_file_icon, R.drawable.take_file)
            }
        }

        itemView.tv_item_form_file_name.text = data.file_name

        itemView.iv_item_form_file_icon.setOnClickListener {
            itemClick.onItemClick(it, position, 1)
        }
        itemView.iv_item_form_file_delete.setOnClickListener {
            itemClick.onItemClick(it, position, 2)
        }
    }
}
