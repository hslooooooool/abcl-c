package qsos.core.form.utils

import qsos.core.form.config.IFormConfig
import qsos.core.form.db.entity.FormValueOfFile
import qsos.core.form.db.entity.FormValueOfLocation
import qsos.core.form.db.entity.FormValueOfUser

/**
 * @author : 华清松
 * 表单操作帮助类
 */
object FormConfigHelper : IFormConfig {

    override fun takeCamera(formItemId: Long, onSuccess: (FormValueOfFile) -> Any) {
        mFormConfig?.takeCamera(formItemId, onSuccess)
    }

    override fun takeGallery(formItemId: Long, canTakeSize: Int, onSuccess: (List<FormValueOfFile>) -> Any) {
        mFormConfig?.takeGallery(formItemId, canTakeSize, onSuccess)
    }

    override fun takeVideo(formItemId: Long, canTakeSize: Int, onSuccess: (List<FormValueOfFile>) -> Any) {
        mFormConfig?.takeVideo(formItemId, canTakeSize, onSuccess)
    }

    override fun takeAudio(formItemId: Long, onSuccess: (FormValueOfFile) -> Any) {
        mFormConfig?.takeAudio(formItemId, onSuccess)
    }

    override fun takeFile(formItemId: Long, canTakeSize: Int, mimeTypes: List<String>, onSuccess: (List<FormValueOfFile>) -> Any) {
        mFormConfig?.takeFile(formItemId, canTakeSize, mimeTypes, onSuccess)
    }

    override fun takeLocation(formItemId: Long, location: FormValueOfLocation?, onSuccess: (FormValueOfLocation) -> Any) {
        mFormConfig?.takeLocation(formItemId, location, onSuccess)
    }

    override fun takeUser(formItemId: Long, canTakeSize: Int, checkedUsers: List<FormValueOfUser>, onSuccess: (List<FormValueOfUser>) -> Any) {
        mFormConfig?.takeUser(formItemId, canTakeSize, checkedUsers, onSuccess)
    }

    override fun previewFile(index: Int, formValueOfFiles: List<FormValueOfFile>) {
        mFormConfig?.previewFile(index, formValueOfFiles)
    }

    private var mFormConfig: IFormConfig? = null

    fun init(formConfig: IFormConfig) {
        this.mFormConfig = formConfig
    }
}