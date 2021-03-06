package qsos.core.form.data

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
    fun getForm(formId: Long): FormEntity?

    /**插入表单数据*/
    fun insertForm(form: FormEntity): FormEntity?

    /**插入表单项数据*/
    fun insertFormItem(formItem: FormItem)

    /**插入表单项值数据*/
    fun insertValue(formItemValue: Value): Value

    /**清除表单数据*/
    fun deleteForm(form: FormEntity)

    /**清除表单项下值列表数据*/
    fun deleteFormItemAllValue(formItemId: Long)

    /**获取表单项数据*/
    fun getFormItem(formItemId: Long): FormItem?

    /**更新表单项数据*/
    fun updateValue(value: Value)

}