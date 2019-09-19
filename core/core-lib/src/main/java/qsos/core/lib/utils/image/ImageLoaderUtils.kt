package qsos.core.lib.utils.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

/**
 * @author : 华清松
 * 图片加载工具类 使用glide框架封装，已感知LifecycleListener
 */
object ImageLoaderUtils {

    /**加载url图片*/
    fun display(context: Context, imageView: ImageView?, url: String?) {
        if (imageView == null) {
            return
        }
        if (TextUtils.isEmpty(url)) {
            return
        }
        GlideApp.with(context)
                .load(url)
                // 磁盘缓存
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                // 避免闪烁
                .skipMemoryCache(false)
                .dontAnimate()
                .into(imageView)
    }

    /**加载url图片*/
    fun display(context: Context, imageView: ImageView?, url: String?, placeholder: Drawable?) {
        if (imageView == null) {
            return
        }
        if (TextUtils.isEmpty(url)) {
            return
        }
        GlideApp.with(context)
                .load(url)
                // 磁盘缓存
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                // 避免闪烁
                .skipMemoryCache(false)
                .dontAnimate()
                .placeholder(placeholder)
                .into(imageView)
    }

    /**加载资源图片*/
    fun display(context: Context, imageView: ImageView?, @DrawableRes url: Int) {
        if (imageView == null) {
            return
        }
        GlideApp.with(context)
                .load(url)
                // 磁盘缓存
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                // 避免闪烁
                .skipMemoryCache(false)
                .dontAnimate()
                .into(imageView)
    }

    /**加载资源图片*/
    fun display(context: Context, imageView: ImageView?, drawable: Drawable) {
        if (imageView == null) {
            return
        }
        GlideApp.with(context)
                .load(drawable)
                // 磁盘缓存
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                // 避免闪烁
                .skipMemoryCache(false)
                .dontAnimate()
                .into(imageView)
    }

    /**加载资源图片*/
    fun display(context: Context, imageView: ImageView?, bitmap: Bitmap) {
        if (imageView == null) {
            return
        }
        GlideApp.with(context)
                .load(bitmap)
                // 磁盘缓存
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                // 避免闪烁
                .skipMemoryCache(false)
                .dontAnimate()
                .into(imageView)
    }

    /**加载url图片,失败加载默认图片*/
    fun displayWithErrorAndPlace(context: Context, imageView: ImageView?, url: String?, @DrawableRes error: Int, @DrawableRes place: Int) {
        if (imageView == null) {
            return
        }
        if (TextUtils.isEmpty(url)) {
            return
        }
        GlideApp.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .skipMemoryCache(false)
                .dontAnimate()
                .placeholder(ContextCompat.getDrawable(context, place))
                .error(ContextCompat.getDrawable(context, error))
                .into(imageView)
    }

    /**加载圆角图片*/
    fun displayRounded(context: Context, imageView: ImageView?, url: String?) {
        if (imageView == null) {
            return
        }
        if (TextUtils.isEmpty(url)) {
            return
        }
        GlideApp.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .skipMemoryCache(false)
                .dontAnimate()
                .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                .into(imageView)
    }

    /**加载GIF图片*/
    fun displayGif(context: Context, imageView: ImageView?, url: String?) {
        if (imageView == null) {
            return
        }
        if (url == null) {
            return
        }
        GlideApp.with(context)
                .asGif()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .skipMemoryCache(false)
                .dontAnimate()
                .into(imageView)
    }

    /**加载圆角图片*/
    fun displayRounded(context: Context, url: String?, imageView: ImageView?, round: Int?) {
        if (imageView == null) {
            return
        }
        if (TextUtils.isEmpty(url)) {
            return
        }
        GlideApp.with(context)
                .load(url)
                // 磁盘缓存
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                // 避免闪烁
                .skipMemoryCache(false)
                .dontAnimate()
                .apply(RequestOptions.bitmapTransform(RoundedCorners(round ?: 20)))
                .into(imageView)
    }
}
