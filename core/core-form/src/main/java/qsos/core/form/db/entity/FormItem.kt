package qsos.core.form.db.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

/**
 * @author : 华清松
 * 通用表单子项实体类
 */
@Entity(tableName = "form_item",
        foreignKeys = [ForeignKey(
                entity = FormEntity::class,
                parentColumns = ["id"],
                childColumns = ["form_id"],
                onDelete = CASCADE)],
        indices = [Index(value = ["id"], unique = true)]
)
class FormItem {
    constructor()
    constructor(form_item_type: Int, form_item_key: String, form_item_required: Boolean, form_item_value: FormItemValue) {
        this.form_item_type = form_item_type
        this.form_item_key = form_item_key
        this.form_item_required = form_item_required
        this.form_item_value = form_item_value
    }

    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

    /*外键*/
    var form_id: Long? = null
    /*表单项顺序*/
    var form_order: Int? = 0
    /*表单项是否显示*/
    var form_visible: Boolean = true
    /*表单项值类型，0：文本展示；1：输入；2：选项；3：时间；4：人员；5：附件；6：位置；*/
    var form_item_type: Int? = null
    /*表单项状态，0：不可编辑；1：可编辑*/
    var form_item_status: Int? = 1
    /*表单项名称*/
    var form_item_key: String? = null
    /*表单项提示*/
    var form_item_hint: String? = null
    /*表单项是否必填*/
    var form_item_required: Boolean = false

    @Embedded
    var form_item_value: FormItemValue? = null
}

enum class FormItemType(val key: String, val tag: Int) {
    TEXT("文本展示", 0),
    INPUT("文本输入", 1),
    CHOOSE("选项", 2),
    TIME("时间", 3),
    USER("用户", 4),
    FILE("附件", 5),
    LOCATION("位置", 6)
}