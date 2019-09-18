package qsos.core.form.db.entity

import androidx.room.Ignore

/**
 * @author : 华清松
 * 表单项值限制条件实体类
 */
class FormItemValue {
    /**值的最小数量*/
    var limitMin: Int? = 0
    /**值的最大数量*/
    var limitMax: Int? = 0
    /**值限制。选用户的时候，可能为角色,输入的时候是类型，
     * 附件规定为:
     * @see FormFileType.CAMERA.key
     * @see FormFileType.ALBUM.key
     * @see FormFileType.VIDEO.key
     * @see FormFileType.AUDIO.key
     * @see FormFileType.FILE.key
     * 多种类型 ; 分割，不传则不限制
     */
    var limitType: String? = ""

    @Ignore
    var limitTypeList: List<String>? = null
        get() {
            if (field != null) return field
            return limitType?.split(";") ?: arrayListOf()
        }

    @Ignore
    var values: ArrayList<Value>? = arrayListOf()
        get() = if (field == null) arrayListOf() else field

    @Ignore
    var value: Value? = null
        get() = field ?: if (values.isNullOrEmpty()) null else values!![0]

    constructor()
    /**
     *
     * @param limitMin 值的最小数量
     * @param limitMax 值的最大数量
     * @param limitType 值限制。选用户的时候，可能为角色,输入的时候是类型
     * */
    constructor(limitMin: Int? = 0, limitMax: Int? = 0, limitType: String? = null, values: ArrayList<Value>? = null) {
        this.limitMin = limitMin
        this.limitMax = limitMax
        this.limitType = limitType
        this.values = values
    }

}