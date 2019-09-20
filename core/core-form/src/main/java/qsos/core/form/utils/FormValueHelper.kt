package qsos.core.form.utils

import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.FormValueOfLocation
import qsos.core.form.db.entity.FormValueOfTime

/**
 * @author : 华清松
 * 表单转换帮助类
 */
object FormValueHelper {

    /**获取表单项的值*/
    object GetValue {
        /**输入值*/
        fun input(formItem: FormItem): String {
            return formItem.formItemValue!!.value?.text?.content ?: ""
        }

        /**单选值*/
        fun singleChose(formItem: FormItem): String? {
            var value: String? = null
            for (chose in formItem.formItemValue!!.values!!) {
                if (chose.check!!.ckChecked) {
                    value = chose.check!!.ckValue!!
                    break
                }
            }
            return value
        }

        /**多选值*/
        fun multiChose(formItem: FormItem): List<String> {
            val values = arrayListOf<String>()
            formItem.formItemValue!!.values!!.forEach {
                if (it.check!!.ckChecked) {
                    values.add(it.check!!.ckValue!!)
                }
            }
            return values
        }

        /**时间值*/
        fun time(formItem: FormItem): FormValueOfTime? {
            return formItem.formItemValue!!.value?.time
        }

        /**人员ID列表*/
        fun userIds(formItem: FormItem): List<String> {
            val values = arrayListOf<String>()
            formItem.formItemValue!!.values!!.forEach {
                values.add(it.user!!.userId!!)
            }
            return values
        }

        /**文件ID列表*/
        fun fileIds(formItem: FormItem): List<String> {
            val values = arrayListOf<String>()
            formItem.formItemValue!!.values!!.forEach {
                values.add(it.file!!.fileId!!)
            }
            return values
        }

        /**位置值*/
        fun location(formItem: FormItem): FormValueOfLocation? {
            return formItem.formItemValue!!.value?.location
        }
    }

}