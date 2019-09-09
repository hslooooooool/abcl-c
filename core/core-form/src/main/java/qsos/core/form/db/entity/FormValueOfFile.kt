package qsos.core.form.db.entity

/**
 * @author : 华清松
 * 表单项值-文件实体类
 * @param fileId 文件ID
 * @param fileName 文件名称
 * @param fileType 文件类型，IMAGE VIDEO AUDIO FILE
 * @param filePath 文件本地路径地址，如/0/data/app1/logo.png
 * @param fileUrl 文件服务器路径地址，如http://qsos.vip/file/logo.png
 */
data class FormValueOfFile(
        var fileId: String? = "",
        var fileName: String? = null,
        var fileType: String? = null,
        var filePath: String? = null,
        var fileUrl: String? = null
)