package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CompetencyDao
import com.example.data.local.dao.FavoriteDao
import com.example.data.local.dao.ProgressDao
import com.example.data.local.dao.PronunciationDao
import com.example.data.local.dao.SimulationDao
import com.example.data.local.dao.SrsDao
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.LessonProgressEntity
import com.example.data.local.entity.PronunciationEvaluationEntity
import com.example.data.local.entity.QuizScoreEntity
import com.example.data.local.entity.SimulationHistoryEntity
import com.example.data.local.entity.SrsCardEntity
import com.example.data.local.entity.UserCompetencyEntity

@Database(
    entities = [
        FavoriteEntity::class,
        LessonProgressEntity::class,
        QuizScoreEntity::class,
        SrsCardEntity::class,
        SimulationHistoryEntity::class,
        PronunciationEvaluationEntity::class,
        UserCompetencyEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun progressDao(): ProgressDao
    abstract fun srsDao(): SrsDao
    abstract fun simulationDao(): SimulationDao
    abstract fun pronunciationDao(): PronunciationDao
    abstract fun competencyDao(): CompetencyDao

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
