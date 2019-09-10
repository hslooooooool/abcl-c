package qsos.core.form.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import qsos.core.form.db.dao.FormDao
import qsos.core.form.db.dao.FormItemDao
import qsos.core.form.db.dao.FormItemValueDao
import qsos.core.form.db.entity.FormEntity
import qsos.core.form.db.entity.FormItem
import qsos.core.form.db.entity.Value
import qsos.lib.base.base.BaseApplication

/**
 * @author : 华清松
 * 表单数据库，当前版本【1】
 */
@Database(
        version = 1,
        entities = [FormEntity::class, FormItem::class, Value::class]
)
abstract class FormDatabase : RoomDatabase() {

    abstract val formDao: FormDao

    abstract val formItemDao: FormItemDao

    abstract val formItemValueDao: FormItemValueDao

    companion object {

        private var DB_NAME = "${FormDatabase::class.java.simpleName}.db"

        @Volatile
        private var INSTANCE: FormDatabase? = null

        fun getInstance(): FormDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: create(BaseApplication.appContext).also { INSTANCE = it }
                }

        private fun create(context: Context): FormDatabase {
            return Room.databaseBuilder(context, FormDatabase::class.java, DB_NAME).build()
        }

    }
}