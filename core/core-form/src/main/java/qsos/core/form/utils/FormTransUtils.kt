package qsos.core.form.utils

import qsos.core.form.db.entity.*

/**表单转换帮助类*/
object FormTransUtils {

    /**指令反馈表单*/
    fun getTestOrderFeedbackData(): FormEntity {
        val form = FormEntity()
        form.notice = "指令反馈表单"
        form.submitName = "提交反馈"
        form.title = "指令反馈"
        val formItemList = arrayListOf<FormItem>()
        /**反馈状态*/
        val formItem1 = FormItem()
        formItem1.valueType = FormItemType.CHOOSE.tag
        formItem1.notice = "请选择指令状态"
        formItem1.title = "反馈状态"
        formItem1.require = true
        val values1 = arrayListOf<Value>()
        values1.add(Value.newCheck(FormValueOfCheck("1", "已抓捕", "已抓捕", true)))
        values1.add(Value.newCheck(FormValueOfCheck("2", "已盘查", "已盘查", false)))
        formItem1.formItemValue = FormItemValue(1, 1, null, values1)
        formItemList.add(formItem1)

        /**反馈内容*/
        val formItem2 = FormItem()
        formItem2.valueType = FormItemType.INPUT.tag
        formItem2.notice = "请输入您的反馈信息"
        formItem2.title = "反馈内容"
        formItem2.require = true
        val itemValue2 = FormItemValue()
        itemValue2.limitMin = 5
        itemValue2.limitMax = 500
        val values2 = arrayListOf<Value>()
        val value2 = Value()
        value2.text = FormValueOfText("")
        values2.add(value2)
        itemValue2.values = values2
        formItem2.formItemValue = itemValue2
        formItemList.add(formItem2)

        /**反馈附件*/
        val formItem3 = FormItem()
        formItem3.valueType = FormItemType.FILE.tag
        formItem3.notice = "请上传指令附件"
        formItem3.title = "反馈附件"
        formItem3.require = false
        formItem3.formItemValue = FormItemValue(null, 9, "image", null)
        formItemList.add(formItem3)
        form.formItem = formItemList
        return form
    }

