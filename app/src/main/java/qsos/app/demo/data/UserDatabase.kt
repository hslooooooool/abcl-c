package qsos.app.demo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import qsos.core.form.db.entity.UserEntity
import qsos.lib.base.base.BaseApplication

/**
 * @author : 华清松
 * 用户数据库，当前版本【1】
 */
@Database(
        version = 1,
        entities = [UserEntity::class]
)
abstract class UserDatabase : RoomDatabase() {

    abstract val userDao: UserDao

    companion object {

        private var DB_NAME = "${UserDatabase::class.java.simpleName}.db"

        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(): UserDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: create(BaseApplication.appContext).also { INSTANCE = it }
                }

        private fun create(context: Context): UserDatabase {
            return Room.databaseBuilder(context, UserDatabase::class.java, DB_NAME).build()
        }

    }
}