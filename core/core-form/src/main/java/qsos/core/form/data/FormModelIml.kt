package qsos.core.form.data

import io.reactivex.Completable
import io.reactivex.Flowable
import qsos.core.form.db.entity.FormEntity
import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.Value

/**
 * @author : 华清松
 * 表单数据实现
 */
class FormModelIml(
        val formRepo: IFormRepo = FormRepository()
) : IFormModel {

    override fun getForm(formId: Long): Flowable<FormEntity> {
        return formRepo.getForm(formId)
    }

    override fun insertForm(form: FormEntity): Flowable<FormEntity> {
        return formRepo.insertForm(form)
    }

    override fun insertFormItem(formItem: FormItem) {
        formRepo.insertFormItem(formItem)
    }

    override fun insertValue(formItemValue: Value) {
        formRepo.insertValue(formItemValue)
    }

    override fun addValueToFormItem(formItemValue: Value): Flowable<Value> {
        return formRepo.addValueToFormItem(formItemValue)
    }

    override fun deleteForm(form: FormEntity): Completable {
        return formRepo.deleteForm(form)
    }

    override fun postForm(formType: String, formId: Long): Flowable<FormEntity> {
        return formRepo.postForm(formType, formId)
    }

    override fun getFormItemByDB(formItemId: Long): Flowable<FormItem> {
        return formRepo.getFormItemByDB(formItemId)
    }

    override fun updateValue(value: Value): Completable {
        return formRepo.updateValue(value)
    }

    override fun getUsers(formItem: FormItem, key: String?): Flowable<List<Value>> {
        return formRepo.getUsers(formItem, key)
    }
}