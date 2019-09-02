package qsos.app.demo.view.widget.image

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.noober.menu.FloatMenu
import qsos.app.demo.R
import qsos.core.lib.utils.image.GlideApp
import qsos.core.lib.utils.image.ImageLoaderUtils

/**
 * @author : 华清松
 * 九宫格图片布局
 */
class NineGridLayout : AbsNineGridLayout {
    private var mListener: OnImageClickListener? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    private fun initView() {

    }

    override fun displayOneImage(imageView: RatioImageView, url: String, parentWidth: Int): Boolean {
        GlideApp.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .dontAnimate()
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        val w = resource.intrinsicWidth
                        val h = resource.intrinsicHeight
                        val newW: Int
                        val newH: Int
                        when {
                            h > w * MAX_RATIO -> {
                                //h:w = 5:3
                                newW = parentWidth / 2
                                newH = newW * 5 / 3
                            }
                            h < w -> {
                                //h:w = 2:3
                                newW = parentWidth * 2 / 3
                                newH = newW * 2 / 3
                            }
                            else -> {
                                //newH:h = newW :w
                                newW = parentWidth / 2
                                newH = h * newW / w
                            }
                        }
                        setOneImageLayoutParams(imageView, newW, newH)
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                })
                .into(imageView)
        return false
    }

    override fun displayImage(imageView: RatioImageView, url: String) {
        ImageLoaderUtils.display(imageView.context, imageView, url)
    }

    override fun onClickImage(view: View, position: Int, url: String, urlList: List<String>) {
        mListener?.onClickImage(view, position, urlList)
    }

    override fun onLongClickImage(view: View, position: Int, url: String, menu: FloatMenu) {
        menu.setOnItemClickListener { _, index ->
            mListener?.onLongClickImage(view, position, url, index)
        }
        menu.show()
    }

    fun setOnClickListener(listener: OnImageClickListener) {
        this.mListener = listener
    }

    interface OnImageClickListener {
        fun onClickImage(view: View, position: Int, urls: List<String>)
        fun onLongClickImage(view: View, position: Int, url: String, index: Int)
    }

    companion object {
        /**最大列数*/
        private const val MAX_RATIO = 3
    }
}
