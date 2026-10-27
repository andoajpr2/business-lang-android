package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.FavoriteDao
import com.example.data.local.dao.ProgressDao
import com.example.data.local.entity.ExpressionReviewEntity
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.LessonProgressEntity
import com.example.data.local.entity.QuizScoreEntity

@Database(
    entities = [
        FavoriteEntity::class,
        LessonProgressEntity::class,
        ExpressionReviewEntity::class,
        QuizScoreEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun progressDao(): ProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "francais_pro_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
