package qsos.core.form.db.dao

import androidx.room.*
import io.reactivex.Completable
import qsos.core.form.db.entity.Value

/**
 * @author : 华清松
 * 表单项值 Dao 层
 */
@Dao
interface FormItemValueDao {

    @Query("SELECT * FROM formItemValue where formItemId=:formItemId")
    fun getValueByFormItemId(formItemId: Long): List<Value>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(value: Value): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(value: Value): Completable

    @Update
    fun update(value: List<Value>)

    @Delete
    fun delete(value: Value)

    @Query("DELETE FROM formItemValue WHERE formItemId=:formItemId AND userDesc=:userDesc")
    fun deleteUserByUserDesc(formItemId: Long?, userDesc: String)

    @Query("DELETE FROM formItemValue where formItemId=:formItemId")
    fun deleteByFormItemId(formItemId: Long?)

}