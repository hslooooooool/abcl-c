package qsos.core.form.db.entity

import androidx.room.*

/**
 * @author : 华清松
 * 表单项值实体类
 * @param id 表单项值ID
 *
 * @param formItemId 外键-表单项ID
 *
 * @param limitType 此值类型限制
 * @param limitEdit 此值是否可编辑
 */
@Entity(tableName = "formItemValue",
        foreignKeys = [
            ForeignKey(entity = FormItem::class, parentColumns = ["id"], childColumns = ["formItemId"], onDelete = ForeignKey.CASCADE)
        ],
        indices = [
            Index(value = ["id"], unique = true)
        ]
)
data class Value(
        @PrimaryKey(autoGenerate = true)
        var id: Long? = null,
        var formItemId: Long? = null,
        var limitType: String? = null,
        var limitEdit: Boolean = false

) {
    /**输入*/
    @Embedded
    var text: FormValueOfText? = null
    /**时间*/
    @Embedded
    var time: FormValueOfTime? = FormValueOfTime()
    /**选择*/
    @Embedded
    var check: FormValueOfCheck? = FormValueOfCheck()
    /**人员*/
    @Embedded
    var user: FormValueOfUser? = FormValueOfUser()
    /**附件*/
    @Embedded
    var file: FormValueOfFile? = FormValueOfFile()
    /**位置*/
    @Embedded
    var location: FormValueOfLocation? = FormValueOfLocation()

    companion object {
        fun newCheck(check: FormValueOfCheck): Value {
            val v = Value()
            v.check = check
            return v
        }

        fun newUser(formItemId: Long, user: FormValueOfUser): Value {
            val v = Value()
            v.formItemId = formItemId
            v.user = user
            return v
        }
    }

}