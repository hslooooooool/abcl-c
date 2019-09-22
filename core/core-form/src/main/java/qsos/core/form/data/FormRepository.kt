package qsos.core.form.data

import qsos.core.form.db.FormDatabase
import qsos.core.form.db.entity.FormEntity
import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.Value

/**
 * @author : 华清松
 * 表单数据获取
 */
class FormRepository : IFormRepo {

    override fun getForm(formId: Long): FormEntity? {
        var sForm: FormEntity? = null
        FormDatabase.getInstance().formDao.getFormById(formId)?.let {
            it.formItems = FormDatabase.getInstance().formItemDao.getFormItemByFormId(it.id!!)
            it.formItems?.forEach { item ->
                item.formItemValue?.values?.clear()
                item.formItemValue?.values?.addAll(
                        FormDatabase.getInstance().formItemValueDao.getByFormItemId(item.id!!)
                )
            }
            sForm = it
        }
        return sForm
    }

    override fun insertForm(form: FormEntity): FormEntity {
        form.id = FormDatabase.getInstance().formDao.insert(form)
        form.formItems?.forEach { formItem ->
            formItem.formId = form.id
            insertFormItem(formItem)
        }
        return form
    }

    override fun insertFormItem(formItem: FormItem) {
        val formItemId = FormDatabase.getInstance().formItemDao.insert(formItem)
        formItem.formItemValue!!.values?.forEach {
            it.formItemId = formItemId
            insertValue(it)
        }
    }

    override fun insertValue(formItemValue: Value): Value {
        formItemValue.id = FormDatabase.getInstance().formItemValueDao.insert(formItemValue)
        return formItemValue
    }

    override fun deleteForm(form: FormEntity) {
        FormDatabase.getInstance().formDao.delete(form)
    }

    override fun deleteFormItemAllValue(formItemId: Long) {
        FormDatabase.getInstance().formItemValueDao.deleteByFormItemId(formItemId)
    }

    override fun getFormItem(formItemId: Long): FormItem? {
        var sFormItem: FormItem? = null
        FormDatabase.getInstance().formItemDao.getFormItem(formItemId)?.let {
            it.formItemValue?.values?.clear()
            it.formItemValue?.values?.addAll(FormDatabase.getInstance().formItemValueDao.getByFormItemId(formItemId))
            sFormItem = it
        }
        return sFormItem
    }

    override fun updateValue(value: Value) {
        FormDatabase.getInstance().formItemValueDao.update(value)
    }

}