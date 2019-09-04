package qsos.core.form.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import qsos.core.form.db.entity.FormItem

/**
 * @author : 华清松
 * @description : 表单项 Dao 层
 */
@Dao
interface FormItemDao {

    @Query("SELECT * FROM form_item where form_id=:formId")
    fun getFormItemByFormId(formId: Long): List<FormItem>

    @Query("SELECT * FROM form_item where id=:id")
    fun getFormItemByIdF(id: Long): Flowable<FormItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: FormItem): Long

}