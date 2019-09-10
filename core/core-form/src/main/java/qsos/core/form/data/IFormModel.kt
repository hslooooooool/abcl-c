package qsos.core.form.data

import qsos.core.form.db.entity.FormEntity
import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.FormType
import qsos.core.form.db.entity.Value

/**
 * @author : 华清松
 * 表单操作接口
 */
interface IFormModel {

    /**获取表单数据库*/
    fun getFormByDB(formId: Long)

    /**插入表单数据*/
    fun insertForm(form: FormEntity, success: (form: FormEntity) -> Any?)

    /**插入表单项数据*/
    fun insertFormItem(formItem: FormItem)

    /**插入表单项值数据*/
    fun insertValue(formItemValue: Value)

    /**插入Value到FormItem*/
    fun addValueToFormItem(formItemValue: Value)

    /**清除表单数据*/
    fun deleteForm(form: FormEntity)

    /**提交表单数据*/
    fun postForm(formType: String, formId: Long)

    /**获取表单项数据*/
    fun getFormItemByDB(formItemId: Long)

    /**更新表单项数据*/
    fun updateValue(value: Value)

    /**获取可选用户列表 key 可以是姓名、手机号*/
    fun getUsers(formItem: FormItem, key: String?)
}