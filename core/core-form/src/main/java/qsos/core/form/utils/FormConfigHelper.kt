package qsos.core.form.utils

import qsos.core.form.config.IFormConfig
import qsos.core.form.db.entity.FormValueOfFile
import qsos.core.form.db.entity.FormValueOfLocation

/**
 * @author : 华清松
 * 表单操作帮助类
 */
object FormConfigHelper : IFormConfig {
    override fun previewFile(index: Int, formValueOfFiles: List<FormValueOfFile>) {
        mFormConfig?.previewFile(index, formValueOfFiles)
    }

    override fun takeCamera(onSuccess: (FormValueOfFile) -> Any) {
        mFormConfig?.takeCamera(onSuccess)
    }

    override fun takeGallery(canTakeSize: Int, onSuccess: (List<FormValueOfFile>) -> Any) {
        mFormConfig?.takeGallery(canTakeSize, onSuccess)
    }

    override fun takeVideo(canTakeSize: Int, onSuccess: (List<FormValueOfFile>) -> Any) {
        mFormConfig?.takeVideo(canTakeSize, onSuccess)
    }

    override fun takeAudio(onSuccess: (FormValueOfFile) -> Any) {
        mFormConfig?.takeAudio(onSuccess)
    }

    override fun takeFile(canTakeSize: Int, mimeTypes: List<String>, onSuccess: (List<FormValueOfFile>) -> Any) {
        mFormConfig?.takeFile(canTakeSize, mimeTypes, onSuccess)
    }

    override fun takeLocation(location: FormValueOfLocation?, onSuccess: (FormValueOfLocation) -> Any) {
        mFormConfig?.takeLocation(location, onSuccess)
    }

    private var mFormConfig: IFormConfig? = null

    fun init(formConfig: IFormConfig) {
        this.mFormConfig = formConfig
    }


}