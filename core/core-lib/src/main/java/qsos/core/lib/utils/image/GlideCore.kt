package qsos.core.lib.utils.image

import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.resource.SimpleResource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.bumptech.glide.module.AppGlideModule
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import qsos.core.lib.config.CoreConfig
import java.io.IOException
import java.io.InputStream


@GlideModule
class GlideCore : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.register(SVG::class.java, PictureDrawable::class.java, SvgToDrawableTranscoder())
                .append(InputStream::class.java, SVG::class.java, SvgDecoder())
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // 运存缓存限制50M
        builder.setMemoryCache(LruResourceCache(50L * 1024 * 1024))
        // 磁盘缓存限制500M
        builder.setDiskCache(ExternalPreferredCacheDiskCacheFactory(context, diskCache(context), 500L * 1024 * 1024))
        // 日志级别
        builder.setLogLevel(Log.ERROR)
        // DEBUG模式才打印日志
        builder.setLogRequestOrigins(CoreConfig.DEBUG)
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    /**缓存路径，优先SD卡，否则内部存储*/
    private fun diskCache(context: Context): String {
        return (context.externalCacheDir?.path ?: context.cacheDir.path) + "/glide"
    }
}

/**
 * @author : 华清松
 * 自定义ResourceTranscoder，将SVG转为Drawable对象
 */
class SvgToDrawableTranscoder : ResourceTranscoder<SVG, PictureDrawable> {
    override fun transcode(toTranscode: Resource<SVG>, options: Options): Resource<PictureDrawable>? {
        val svg = toTranscode.get()
        val picture = svg.renderToPicture()
        val drawable = PictureDrawable(picture)
        return SimpleResource(drawable)
    }

}

class SvgDecoder : ResourceDecoder<InputStream, SVG> {

    override fun handles(source: InputStream, options: Options): Boolean {
        return true
    }

    @Throws(IOException::class)
    override fun decode(source: InputStream, width: Int, height: Int, options: Options): Resource<SVG>? {
        try {
            val svg = SVG.getFromInputStream(source)
            return SimpleResource(svg)
        } catch (ex: SVGParseException) {
            throw IOException("Cannot load SVG from stream", ex)
        }

    }
}