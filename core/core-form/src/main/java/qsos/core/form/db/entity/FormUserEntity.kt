package qsos.core.form.db.entity

/**
 * @author : 华清松
 * @description : 选择用户实体类
 */
class FormUserEntity {
    constructor(name: String, phone: String?, avatar: String?) {
        this.userName = name
        this.userPhone = phone
        this.userAvatar = avatar
    }

    constructor(formItemId: Long?, userId: String?, name: String?, avatar: String?, phone: String?, cb: Boolean, limit: Boolean) {
        this.formItemId = formItemId
        this.userId = userId
        this.userName = name
        this.userPhone = phone
        this.userAvatar = avatar
        this.userCb = cb
        this.userLimit = limit
    }

    var id: Int? = null
    var userName: String? = null
    var userPhone: String? = ""
    var userAvatar: String? = null
    var userId: String? = ""
    var userCb: Boolean = false
    var userLimit: Boolean = false
    /*外键*/
    var formItemId: Long? = null

}