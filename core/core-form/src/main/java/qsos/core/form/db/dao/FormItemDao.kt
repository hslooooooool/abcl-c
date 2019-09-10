package qsos.core.form.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import qsos.core.form.db.entity.FormItem

/**
 * @author : 华清松
 * 表单项 Dao 层
 */
@Dao
interface FormItemDao {

    @Query("SELECT * FROM formItems where formId=:formId")
    fun getFormItemByFormId(formId: Long): List<FormItem>

    @Query("SELECT * FROM formItems where id=:id")
    fun getFormItemByIdF(id: Long): Flowable<FormItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: FormItem): Long

}