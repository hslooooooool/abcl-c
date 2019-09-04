package qsos.core.form.view.hodler

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.form_item_file.view.*
import qsos.core.form.db.entity.FormItem
import qsos.core.form.view.adapter.FormFileAdapter
import qsos.lib.base.base.holder.BaseHolder

import qsos.lib.base.callback.OnListItemClickListener

/**
 * @author : 华清松
 * @description : 文件表单项
 */
class ItemFormFileHolder(
        itemView: View,
        private val itemClick: OnListItemClickListener
) : BaseHolder<FormItem>(itemView) {

    @SuppressLint("SetTextI18n")
    override fun setData(data: FormItem, position: Int) {
        itemView.form_item_file_title.text = data.form_item_key

        if (data.form_item_value?.values != null) {
            val files = data.form_item_value?.values!!
            itemView.rv_item_form_files.layoutManager = GridLayoutManager(itemView.context, 4)

            itemView.rv_item_form_files.adapter = FormFileAdapter(files)
        }

        when (data.form_item_value!!.limit_type) {
            "image" -> {
                itemView.form_item_file_take_photo.visibility = View.VISIBLE
                itemView.form_item_file_take_album.visibility = View.VISIBLE
                itemView.form_item_file_take_video.visibility = View.GONE
                itemView.form_item_file_take_audio.visibility = View.GONE
            }
            "video" -> {
                itemView.form_item_file_take_photo.visibility = View.GONE
                itemView.form_item_file_take_album.visibility = View.GONE
                itemView.form_item_file_take_video.visibility = View.VISIBLE
                itemView.form_item_file_take_audio.visibility = View.GONE
            }
            "audio" -> {
                itemView.form_item_file_take_photo.visibility = View.GONE
                itemView.form_item_file_take_album.visibility = View.GONE
                itemView.form_item_file_take_video.visibility = View.GONE
                itemView.form_item_file_take_audio.visibility = View.VISIBLE
            }
            else -> {
                itemView.form_item_file_take_photo.visibility = View.VISIBLE
                itemView.form_item_file_take_album.visibility = View.VISIBLE
                itemView.form_item_file_take_video.visibility = View.VISIBLE
                itemView.form_item_file_take_audio.visibility = View.VISIBLE
            }
        }

        itemView.form_item_file_take_photo.setOnClickListener {
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
    }

}