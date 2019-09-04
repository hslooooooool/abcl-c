package qsos.core.form.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * @author : 华清松
 * @description : 通用表单子项值实体类
 */
@Entity(tableName = "form_item_value",
        foreignKeys = [
            ForeignKey(
                    entity = FormItem::class,
                    parentColumns = ["id"],
                    childColumns = ["form_item_id"],
                    onDelete = ForeignKey.CASCADE)
        ],
        indices = [Index(value = ["id"], unique = true)]
)
class Value {
    constructor()

    /**用户*/
    constructor(form_item_id: Long?, user_name: String?, user_phone: String?, user_header: String?, user_id: String?) {
        this.form_item_id = form_item_id
        this.user_name = user_name
        this.user_phone = user_phone
        this.user_header = user_header
        this.user_id = user_id
    }

    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

    /*外键*/
    var form_item_id: Long? = null

    /*输入*/
    var input_value: String? = null
    /*选择*/
    var ck_id: String? = null
    var ck_name: String? = null
    var ck_value: String? = null
    var ck_check: Boolean = false
    /*时间*/
    var time: Long = 0L
    /*人员*/
    var user_name: String? = null
    var user_phone: String? = ""
    var user_header: String? = null
    var user_id: String? = ""
    /*附件*/
    var file_id: String? = ""
    var file_name: String? = null
    var file_type: String? = null
    var file_path: String? = null
    var file_url: String? = null
    /*位置*/
    var loc_name: String? = null
    var loc_x: Double? = null
    var loc_y: Double? = null
    /*此值类型限制*/
    var limit_type: String? = null
    /*是否可编辑此值*/
    var limit_edit: Boolean = false

    companion object {
        fun newCheck(
                ck_id: String?,
                ck_name: String?,
                ck_value: String?,
                ck_check: Boolean? = false
        ): Value {
            val v = Value()
            v.ck_id = ck_id
            v.ck_name = ck_name
            v.ck_value = ck_value
            v.ck_check = ck_check ?: false
            return v
        }

        fun newFile(
                form_item_id: Long,
                file_id: String? = "",
                file_name: String? = null,
                file_type: String? = null,
                file_url: String? = null
        ): Value {
            val v = Value()
            v.form_item_id = form_item_id
            v.file_id = file_id
            v.file_name = file_name
            v.file_type = file_type
            v.file_url = file_url
            return v
        }
    }

}