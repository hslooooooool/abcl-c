package qsos.core.lib.utils.image

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule
import qsos.core.lib.config.BaseConfig

@GlideModule
class GlideCore : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {}

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // 运存缓存限制50M
        builder.setMemoryCache(LruResourceCache(50L * 1024 * 1024))
        // 磁盘缓存限制500M
        builder.setDiskCache(ExternalPreferredCacheDiskCacheFactory(context, diskCache(context), 500L * 1024 * 1024))
        // 日志级别
        builder.setLogLevel(Log.ERROR)
        // DEBUG模式才打印日志
        builder.setLogRequestOrigins(BaseConfig.DEBUG)
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    /**缓存路径，优先SD卡，否则内部存储*/
    private fun diskCache(context: Context): String {
        return (context.externalCacheDir?.path ?: context.cacheDir.path) + "/glide"
    }
}
