package qsos.app.demo.form

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author : 华清松
 * 选择用户实体类
 * @param id 主键ID
 * @param userId 用户ID
 * @param userName 用户名称
 * @param userDesc 用户描述，推荐为手机号
 * @param userAvatar 用户头像链接，http://qsos.vip/img/logo.png
 * @param limitEdit 用户是否可操作
 * @param checked 用户是否被选中
 */
@Entity(tableName = "demoUser")
data class UserEntity(
        @PrimaryKey(autoGenerate = true)
        var id: Long? = null,
        var userId: String? = null,
        var userName: String? = null,
        var userDesc: String? = null,
        var userAvatar: String? = null,
        var limitEdit: Boolean = false,
        var checked: Boolean = false
)