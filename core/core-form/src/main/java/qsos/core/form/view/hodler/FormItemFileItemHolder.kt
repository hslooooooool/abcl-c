package qsos.core.form.view.hodler

import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import kotlinx.android.synthetic.main.form_item_file_item.view.*
import qsos.core.form.R
import qsos.core.form.db.entity.FormValueOfFile
import qsos.core.form.db.entity.Value
import qsos.core.lib.utils.image.ImageLoaderUtils
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
        ImageLoaderUtils.display(
                itemView.context, itemView.iv_item_form_file_icon, data.file!!.fileCover,
                getPlaceDrawable(FormValueOfFile.getFileTypeByMime(data.file!!.fileType))
        )
        itemView.tv_item_form_file_name.text = data.file!!.fileName

        itemView.iv_item_form_file_icon.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
        itemView.iv_item_form_file_delete.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
    }

    private fun getPlaceDrawable(type: String?): Drawable? {
        return AppCompatResources.getDrawable(itemView.context, when (type) {
            "IMAGE" -> R.drawable.take_image
            "VIDEO" -> R.drawable.take_video
            "AUDIO" -> R.drawable.take_audio
            "FILE" -> R.drawable.take_file
            else -> R.drawable.take_file
        })
    }
}
