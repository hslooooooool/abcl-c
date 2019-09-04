package qsos.core.form.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import qsos.core.form.db.FormDatabase
import qsos.core.form.db.entity.FormEntity
import qsos.core.form.db.entity.FormType
import qsos.core.form.db.entity.FormUserEntity
import qsos.core.form.db.entity.Value
import qsos.core.form.utils.FormTransUtils
import qsos.core.form.utils.FormVerifyUtils
import timber.log.Timber
import qsos.core.form.db.entity.FormItem as FormItem1

/**
 * @author : 华清松
 * @description : 表单数据获取
 */
@SuppressLint("CheckResult")
class FormRepository(
        private val mContext: Context
) : IFormModel {

    /**NOTICE 数据库操作*/

    override fun getFormByDB(formId: Long) {
        FormDatabase.getInstance(mContext).formDao.getFormById(formId)
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    it.form_item = FormDatabase.getInstance(mContext).formItemDao.getFormItemByFormId(it.id!!)
                    it.form_item?.forEach { formItem ->
                        formItem.form_item_value!!.values = arrayListOf()
                        formItem.form_item_value!!.values!!.addAll(FormDatabase.getInstance(mContext).formItemValueDao.getValueByFormItemId(formItem.id!!))
                        Timber.tag("数据库").i("查询formItem={formItem.id}下Value列表：${Gson().toJson(formItem.form_item_value!!.values)}")
                    }
                }
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            dbFormEntity.postValue(it)
                        },
                        {
                            dbFormEntity.postValue(null)
                        }
                )
    }

    override fun insertForm(form: FormEntity) {
        Observable.create<FormEntity> {
            val id = FormDatabase.getInstance(mContext).formDao.insert(form)
            form.id = id
            form.form_item?.forEach { formItem ->
                formItem.form_id = id
                insertFormItem(formItem)
            }
            if (form.id != null) {
                it.onNext(form)
            } else {
                it.onError(Exception("数据插入失败"))
            }
        }.subscribeOn(Schedulers.io())
                .subscribe(
                        {
                            getFormByDB(it.id!!)
                        },
                        {
                            it.printStackTrace()
                            dbFormEntity.postValue(null)
                        }
                )
    }

    override fun insertFormItem(formItem: FormItem1) {
        val formItemId = FormDatabase.getInstance(mContext).formItemDao.insert(formItem)
        formItem.form_item_value!!.values?.forEach {
            it.form_item_id = formItemId
            insertValue(it)
        }
    }

    override fun insertValue(formItemValue: Value) {
        FormDatabase.getInstance(mContext).formItemValueDao.insert(formItemValue)
    }

    override fun addValueToFormItem(formItemValue: Value) {
        Observable.create<Value> {
            val id = FormDatabase.getInstance(mContext).formItemValueDao.insert(formItemValue)
            formItemValue.id = id
            it.onNext(formItemValue)
        }.subscribeOn(Schedulers.io())
                .subscribe(
                        {
                            addValueToFormItem.postValue(it)
                        },
                        {
                            addValueToFormItem.postValue(null)
                        }
                )
    }

    override fun deleteForm(form: FormEntity) {
        Observable.create<Boolean> {
            FormDatabase.getInstance(mContext).formDao.delete(form)
            it.onNext(true)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    dbDeleteForm.postValue(it)
                }
    }

    override fun getFormItemByDB(formItemId: Long) {
        FormDatabase.getInstance(mContext).formItemDao.getFormItemByIdF(formItemId)
                .observeOn(Schedulers.io())
                .map {
                    it.form_item_value!!.values = arrayListOf()
                    it.form_item_value!!.values?.addAll(FormDatabase.getInstance(mContext).formItemValueDao.getValueByFormItemId(formItemId))
                    it
                }.observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    dbFormItem.postValue(it)
                }
    }

    override fun updateValue(value: Value) {
        FormDatabase.getInstance(mContext).formItemValueDao.update(value)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    updateValueStatus.postValue(true)
                }
    }

    override fun postForm(formType: String, connectId: String?, formId: Long) {
        FormDatabase.getInstance(mContext).formDao.getFormById(formId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(
                        {
                            // TODO 提交数据
                        },
                        {
                            postFormStatus.postValue(FormVerifyUtils.Verify(false, "提交失败 $it"))
                        }
                )

    }

    override fun getForm(formType: FormType, connectId: String?) {
        val form = when (formType) {
            FormType.ADD_EXECUTE -> FormTransUtils.getTestExecuteData()
            FormType.ADD_NOTICE -> FormTransUtils.getTestOrderFeedbackData()
        }
        if (form.form_item == null) {
            dbFormEntity.postValue(null)
        } else {
            insertForm(form)
        }
    }

    override fun getUsers(connectId: String?, formItem: FormItem1, key: String?) {

    }

    // 获取表单数据结果
    val dbFormEntity = MutableLiveData<FormEntity?>()

    // 获取表单项数据结果
    val dbFormItem = MutableLiveData<FormItem1>()

    // 删除表单数据结果
    val dbDeleteForm = MutableLiveData<Boolean>()

    // 用户列表数据结果
    val userList = MutableLiveData<List<FormUserEntity>?>()

    // 提交表单数据结果
    val postFormStatus = MutableLiveData<FormVerifyUtils.Verify>()

    // 更新列表项值数据结果
    val updateValueStatus = MutableLiveData<Boolean>()

    // 插入Value到FormItem后数据结果
    val addValueToFormItem = MutableLiveData<Value?>()
}