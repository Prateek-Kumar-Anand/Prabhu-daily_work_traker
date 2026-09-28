package com.prabhu.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.prabhu.app.data.dao.ActivityDao
import com.prabhu.app.data.dao.ClassDao
import com.prabhu.app.data.dao.ExpenseDao
import com.prabhu.app.data.model.ActivityEntity
import com.prabhu.app.data.model.ClassEntity
import com.prabhu.app.data.model.ExpenseEntity

@Database(
    entities = [ExpenseEntity::class, ClassEntity::class, ActivityEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun classDao(): ClassDao
    abstract fun activityDao(): ActivityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "daily_tracker.db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
