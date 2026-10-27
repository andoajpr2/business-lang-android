package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.LessonProgressEntity
import com.example.data.local.entity.PronunciationEvaluationEntity
import com.example.data.local.entity.QuizScoreEntity
import com.example.data.local.entity.SimulationHistoryEntity
import com.example.data.local.entity.SrsCardEntity
import com.example.data.local.entity.UserCompetencyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT expressionId FROM favorites ORDER BY savedAt DESC")
    fun getAllFavoriteIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE expressionId = :expressionId)")
    fun isFavorite(expressionId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE expressionId = :expressionId")
    suspend fun removeFavorite(expressionId: String)

    @Query("DELETE FROM favorites")
    suspend fun clearAllFavorites()
}

@Dao
interface ProgressDao {
    @Query("SELECT lessonId FROM lesson_progress WHERE isCompleted = 1")
    fun getCompletedLessonIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markLessonCompleted(progress: LessonProgressEntity)

    @Query("DELETE FROM lesson_progress WHERE lessonId = :lessonId")
    suspend fun unmarkLessonCompleted(lessonId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordQuizScore(score: QuizScoreEntity)

    @Query("SELECT * FROM quiz_scores ORDER BY timestamp DESC")
    fun getAllQuizScores(): Flow<List<QuizScoreEntity>>

    @Query("DELETE FROM lesson_progress")
    suspend fun clearAllLessonProgress()

    @Query("DELETE FROM quiz_scores")
    suspend fun clearAllQuizScores()
}

@Dao
interface SrsDao {
    @Query("SELECT * FROM srs_cards")
    fun getAllCards(): Flow<List<SrsCardEntity>>

    @Query("SELECT * FROM srs_cards WHERE nextReviewTimestamp <= :nowTimestamp ORDER BY nextReviewTimestamp ASC")
    fun getCardsDueForReview(nowTimestamp: Long = System.currentTimeMillis()): Flow<List<SrsCardEntity>>

    @Query("SELECT * FROM srs_cards WHERE expressionId = :expressionId")
    suspend fun getCardByExpressionId(expressionId: String): SrsCardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCard(card: SrsCardEntity)

    @Query("DELETE FROM srs_cards")
    suspend fun clearAllCards()
}

@Dao
interface SimulationDao {
    @Query("SELECT * FROM simulation_history ORDER BY completedAt DESC")
    fun getAllSimulationHistory(): Flow<List<SimulationHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordSimulation(history: SimulationHistoryEntity)

    @Query("DELETE FROM simulation_history")
    suspend fun clearAllSimulationHistory()
}

@Dao
interface PronunciationDao {
    @Query("SELECT * FROM pronunciation_evaluations ORDER BY testedAt DESC")
    fun getAllEvaluations(): Flow<List<PronunciationEvaluationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordEvaluation(evaluation: PronunciationEvaluationEntity)

    @Query("DELETE FROM pronunciation_evaluations")
    suspend fun clearAllEvaluations()
}

@Dao
interface CompetencyDao {
    @Query("SELECT * FROM user_competency WHERE userId = :userId")
    fun getCompetency(userId: String = "local_user"): Flow<UserCompetencyEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCompetency(competency: UserCompetencyEntity)

    @Query("DELETE FROM user_competency")
    suspend fun clearCompetency()
}
