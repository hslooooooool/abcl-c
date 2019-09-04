package qsos.core.form.db.entity

/**
 * @author : 华清松
 * @description : 表单业务类型
 */
enum class FormType(key: String) {

    ADD_NOTICE("添加公告"),
    ADD_EXECUTE("添加布控");

    val title = key

    companion object {

        fun getEnum(key: String?): FormType? {
            var mType: FormType? = null
            values().forEach { type ->
                if (type.title == key) {
                    mType = type
                }
            }
            return mType
        }
    }
}