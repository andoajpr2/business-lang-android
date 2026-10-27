package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ExpressionReviewEntity
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.LessonProgressEntity
import com.example.data.local.entity.QuizScoreEntity
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

    @Query("SELECT * FROM expression_reviews")
    fun getAllReviews(): Flow<List<ExpressionReviewEntity>>

    @Query("SELECT * FROM expression_reviews WHERE expressionId = :expressionId")
    suspend fun getReviewForExpression(expressionId: String): ExpressionReviewEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReview(review: ExpressionReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordQuizScore(score: QuizScoreEntity)

    @Query("SELECT * FROM quiz_scores ORDER BY timestamp DESC")
    fun getAllQuizScores(): Flow<List<QuizScoreEntity>>

    @Query("DELETE FROM lesson_progress")
    suspend fun clearAllLessonProgress()

    @Query("DELETE FROM expression_reviews")
    suspend fun clearAllReviews()

    @Query("DELETE FROM quiz_scores")
    suspend fun clearAllQuizScores()
}
