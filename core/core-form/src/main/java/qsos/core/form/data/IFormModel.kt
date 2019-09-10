package qsos.core.form.data

import io.reactivex.Completable
import io.reactivex.Flowable
import qsos.core.form.db.entity.FormEntity
import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.Value

/**
 * @author : 华清松
 * 表单 M 层
 */
interface IFormModel : IFormRepo

/**
 * @author : 华清松
 * 表单 M 层实现
 */
interface IFormRepo {

    /**获取表单数据*/
    fun getForm(formId: Long): Flowable<FormEntity>

    /**插入表单数据*/
    fun insertForm(form: FormEntity): Flowable<FormEntity>

    /**插入表单项数据*/
    fun insertFormItem(formItem: FormItem)

    /**插入表单项值数据*/
    fun insertValue(formItemValue: Value)

    /**插入Value到FormItem*/
    fun addValueToFormItem(formItemValue: Value): Flowable<Value>

    /**清除表单数据*/
    fun deleteForm(form: FormEntity): Completable

    /**提交表单数据*/
    fun postForm(formType: String, formId: Long): Flowable<FormEntity>

    /**获取表单项数据*/
    fun getFormItemByDB(formItemId: Long): Flowable<FormItem>

    /**更新表单项数据*/
    fun updateValue(value: Value): Completable

    /**获取可选用户列表 key 可以是姓名、手机号*/
    fun getUsers(formItem: FormItem, key: String?): Flowable<List<Value>>
}