package qsos.core.form.data

import qsos.core.form.db.entity.FormEntity
import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.FormType
import qsos.core.form.db.entity.Value

/**
 * @author : 华清松
 * 表单数据实现
 */
class FormModelIml(
        val formRepo: FormRepository
) : IFormModel {

    override fun getForm(formType: FormType) {
        formRepo.getForm(formType)
    }

    override fun getFormByDB(formId: Long) {
        formRepo.getFormByDB(formId)
    }

    override fun insertForm(form: FormEntity) {
        formRepo.insertForm(form)
    }

    override fun insertFormItem(formItem: FormItem) {
        formRepo.insertFormItem(formItem)
    }

    override fun insertValue(formItemValue: Value) {
        formRepo.insertValue(formItemValue)
    }

    override fun addValueToFormItem(formItemValue: Value) {
        formRepo.addValueToFormItem(formItemValue)
    }

    override fun deleteForm(form: FormEntity) {
        formRepo.deleteForm(form)
    }

    override fun postForm(formType: String, formId: Long) {
        formRepo.postForm(formType, formId)
    }

    override fun getFormItemByDB(formItemId: Long) {
        formRepo.getFormItemByDB(formItemId)
    }

    override fun updateValue(value: Value) {
        formRepo.updateValue(value)
    }

    override fun getUsers(formItem: FormItem, key: String?) {
        formRepo.getUsers(formItem, key)
    }
}