package qsos.core.form.db.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import qsos.core.form.db.entity.FormEntity

/**
 * @author : 华清松
 * 表单 Dao 层
 *
 * 注意：最新版本的Room支持更多RxJava相关操作，如删除是否完成观察者
 */
@Dao
interface FormDao {

    @Query("SELECT * FROM form where id=:id")
    fun getFormById(id: Long): Flowable<FormEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(form: FormEntity): Long

    @Delete
    fun delete(form: FormEntity): Completable

}