package qsos.core.form.db.entity

import androidx.room.Ignore

/**
 * @author : 华清松
 * @description : 通用表单子项值集实体类
 */
class FormItemValue {
    constructor()

    constructor(limit_max: Int) {
        this.limit_max = limit_max
    }

    constructor(limit_min: Int? = 0, limit_max: Int? = 0, limit_type: String?, values: ArrayList<Value>?) {
        this.limit_min = limit_min
        this.limit_max = limit_max
        this.limit_type = limit_type
        this.values = values
    }

    /**值的最小数量*/
    var limit_min: Int? = 0
    /**值的最大数量*/
    var limit_max: Int? = 0
    /**值限制，选用户的时候，可能为角色,输入的时候是类型*/
    var limit_type: String? = ""

    @Ignore
    var values: ArrayList<Value>? = arrayListOf()
        get() = if (field == null) arrayListOf() else field
}