package qsos.core.form.config

import qsos.core.form.db.entity.FormValueOfFile
import qsos.core.form.db.entity.FormValueOfLocation

/**
 * @author : 华清松
 * 表单多媒体数据（文件、定位等等）预览接口
 */
interface IFormPreviewConfig {

    /**
     * 文件预览
     * @param index 当前预览文件下标
     * @param formValueOfFiles 可预览的图片集合
     */
    fun previewFile(index: Int, formValueOfFiles: List<FormValueOfFile>)

    /**
     * 位置预览
     */
    fun previewLocation(formValueOfLocation: FormValueOfLocation)
}