package qsos.core.player

/**
 * @author : 华清松
 * 媒体预览路由地址
 */
object PlayerPath {
    const val GROUP = "PLAYER"
    /**画廊界面*/
    const val GALLERY = "/$GROUP/GALLERY"
    /**
     * 画廊列表数据，为json格式，转换实体
     * @see qsos.core.player.data.PreFileEntity
     */
    const val GALLERY_DATA = "/$GROUP/GALLERY_DATA"

}