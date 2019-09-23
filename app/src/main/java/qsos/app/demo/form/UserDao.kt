package qsos.app.demo.form

import androidx.room.*

/**
 * @author : 华清松
 * 用户 Dao 层
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM demoUser")
    fun findAll(): List<UserEntity>

    @Query("SELECT * FROM demoUser where userName like '%'||:key||'%' OR userDesc like '%'||:key||'%' ")
    fun findUserByKey(key: String): List<UserEntity>

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

    @Query("DELETE FROM demoUser")
    fun deleteAll()

}