package qsos.core.player.view

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import qsos.core.player.PlayerPath
import qsos.core.player.R
import qsos.core.player.data.PreFileEntity
import qsos.core.player.data.PreImageEntity
import qsos.lib.base.base.activity.BaseActivity

/**
 * @author : 华清松
 * 画廊界面
 */
@Route(group = PlayerPath.GROUP, path = PlayerPath.GALLERY)
class GalleryActivity(
        override val layoutId: Int = R.layout.activity_gallery,
        override val reload: Boolean = false
) : BaseActivity() {

    @Autowired(name = PlayerPath.GALLERY_DATA)
    @JvmField
    var mData: String? = null

    private var mFile: PreFileEntity<PreImageEntity>? = null
    private var mImageList: ArrayList<PreImageEntity>? = null

    override fun initData(savedInstanceState: Bundle?) {
        mFile = try {
            Gson().fromJson(mData, object : TypeToken<PreFileEntity<PreImageEntity>>() {}.type)
        } catch (e: Exception) {
            null
        }
    }

    override fun initView() {
        if (mFile == null || mFile?.data.isNullOrEmpty()) finish()
        mImageList = arrayListOf()
        mImageList!!.addAll(mFile!!.data)
        supportFragmentManager.beginTransaction().add(R.id.gallery_frg, GalleryFragment(mFile!!.position, mImageList!!), "GalleryFragment").commit()
    }

    override fun getData() {}

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_in_center, R.anim.activity_out_center)
    }
}