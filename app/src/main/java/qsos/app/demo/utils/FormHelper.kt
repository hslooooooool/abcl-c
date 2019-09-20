package qsos.app.demo.utils

import qsos.core.form.db.entity.*
import java.util.*

/**表单转换帮助类*/
object FormHelper {

    /**创建一个表单Demo*/
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
                            Value().newCheck(FormValueOfCheck("1", "做得好", "YES", true)),
                            Value().newCheck(FormValueOfCheck("2", "做得差", "NO", false))
                    ))
            ))
            /**反馈内容*/
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.INPUT.tag, notice = "请输入您的反馈信息",
                            title = "反馈内容", require = true
                    ),
                    value = FormItemValue(limitMin = 5, limitMax = 500, values = arrayListOf(
                            Value().newText(FormValueOfText(""))
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
                    value = FormItemValue(limitMin = 1, limitMax = 1, limitType = ".png;.jpg;.jpeg", values = arrayListOf())
            ))
            /**输入姓名*/
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.INPUT.tag, notice = "请输入姓名",
                            title = "姓名", require = true
                    ),
                    value = FormItemValue(limitMin = 1, limitMax = 10, values = arrayListOf(
                            Value().newText(FormValueOfText())
                    ))
            ))
            /**输入身份证号*/
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.INPUT.tag, notice = "请输入身份证号",
                            title = "身份证号", require = true
                    ),
                    value = FormItemValue(limitMin = 18, limitMax = 18, limitType = "ID_CARD", values = arrayListOf(
                            Value().newText(FormValueOfText())
                    ))
            ))
            /**输入居住地址*/
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.INPUT.tag, notice = "请输入居住地址",
                            title = "居住地址", require = true
                    ),
                    value = FormItemValue(limitMin = 2, limitMax = 50, values = arrayListOf(
                            Value().newText(FormValueOfText())
                    ))
            ))
            /**居住地址位置*/
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.LOCATION.tag, notice = "请设置居住定位",
                            title = "居住定位", require = false
                    ),
                    value = FormItemValue(limitMin = 1, limitMax = 1, values = arrayListOf(
                            Value().newLocation(FormValueOfLocation())
                    ))
            ))
            /**单选-性别*/
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.CHOOSE.tag, notice = "请选择性别",
                            title = "性别", require = true
                    ),
                    value = FormItemValue(limitMin = 1, limitMax = 1, values = arrayListOf(
                            Value().newCheck(FormValueOfCheck("1", "男", "man", true)),
                            Value().newCheck(FormValueOfCheck("2", "女", "woman"))
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
                            Value().newTime(FormValueOfTime(timeLimitMin = c.time.time, timeLimitMax = now.time))
                    ))
            ))
            /**多选-爱好*/
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.CHOOSE.tag, notice = "请选择爱好",
                            title = "爱好", require = false
                    ),
                    value = FormItemValue(limitMin = 1, values = arrayListOf(
                            Value().newCheck(FormValueOfCheck("1", "篮球", "A")),
                            Value().newCheck(FormValueOfCheck("2", "足球", "B")),
                            Value().newCheck(FormValueOfCheck("3", "乒乓球", "C")),
                            Value().newCheck(FormValueOfCheck("4", "羽毛球", "D")),
                            Value().newCheck(FormValueOfCheck("5", "排球", "E"))
                    ))
            ))
            /**人员-推荐人*/
            val users = arrayListOf<Value>()
            for (i in 0..2) {
                // 案例将初始化两个不可撤销的人员，在固定的审批流程下非常有用
                users.add(Value(limitEdit = true, limitType = "role-manager", position = i).newUser(FormValueOfUser(userId = "000$i", userName = "用户$i", userDesc = "1822755555$i", userAvatar = "http://www.qsos.vip/upload/2018/11/ic_launcher20181225044818498.png")))
            }
            formItemList.add(FormItem.newFormItemValue(
                    item = FormItem(
                            valueType = FormItemType.USER.tag, notice = "请选择推荐人",
                            title = "推荐人", require = false
                    ),
                    value = FormItemValue(limitMin = 1, limitMax = 5, limitType = "role-manager", values = users)
            ))

            return FormEntity.newFormItems(form = form, formItems = formItemList)
        }
    }

}