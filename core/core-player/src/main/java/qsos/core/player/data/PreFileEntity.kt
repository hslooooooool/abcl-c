package qsos.core.player.data

/**
 * @author : 华清松
 * 预览文件数据
 * @param type 文件类型 0 图片 1 视频 2 音频 3 文档
 * @param position 文件位置
 * @param data 文件集合
 */
data class PreFileEntity<T>(
        val position: Int = 0,
        val data: List<T> = arrayListOf()
)

/**
 * @author : 华清松
 * 预览图片数据
 * @param name 图片名称
 * @param path 图片地址
 * @param desc 图片描述
 */
data class PreImageEntity(
        val name: String = "",
        val path: String = "",
        val desc: String = ""
)

/**
 * @author : 华清松
 * 预览视频数据
 * @param name 视频名称
 * @param path 视频地址
 * @param avatar 视频封面
 * @param desc 视频描述
 */
data class PreVideoEntity(
        val name: String = "",
        val path: String = "",
        val avatar: String = "",
        val desc: String = ""
)

/**
 * @author : 华清松
 * 预览音频数据
 * @param name 音频名称
 * @param path 音频地址
 * @param desc 音频描述
 */
data class PreAudioEntity(
        val name: String = "",
        val path: String = "",
        val desc: String = ""
)

/**
 * @author : 华清松
 * 预览文档数据
 * @param name 文档名称
 * @param path 文档地址
 * @param desc 文档描述
 */
data class PreDocumentEntity(
        val name: String = "",
        val path: String = "",
        val desc: String = ""
)