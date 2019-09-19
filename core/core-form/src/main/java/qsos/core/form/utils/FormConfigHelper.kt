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

    override fun previewLocation(formValueOfLocation: FormValueOfLocation) {
        mFormConfig?.previewLocation(formValueOfLocation)
    }

    override fun takeCamera(onSuccess: (FormValueOfFile) -> Any) {
        mFormConfig?.takeCamera(onSuccess)
    }

    override fun takeGallery(onSuccess: (List<FormValueOfFile>) -> Any) {
        mFormConfig?.takeGallery(onSuccess)
    }

    override fun takeVideo(onSuccess: (List<FormValueOfFile>) -> Any) {
        mFormConfig?.takeVideo(onSuccess)
    }

    override fun takeAudio(onSuccess: (FormValueOfFile) -> Any) {
        mFormConfig?.takeAudio(onSuccess)
    }

    override fun takeFile(mimeTypes: List<String>, onSuccess: (List<FormValueOfFile>) -> Any) {
        mFormConfig?.takeFile(mimeTypes, onSuccess)
    }

    override fun takeLocation(onSuccess: (FormValueOfLocation) -> Any) {
        mFormConfig?.takeLocation(onSuccess)
    }

    private var mFormConfig: IFormConfig? = null

    fun init(formConfig: IFormConfig) {
        this.mFormConfig = formConfig
    }


}