    /**添加布控表单*/
    fun getTestExecuteData(): FormEntity {
        val form = FormEntity()
        form.notice = "添加布控表单"
        form.submitName = "确认添加"
        form.title = "添加布控"
        val formItemList = arrayListOf<FormItem>()

        /**设置图片*/
        val itemHead = FormItem()
        itemHead.valueType = FormItemType.FILE.tag
        itemHead.notice = "请上传头像图片"
        itemHead.title = "用户头像"
        itemHead.require = true
        itemHead.formItemValue = FormItemValue(1, 1, "image", arrayListOf())
        formItemList.add(itemHead)
        /**输入姓名*/
        val itemName = FormItem()
        itemName.valueType = FormItemType.INPUT.tag
        itemName.notice = "请输入姓名"
        itemName.title = "姓名"
        itemName.require = true
        val names = arrayListOf<Value>()
        val name = Value()
        name.text = FormValueOfText("")
        names.add(name)
        itemName.formItemValue = FormItemValue(1, 20, null, names)
        formItemList.add(itemName)
        /**输入身份证号*/
        val itemIDCard = FormItem()
        itemIDCard.valueType = FormItemType.INPUT.tag
        itemIDCard.notice = "请输入身份证号"
        itemIDCard.title = "身份证号"
        itemIDCard.require = true
        val idCards = arrayListOf<Value>()
        val idCard = Value()
        idCard.text = FormValueOfText()
        idCards.add(idCard)
        itemIDCard.formItemValue = FormItemValue(18, 18, "ID_CARD", idCards)
        formItemList.add(itemIDCard)
        /**输入户籍地址*/
        val itemNativeAddress = FormItem()
        itemNativeAddress.valueType = FormItemType.INPUT.tag
        itemNativeAddress.notice = "请输入户籍地址"
        itemNativeAddress.title = "户籍地址"
        itemNativeAddress.require = true
        val nativeAddresses = arrayListOf<Value>()
        val nativeAddress = Value()
        nativeAddress.text = FormValueOfText()
        nativeAddresses.add(nativeAddress)
        itemNativeAddress.formItemValue = FormItemValue(2, 50, null, nativeAddresses)
        formItemList.add(itemNativeAddress)
        /**输入现居住地址*/
        val itemNowAddress = FormItem()
        itemNowAddress.valueType = FormItemType.INPUT.tag
        itemNowAddress.notice = "请输入现居地址"
        itemNowAddress.title = "现居地址"
        itemNowAddress.require = true
        val nowAddresses = arrayListOf<Value>()
        val nowAddress = Value()
        nowAddress.text = FormValueOfText()
        nowAddresses.add(nowAddress)
        itemNowAddress.formItemValue = FormItemValue(2, 50, null, nowAddresses)
        formItemList.add(itemNowAddress)
        /**单选-性别*/
        val itemSex = FormItem()
        itemSex.valueType = FormItemType.CHOOSE.tag
        itemSex.notice = "请选择性别"
        itemSex.title = "性别"
        itemSex.require = false
        val sexList = arrayListOf<Value>()
        itemSex.formItemValue = FormItemValue(1, 1, null, sexList)
        formItemList.add(itemSex)
        /**单选-民族*/
        val itemNation = FormItem()
        itemNation.valueType = FormItemType.CHOOSE.tag
        itemNation.notice = "请选择民族"
        itemNation.title = "民族"
        itemNation.require = true
        val nations = arrayListOf<Value>()
        itemNation.formItemValue = FormItemValue(1, 1, null, nations)
        formItemList.add(itemNation)
        /**单选-人员类型*/
        val itemUserType = FormItem()
        itemUserType.valueType = FormItemType.CHOOSE.tag
        itemUserType.notice = "请选择人员类型"
        itemUserType.title = "人员类型"
        itemUserType.require = true
        val userTypes = arrayListOf<Value>()
        itemUserType.formItemValue = FormItemValue(1, null, null, userTypes)
        formItemList.add(itemUserType)
        /**单选-布控级别*/
        val itemExecuteLevel = FormItem()
        itemExecuteLevel.valueType = FormItemType.CHOOSE.tag
        itemExecuteLevel.notice = "请选择布控级别"
        itemExecuteLevel.title = "布控级别"
        itemExecuteLevel.require = true
        val executeLevels = arrayListOf<Value>()
        itemExecuteLevel.formItemValue = FormItemValue(1, 1, null, executeLevels)
        formItemList.add(itemExecuteLevel)
        /**单选-处置方式*/
        val itemExecuteDeal = FormItem()
        itemExecuteDeal.valueType = FormItemType.CHOOSE.tag
        itemExecuteDeal.notice = "请选择处置方式"
        itemExecuteDeal.title = "处置方式"
        itemExecuteDeal.require = true
        val itemExecuteDeals = arrayListOf<Value>()
        itemExecuteDeal.formItemValue = FormItemValue(1, 1, null, itemExecuteDeals)
        formItemList.add(itemExecuteDeal)
        /**通知人*/
        val formItem1 = FormItem()
        formItem1.valueType = FormItemType.USER.tag
        formItem1.notice = "点击选择通知人员"
        formItem1.title = "通知人员"
        formItem1.require = false
        formItem1.formItemValue = FormItemValue(0, null, "execute_toast", arrayListOf())
        // todo 后期实现 formItemList.add(formItem1)

        form.formItem = formItemList
        return form
    }

    /**获取输入内容*/
    private fun getFormInput(formItem: FormItem): String {
        return formItem.formItemValue!!.value?.text?.content ?: ""
    }

    /**选项校验-单选*/
    private fun getFormSingleChose(formItem: FormItem): String {
        var chose = ""
        val choseList = formItem.formItemValue!!.values!!
        choseList.forEach {
            if (it.check!!.ckChecked) {
                chose = it.check!!.ckValue!!
            }
        }
        return chose
    }

    /**选项校验-多选*/
    private fun getFormMultiChose(formItem: FormItem): String {
        var chose = ""
        val choseList = formItem.formItemValue!!.values!!
        choseList.forEach {
            if (it.check!!.ckChecked) {
                chose += it.check!!.ckValue!! + ","
            }
        }
        return chose
    }

    /**时间校验*/
    private fun time(formItem: FormItem): FormItem {

        return FormItem()
    }

    /**人员校验*/
    private fun user(formItem: FormItem): FormItem {

        return FormItem()
    }

    /**获取文件ID列表*/
    private fun getFormFileIds(formItem: FormItem): String {
        var ids = ""
        val formValues = formItem.formItemValue!!.values ?: arrayListOf()
        formValues.forEachIndexed { index, value ->
            ids = when (index) {
                0 -> {
                    value.file!!.fileId ?: ""
                }
                else -> {
                    ids + "," + value.file!!.fileId
                }
            }
        }
        return ids
    }

    /**位置校验*/
    private fun location(formItem: FormItem): FormItem {

        return FormItem()
    }
}