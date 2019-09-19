package qsos.core.form.utils

import qsos.core.form.db.entity.*
import java.util.*

/**表单转换帮助类*/
object FormHelper {

    /**创建一个表单*/
    object Create {
        /**反馈表单*/
        fun feedbackForm(): FormEntity {
            val form = FormEntity(notice = "指令反馈表单", submitName = "提交反馈", title = "指令反馈")
            val formItemList = arrayListOf<FormItem>()
            /**反馈状态*/
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.CHOOSE.tag, notice = "请选择指令状态",
                            title = "反馈状态", require = true
                    ),
                    value = FormItemValue(limitMin = 1, limitMax = 1, values = arrayListOf(
                            Value.newCheck(FormValueOfCheck("1", "做得好", "YES", true)),
                            Value.newCheck(FormValueOfCheck("2", "做得差", "NO", false))
                    ))
            ))

            /**反馈内容*/
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.INPUT.tag, notice = "请输入您的反馈信息",
                            title = "反馈内容", require = true
                    ),
                    value = FormItemValue(limitMin = 5, limitMax = 500, values = arrayListOf(
                            Value.newText(FormValueOfText(""))
                    ))
            ))

            /**反馈附件*/
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.FILE.tag, notice = "请上传指令附件",
                            title = "反馈附件", require = false
                    ),
                    value = FormItemValue(limitMax = 9, limitType = "", values = null)
            ))

            return FormEntity.newFormItems(form = form, formItems = formItemList)
        }

        /**用户表单*/
        fun userInfoForm(): FormEntity {
            val form = FormEntity(notice = "添加用户表单", submitName = "保存", title = "添加用户")
            val formItemList = arrayListOf<FormItem>()

            /**用户头像*/
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.FILE.tag, notice = "请上传头像图片",
                            title = "用户头像", require = true
                    ),
                    value = FormItemValue(limitMin = 1, limitMax = 1, limitType = "image", values = arrayListOf())
            ))
            /**输入姓名*/
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.INPUT.tag, notice = "请输入姓名",
                            title = "姓名", require = true
                    ),
                    value = FormItemValue(limitMin = 1, limitMax = 10, values = arrayListOf(
                            Value.newText(FormValueOfText())
                    ))
            ))
            /**输入身份证号*/
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.INPUT.tag, notice = "请输入身份证号",
                            title = "身份证号", require = true
                    ),
                    value = FormItemValue(limitMin = 18, limitMax = 18, limitType = "ID_CARD", values = arrayListOf(
                            Value.newText(FormValueOfText())
                    ))
            ))
            /**输入居住地址*/
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.INPUT.tag, notice = "请输入居住地址",
                            title = "居住地址", require = true
                    ),
                    value = FormItemValue(limitMin = 2, limitMax = 50, values = arrayListOf(
                            Value.newText(FormValueOfText())
                    ))
            ))
            /**单选-性别*/
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.CHOOSE.tag, notice = "请选择性别",
                            title = "性别", require = true
                    ),
                    value = FormItemValue(limitMin = 1, limitMax = 1, values = arrayListOf(
                            Value.newCheck(FormValueOfCheck("1", "男", "man", true)),
                            Value.newCheck(FormValueOfCheck("2", "女", "woman"))
                    ))
            ))
            /**出身日期*/
            val c = Calendar.getInstance()
            val now = Date()
            c.time = now
            c.add(Calendar.YEAR, -100)// 当前年往前100年
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.TIME.tag, notice = "请设置出身日期",
                            title = "出身日期", require = true
                    ),
                    value = FormItemValue(limitMin = 1, limitMax = 1, values = arrayListOf(
                            Value.newTime(FormValueOfTime(timeLimitMin = c.time.time, timeLimitMax = now.time))
                    ))
            ))
            /**多选-爱好*/
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.CHOOSE.tag, notice = "请选择爱好",
                            title = "爱好", require = false
                    ),
                    value = FormItemValue(limitMin = 1, values = arrayListOf(
                            Value.newCheck(FormValueOfCheck("1", "篮球", "A")),
                            Value.newCheck(FormValueOfCheck("2", "足球", "B")),
                            Value.newCheck(FormValueOfCheck("3", "乒乓球", "C")),
                            Value.newCheck(FormValueOfCheck("4", "羽毛球", "D")),
                            Value.newCheck(FormValueOfCheck("5", "排球", "E"))
                    ))
            ))

            return FormEntity.newFormItems(form = form, formItems = formItemList)
        }
    }

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