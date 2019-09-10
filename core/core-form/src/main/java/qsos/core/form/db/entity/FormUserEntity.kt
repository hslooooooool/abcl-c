package qsos.core.form.db.entity

/**
 * @author : 华清松
 * 选择用户实体类
 */
class FormUserEntity {
    constructor()
    constructor(name: String, phone: String?, avatar: String?) {
        this.userName = name
        this.userPhone = phone
        this.userAvatar = avatar
    }

    var id: Long? = null
    var userName: String? = null
    var userPhone: String? = ""
    var userAvatar: String? = null
    var userId: String? = ""
    var userCb: Boolean = false
    var userLimit: Boolean = false
    /*外键*/
    var formItemId: Long? = null

}