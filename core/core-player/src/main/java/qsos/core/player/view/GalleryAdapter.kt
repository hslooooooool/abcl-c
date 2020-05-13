package qsos.core.player.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_gallery_image.view.*
import qsos.core.lib.utils.image.ImageLoaderUtils
import qsos.core.player.R
import qsos.core.player.data.PreImageEntity
import qsos.lib.base.base.adapter.BaseAdapter
import qsos.lib.base.base.holder.BaseHolder

/**
 * @author : 华清松
 * 画廊容器
 */
class GalleryAdapter(mList: ArrayList<PreImageEntity>) : BaseAdapter<PreImageEntity>(mList) {

    override fun getHolder(view: View, viewType: Int): BaseHolder<PreImageEntity> = GalleryHolder(view)

    override fun getLayoutId(viewType: Int): Int = R.layout.item_gallery_image
}

class GalleryHolder(itemView: View) : BaseHolder<PreImageEntity>(itemView) {
    override fun bind(data: PreImageEntity, position: Int) {
        itemView.item_gallery_image.setTag(itemView.item_gallery_image.id, data.path)
        val tag = itemView.item_gallery_image.getTag(itemView.item_gallery_image.id) as String
        if (tag == data.path) {
            ImageLoaderUtils.display(itemView.context, itemView.item_gallery_image, data.path)
        }
        itemView.item_gallery_image.setOnClickListener {
            (itemView.parent as RecyclerView).smoothScrollToPosition(position)
        }
    }
}
