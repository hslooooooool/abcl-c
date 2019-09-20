package qsos.app.demo.data

import androidx.room.*
import qsos.core.form.db.entity.UserEntity

/**
 * @author : 华清松
 * 用户 Dao 层
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM demoUser where  userName like :key OR userDesc like :key")
    fun getUserByKey(key: String?): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(user: UserEntity)

    @Query("UPDATE demoUser SET checked = :checked WHERE limitEdit = :limitEdit")
    fun updateAllUncheck(checked: Boolean = false, limitEdit: Boolean = false)

    @Query("UPDATE demoUser SET checked = :checked WHERE limitEdit = :limitEdit")
    fun updateAllChecked(checked: Boolean = true, limitEdit: Boolean = false)

    @Delete
    fun delete(user: UserEntity)

}