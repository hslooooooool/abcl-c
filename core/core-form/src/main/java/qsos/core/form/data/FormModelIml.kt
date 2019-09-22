package qsos.core.form.data

import qsos.core.form.db.entity.FormEntity
import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.Value

/**
 * @author : 华清松
 * 表单数据实现
 */
class FormModelIml(
        private val formRepo: IFormRepo = FormRepository()
) : IFormModel {

    override fun getForm(formId: Long): FormEntity? {
        return formRepo.getForm(formId)
    }

    override fun insertForm(form: FormEntity): FormEntity? {
        return formRepo.insertForm(form)
    }

    override fun insertFormItem(formItem: FormItem) {
        formRepo.insertFormItem(formItem)
    }

    override fun insertValue(formItemValue: Value): Value {
        return formRepo.insertValue(formItemValue)
    }

    override fun deleteForm(form: FormEntity) {
        formRepo.deleteForm(form)
    }

    override fun deleteFormItemAllValue(formItemId: Long) {
        formRepo.deleteFormItemAllValue(formItemId)
    }

    override fun getFormItem(formItemId: Long): FormItem? {
        return formRepo.getFormItem(formItemId)
    }

    override fun updateValue(value: Value) {
        formRepo.updateValue(value)
    }

}