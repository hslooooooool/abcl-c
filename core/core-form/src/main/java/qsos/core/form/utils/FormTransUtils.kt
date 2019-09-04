package qsos.core.form.utils

import qsos.core.form.db.entity.*

/**表单转换帮助类*/
object FormTransUtils {

    /**指令反馈表单*/
    fun getTestOrderFeedbackData(): FormEntity {
        val form = FormEntity()
        form.desc = "指令反馈表单"
        form.submit_name = "提交反馈"
        form.title = "指令反馈"
        val formItemList = arrayListOf<FormItem>()
        /**反馈状态*/
        val formItem1 = FormItem()
        formItem1.form_item_type = FormItemType.CHOOSE.tag
        formItem1.form_item_hint = "请选择指令状态"
        formItem1.form_item_key = "反馈状态"
        formItem1.form_item_required = true
        val values1 = arrayListOf<Value>()
        values1.add(Value.newCheck("1", "已抓捕", "已抓捕", true))
        values1.add(Value.newCheck("2", "已盘查", "已盘查", false))
        values1.add(Value.newCheck("3", "已移动", "已移动", false))
        formItem1.form_item_value = FormItemValue(1, 1, null, values1)
        formItemList.add(formItem1)

        /**反馈内容*/
        val formItem2 = FormItem()
        formItem2.form_item_type = FormItemType.INPUT.tag
        formItem2.form_item_hint = "请输入您的反馈信息"
        formItem2.form_item_key = "反馈内容"
        formItem2.form_item_required = true
        val itemValue2 = FormItemValue()
        itemValue2.limit_min = 5
        itemValue2.limit_max = 500
        val values2 = arrayListOf<Value>()
        val value2 = Value()
        value2.input_value = ""
        values2.add(value2)
        itemValue2.values = values2
        formItem2.form_item_value = itemValue2
        formItemList.add(formItem2)

        /**反馈附件*/
        val formItem3 = FormItem()
        formItem3.form_item_type = FormItemType.FILE.tag
        formItem3.form_item_hint = "请上传指令附件"
        formItem3.form_item_key = "反馈附件"
        formItem3.form_item_required = false
        formItem3.form_item_value = FormItemValue(null, 9, "image", null)
        formItemList.add(formItem3)
        form.form_item = formItemList
        return form
    }

