package qsos.core.form.view.hodler

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import kotlinx.android.synthetic.main.form_item_file_item.view.*
import qsos.core.form.R
import qsos.core.form.db.entity.Value
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnListItemClickListener

/**
 * @author : 华清松
 * 图片文件布局
 */
class FormItemFileItemHolder(
        itemView: View,
        private val itemClick: OnListItemClickListener
) : BaseHolder<Value>(itemView) {

    override fun setData(data: Value, position: Int) {
        //todo 显示具体缩略图
        val drawable = when (data.file!!.getFileTypeByMime()) {
            "IMAGE" -> {
                AppCompatResources.getDrawable(itemView.context, R.drawable.take_image)
            }
            "VIDEO" -> {
                AppCompatResources.getDrawable(itemView.context, R.drawable.take_video)
            }
            "AUDIO" -> {
                AppCompatResources.getDrawable(itemView.context, R.drawable.take_audio)
            }
            "FILE" -> {
                AppCompatResources.getDrawable(itemView.context, R.drawable.take_file)
            }
            else -> {
                AppCompatResources.getDrawable(itemView.context, R.drawable.take_file)
            }
        }
        itemView.iv_item_form_file_icon.setImageDrawable(drawable)
        itemView.tv_item_form_file_name.text = data.file!!.fileName

        itemView.iv_item_form_file_icon.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
        itemView.iv_item_form_file_delete.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
    }
}
