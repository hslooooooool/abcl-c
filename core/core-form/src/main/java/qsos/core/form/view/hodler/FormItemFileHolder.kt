package qsos.core.form.view.hodler

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.form_item_file.view.*
import kotlinx.android.synthetic.main.form_normal_title.view.*
import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.FormValueOfFile
import qsos.core.form.view.adapter.FormFileAdapter
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnListItemClickListener
import kotlin.coroutines.CoroutineContext

/**
 * @author : 华清松
 * 文件表单项
 */
class FormItemFileHolder(
        itemView: View,
        private val mJob: CoroutineContext,
        private val itemClick: OnListItemClickListener
) : BaseHolder<FormItem>(itemView) {

    override fun setData(data: FormItem, position: Int) {
        itemView.form_item_title.text = data.title
        itemView.rv_item_form_files.layoutManager = GridLayoutManager(itemView.context, 3)
        itemView.rv_item_form_files.adapter = FormFileAdapter(data.formItemValue?.values!!, mJob)

        data.formItemValue!!.limitTypeList?.forEach {
            when (FormValueOfFile.getFileTypeByMime(it)) {
                "IMAGE" -> {
                    itemView.form_item_file_take_camera.visibility = View.VISIBLE
                    itemView.form_item_file_take_album.visibility = View.VISIBLE
                }
                "VIDEO" -> itemView.form_item_file_take_video.visibility = View.VISIBLE
                "AUDIO" -> itemView.form_item_file_take_audio.visibility = View.VISIBLE
                else -> {
                    itemView.form_item_file_take_camera.visibility = View.VISIBLE
                    itemView.form_item_file_take_album.visibility = View.VISIBLE
                    itemView.form_item_file_take_video.visibility = View.VISIBLE
                    itemView.form_item_file_take_audio.visibility = View.VISIBLE
                    itemView.form_item_file_take_file.visibility = View.VISIBLE
                }
            }
        }

        itemView.form_item_title.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
        itemView.form_item_file_take_camera.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
        itemView.form_item_file_take_album.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
        itemView.form_item_file_take_video.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
        itemView.form_item_file_take_audio.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
        itemView.form_item_file_take_file.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
    }

}