    /**添加布控表单*/
    fun getTestExecuteData(): FormEntity {
        val form = FormEntity()
        form.desc = "添加布控表单"
        form.submit_name = "确认添加"
        form.title = "添加布控"
        val formItemList = arrayListOf<FormItem>()

        /**设置图片*/
        val itemHead = FormItem()
        itemHead.form_item_type = FormItemType.FILE.tag
        itemHead.form_item_hint = "请上传头像图片"
        itemHead.form_item_key = "用户头像"
        itemHead.form_item_required = true
        itemHead.form_item_value = FormItemValue(1, 1, "image", arrayListOf())
        formItemList.add(itemHead)
        /**输入姓名*/
        val itemName = FormItem()
        itemName.form_item_type = FormItemType.INPUT.tag
        itemName.form_item_hint = "请输入姓名"
        itemName.form_item_key = "姓名"
        itemName.form_item_required = true
        val names = arrayListOf<Value>()
        val name = Value()
        name.input_value = ""
        names.add(name)
        itemName.form_item_value = FormItemValue(1, 20, null, names)
        formItemList.add(itemName)
        /**输入身份证号*/
        val itemIDCard = FormItem()
        itemIDCard.form_item_type = FormItemType.INPUT.tag
        itemIDCard.form_item_hint = "请输入身份证号"
        itemIDCard.form_item_key = "身份证号"
        itemIDCard.form_item_required = true
        val idCards = arrayListOf<Value>()
        val idCard = Value()
        idCard.input_value = ""
        idCards.add(idCard)
        itemIDCard.form_item_value = FormItemValue(18, 18, "ID_CARD", idCards)
        formItemList.add(itemIDCard)
        /**输入户籍地址*/
        val itemNativeAddress = FormItem()
        itemNativeAddress.form_item_type = FormItemType.INPUT.tag
        itemNativeAddress.form_item_hint = "请输入户籍地址"
        itemNativeAddress.form_item_key = "户籍地址"
        itemNativeAddress.form_item_required = true
        val nativeAddresses = arrayListOf<Value>()
        val nativeAddress = Value()
        nativeAddress.input_value = ""
        nativeAddresses.add(nativeAddress)
        itemNativeAddress.form_item_value = FormItemValue(2, 50, null, nativeAddresses)
        formItemList.add(itemNativeAddress)
        /**输入现居住地址*/
        val itemNowAddress = FormItem()
        itemNowAddress.form_item_type = FormItemType.INPUT.tag
        itemNowAddress.form_item_hint = "请输入现居地址"
        itemNowAddress.form_item_key = "现居地址"
        itemNowAddress.form_item_required = true
        val nowAddresses = arrayListOf<Value>()
        val nowAddress = Value()
        nowAddress.input_value = ""
        nowAddresses.add(nowAddress)
        itemNowAddress.form_item_value = FormItemValue(2, 50, null, nowAddresses)
        formItemList.add(itemNowAddress)
        /**单选-性别*/
        val itemSex = FormItem()
        itemSex.form_item_type = FormItemType.CHOOSE.tag
        itemSex.form_item_hint = "请选择性别"
        itemSex.form_item_key = "性别"
        itemSex.form_item_required = false
        val sexList = arrayListOf<Value>()
        itemSex.form_item_value = FormItemValue(1, 1, null, sexList)
        formItemList.add(itemSex)
        /**单选-民族*/
        val itemNation = FormItem()
        itemNation.form_item_type = FormItemType.CHOOSE.tag
        itemNation.form_item_hint = "请选择民族"
        itemNation.form_item_key = "民族"
        itemNation.form_item_required = true
        val nations = arrayListOf<Value>()
        itemNation.form_item_value = FormItemValue(1, 1, null, nations)
        formItemList.add(itemNation)
        /**单选-人员类型*/
        val itemUserType = FormItem()
        itemUserType.form_item_type = FormItemType.CHOOSE.tag
        itemUserType.form_item_hint = "请选择人员类型"
        itemUserType.form_item_key = "人员类型"
        itemUserType.form_item_required = true
        val userTypes = arrayListOf<Value>()
        itemUserType.form_item_value = FormItemValue(1, null, null, userTypes)
        formItemList.add(itemUserType)
        /**单选-布控级别*/
        val itemExecuteLevel = FormItem()
        itemExecuteLevel.form_item_type = FormItemType.CHOOSE.tag
        itemExecuteLevel.form_item_hint = "请选择布控级别"
        itemExecuteLevel.form_item_key = "布控级别"
        itemExecuteLevel.form_item_required = true
        val executeLevels = arrayListOf<Value>()
        itemExecuteLevel.form_item_value = FormItemValue(1, 1, null, executeLevels)
        formItemList.add(itemExecuteLevel)
        /**单选-处置方式*/
        val itemExecuteDeal = FormItem()
        itemExecuteDeal.form_item_type = FormItemType.CHOOSE.tag
        itemExecuteDeal.form_item_hint = "请选择处置方式"
        itemExecuteDeal.form_item_key = "处置方式"
        itemExecuteDeal.form_item_required = true
        val itemExecuteDeals = arrayListOf<Value>()
        itemExecuteDeal.form_item_value = FormItemValue(1, 1, null, itemExecuteDeals)
        formItemList.add(itemExecuteDeal)
        /**通知人*/
        val formItem1 = FormItem()
        formItem1.form_item_type = FormItemType.USER.tag
        formItem1.form_item_hint = "点击选择通知人员"
        formItem1.form_item_key = "通知人员"
        formItem1.form_item_required = false
        formItem1.form_item_value = FormItemValue(0, null, "execute_toast", arrayListOf())
        // todo 后期实现 formItemList.add(formItem1)

        form.form_item = formItemList
        return form
    }

    /**获取输入内容*/
    private fun getFormInput(formItem: FormItem): String {
        return formItem.form_item_value!!.values!![0].input_value ?: ""
    }

    /**选项校验*/
    private fun getFormSingleChose(formItem: FormItem): String {
        var chose = ""
        val choseList = formItem.form_item_value!!.values!!
        choseList.forEach {
            if (it.ck_check) {
                chose = it.ck_value!!
            }
        }
        return chose
    }

    /**选项校验*/
    private fun getFormMultiChose(formItem: FormItem): String {
        var chose = ""
        val choseList = formItem.form_item_value!!.values!!
        choseList.forEach {
            if (it.ck_check) {
                chose += it.ck_value!! + ","
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
        val formValues = formItem.form_item_value!!.values ?: arrayListOf()
        formValues.forEachIndexed { index, value ->
            ids = when (index) {
                0 -> {
                    value.file_id ?: ""
                }
                else -> {
                    ids + "," + value.file_id
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