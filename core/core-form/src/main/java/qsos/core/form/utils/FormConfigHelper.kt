package qsos.core.form.utils

import android.content.Context
import qsos.core.form.config.IFormConfig
import qsos.core.form.db.entity.FormValueOfFile
import qsos.core.form.db.entity.FormValueOfLocation
import qsos.core.form.db.entity.FormValueOfUser

/**
 * @author : 华清松
 * 表单操作帮助类
 */
object FormConfigHelper : IFormConfig {

    override fun takeCamera(context: Context, formItemId: Long, onSuccess: (FormValueOfFile) -> Any) {
        mFormConfig?.takeCamera(context, formItemId, onSuccess)
    }

    override fun takeGallery(context: Context, formItemId: Long, canTakeSize: Int, onSuccess: (List<FormValueOfFile>) -> Any) {
        mFormConfig?.takeGallery(context, formItemId, canTakeSize, onSuccess)
    }

    override fun takeVideo(context: Context, formItemId: Long, canTakeSize: Int, onSuccess: (List<FormValueOfFile>) -> Any) {
        mFormConfig?.takeVideo(context, formItemId, canTakeSize, onSuccess)
    }

    override fun takeAudio(context: Context, formItemId: Long, onSuccess: (FormValueOfFile) -> Any) {
        mFormConfig?.takeAudio(context, formItemId, onSuccess)
    }

    override fun takeFile(context: Context, formItemId: Long, canTakeSize: Int, mimeTypes: List<String>, onSuccess: (List<FormValueOfFile>) -> Any) {
        mFormConfig?.takeFile(context, formItemId, canTakeSize, mimeTypes, onSuccess)
    }

    override fun takeLocation(context: Context, formItemId: Long, location: FormValueOfLocation?, onSuccess: (FormValueOfLocation) -> Any) {
        mFormConfig?.takeLocation(context, formItemId, location, onSuccess)
    }

    override fun takeUser(context: Context, formItemId: Long, canTakeSize: Int, checkedUsers: List<FormValueOfUser>, onSuccess: (List<FormValueOfUser>) -> Any) {
        mFormConfig?.takeUser(context, formItemId, canTakeSize, checkedUsers, onSuccess)
    }

    override fun previewFile(context: Context, index: Int, formValueOfFiles: List<FormValueOfFile>) {
        mFormConfig?.previewFile(context, index, formValueOfFiles)
    }

    override fun previewUser(context: Context, index: Int, formValueOfUser: List<FormValueOfUser>) {
        mFormConfig?.previewUser(context, index, formValueOfUser)
    }

    private var mFormConfig: IFormConfig? = null

    fun init(formConfig: IFormConfig) {
        this.mFormConfig = formConfig
    }
}