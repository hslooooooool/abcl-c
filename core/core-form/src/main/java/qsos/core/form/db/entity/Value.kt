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
 * @param position 此值下标位置，用于排序
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
        var limitEdit: Boolean = false,
        var position: Int = 0

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
        fun newText(text: FormValueOfText, formItemId: Long? = null): Value {
            val v = Value()
            v.formItemId = formItemId
            v.text = text
            return v
        }

        fun newTime(time: FormValueOfTime, formItemId: Long? = null): Value {
            val v = Value()
            v.formItemId = formItemId
            v.time = time
            return v
        }

        fun newCheck(check: FormValueOfCheck, formItemId: Long? = null): Value {
            val v = Value()
            v.formItemId = formItemId
            v.check = check
            return v
        }

        fun newUser(user: FormValueOfUser, formItemId: Long? = null): Value {
            val v = Value()
            v.formItemId = formItemId
            v.user = user
            return v
        }

        fun newFile(file: FormValueOfFile, formItemId: Long? = null): Value {
            val v = Value()
            v.formItemId = formItemId
            v.file = file
            return v
        }

        fun newLocation(location: FormValueOfLocation, formItemId: Long? = null): Value {
            val v = Value()
            v.formItemId = formItemId
            v.location = location
            return v
        }

    }

}