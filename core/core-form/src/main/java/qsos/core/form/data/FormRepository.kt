package qsos.core.form.data

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import qsos.core.form.db.FormDatabase
import qsos.core.form.db.entity.FormEntity
import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.Value

/**
 * @author : 华清松
 * 表单数据获取
 */
class FormRepository : IFormRepo {

    override fun getForm(formId: Long): Flowable<FormEntity> {
        return FormDatabase.getInstance().formDao.getFormById(formId)
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    it.formItems = FormDatabase.getInstance().formItemDao.getFormItemByFormId(it.id!!)
                    it.formItems?.forEach { formItem ->
                        formItem.formItemValue?.values?.clear()
                        formItem.formItemValue?.values?.addAll(
                                FormDatabase.getInstance().formItemValueDao.getByFormItemId(formItem.id!!)
                        )
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun insertForm(form: FormEntity): Flowable<FormEntity> {
        return Flowable.just(form)
                .subscribeOn(Schedulers.io())
                .map {
                    FormDatabase.getInstance().formDao.insert(it)
                }
                .flatMap {
                    form.id = it
                    form.formItems?.forEach { formItem ->
                        formItem.formId = it
                        insertFormItem(formItem)
                    }
                    Flowable.just(form)
                }.observeOn(AndroidSchedulers.mainThread())
    }

    override fun insertFormItem(formItem: FormItem) {
        val formItemId = FormDatabase.getInstance().formItemDao.insert(formItem)
        formItem.formItemValue!!.values?.forEach {
            it.formItemId = formItemId
            insertValue(it)
        }
    }

    override fun insertValue(formItemValue: Value) {
        FormDatabase.getInstance().formItemValueDao.insert(formItemValue)
    }

    override fun addValueToFormItem(formItemValue: Value): Flowable<Value> {
        return Flowable.just(formItemValue)
                .subscribeOn(Schedulers.io())
                .map {
                    FormDatabase.getInstance().formItemValueDao.insert(formItemValue)
                }
                .flatMap {
                    formItemValue.id = it
                    Flowable.just(formItemValue)
                }.observeOn(AndroidSchedulers.mainThread())
    }

    override fun deleteForm(form: FormEntity): Completable {
        return FormDatabase.getInstance().formDao.delete(form)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getFormItemByDB(formItemId: Long): Flowable<FormItem> {
        return FormDatabase.getInstance().formItemDao.getFormItemByIdF(formItemId)
                .subscribeOn(Schedulers.io())
                .map {
                    it.formItemValue?.values?.clear()
                    it.formItemValue?.values?.addAll(FormDatabase.getInstance().formItemValueDao.getByFormItemId(formItemId))
                    it
                }.observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateValue(value: Value): Completable {
        return FormDatabase.getInstance().formItemValueDao.update(value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun postForm(formType: String, formId: Long): Flowable<FormEntity> {
        return FormDatabase.getInstance().formDao.getFormById(formId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getUsers(formItem: FormItem, key: String?): Flowable<List<Value>> {
        return FormDatabase.getInstance().formItemValueDao.getUserByFormItemIdAndUserDesc(formItem.id!!, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}