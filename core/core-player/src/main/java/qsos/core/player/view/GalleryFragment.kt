package qsos.core.player.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_gallery_image.*
import qsos.core.player.R
import qsos.core.player.data.PreImageEntity
import qsos.lib.base.base.fragment.BaseFragment

/**
 * @author : 华清松
 * 画廊页面
 */
class GalleryFragment(
        private val mPosition: Int = 0,
        private val mImageList: ArrayList<PreImageEntity> = arrayListOf(),
        override val layoutId: Int = R.layout.fragment_gallery_image,
        override val reload: Boolean = false
) : BaseFragment() {

    override fun initData(savedInstanceState: Bundle?) {}

    override fun initView(view: View) {
        gallery_image_rv.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        gallery_image_rv.adapter = GalleryAdapter(mImageList)

        GalleryScalableHelper(gallery_image_rv, object : GalleryScalableHelper.OnPageChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                gallery_image_position.text = "${position + 1}/${mImageList.size}"
                gallery_image_name.text = mImageList[position].name
            }
        }).build()
        gallery_image_rv.scrollToPosition(mPosition)
    }

    override fun getData() {}
}