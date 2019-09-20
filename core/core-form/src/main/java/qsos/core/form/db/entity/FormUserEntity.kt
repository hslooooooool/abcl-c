package qsos.core.form.db.entity

/**
 * @author : 华清松
 * 选择用户实体类
 * @param id 表单项值ID
 * @param formItemId 表单项ID
 * @param userId 用户ID
 * @param userName 用户名称
 * @param userDesc 用户描述，推荐为手机号
 * @param userAvatar 用户头像链接，http://qsos.vip/img/logo.png
 * @param limitEdit 用户是否可操作
 * @param userCb 用户是否被选中
 */
data class FormUserEntity(
        var id: Long? = null,
        var formItemId: Long? = null,
        var userId: String? = null,
        var userName: String? = null,
        var userDesc: String? = null,
        var userAvatar: String? = null,
        var limitEdit: Boolean = false,
        var userCb: Boolean = false
) {
    fun transValueToThis(v: Value): FormUserEntity {
        id = v.id
        formItemId = v.formItemId
        userId = v.user!!.userId
        userName = v.user!!.userName
        userDesc = v.user!!.userDesc
        userAvatar = v.user!!.userAvatar
        limitEdit = v.limitEdit
        return this
    }
}