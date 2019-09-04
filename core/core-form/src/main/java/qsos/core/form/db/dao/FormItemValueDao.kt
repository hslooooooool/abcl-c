package qsos.core.form.db.dao

import androidx.room.*
import io.reactivex.Completable
import qsos.core.form.db.entity.Value

/**
 * @author : 华清松
 * @description : 表单项值 Dao 层
 */
@Dao
interface FormItemValueDao {

    @Query("SELECT * FROM form_item_value where form_item_id=:formItemId")
    fun getValueByFormItemId(formItemId: Long): List<Value>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(value: Value): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(value: Value): Completable

    @Update
    fun update(value: List<Value>)

    @Delete
    fun delete(value: Value)

    @Query("DELETE FROM form_item_value WHERE form_item_id=:formItemId AND user_phone=:userPhone")
    fun deleteUserByUserId(formItemId: Long?, userPhone: String)

    @Query("DELETE FROM form_item_value where form_item_id=:formItemId")
    fun deleteByFormItemId(formItemId: Long?)

}