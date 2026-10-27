package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.ExpressionReviewEntity
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.LessonProgressEntity
import com.example.data.local.entity.QuizScoreEntity
import kotlinx.coroutines.flow.Flow

class UserDataRepository(private val database: AppDatabase) {

    private val favoriteDao = database.favoriteDao()
    private val progressDao = database.progressDao()

    // Favorites
    fun getAllFavoriteIds(): Flow<List<String>> = favoriteDao.getAllFavoriteIds()

    fun isFavorite(expressionId: String): Flow<Boolean> = favoriteDao.isFavorite(expressionId)

    suspend fun toggleFavorite(expressionId: String, currentStatus: Boolean) {
        if (currentStatus) {
            favoriteDao.removeFavorite(expressionId)
        } else {
            favoriteDao.addFavorite(FavoriteEntity(expressionId))
        }
    }

    // Lesson Progress
    fun getCompletedLessonIds(): Flow<List<String>> = progressDao.getCompletedLessonIds()

    suspend fun markLessonCompleted(lessonId: String) {
        progressDao.markLessonCompleted(LessonProgressEntity(lessonId = lessonId, isCompleted = true))
    }

    suspend fun toggleLessonCompleted(lessonId: String, currentlyCompleted: Boolean) {
        if (currentlyCompleted) {
            progressDao.unmarkLessonCompleted(lessonId)
        } else {
            progressDao.markLessonCompleted(LessonProgressEntity(lessonId = lessonId, isCompleted = true))
        }
    }

    // Spaced repetition & reviews
    fun getAllReviews(): Flow<List<ExpressionReviewEntity>> = progressDao.getAllReviews()

    suspend fun recordReview(expressionId: String, masteryLevel: Int) {
        val existing = progressDao.getReviewForExpression(expressionId)
        val count = (existing?.reviewCount ?: 0) + 1
        progressDao.saveReview(
            ExpressionReviewEntity(
                expressionId = expressionId,
                masteryLevel = masteryLevel,
                reviewCount = count,
                lastReviewedAt = System.currentTimeMillis()
            )
        )
    }

    // Quiz scores
    suspend fun recordQuizScore(lessonId: String, score: Int, totalQuestions: Int) {
        progressDao.recordQuizScore(
            QuizScoreEntity(
                lessonId = lessonId,
                score = score,
                totalQuestions = totalQuestions
            )
        )
    }

    fun getAllQuizScores(): Flow<List<QuizScoreEntity>> = progressDao.getAllQuizScores()

    // Reset all
    suspend fun resetAllProgress() {
        favoriteDao.clearAllFavorites()
        progressDao.clearAllLessonProgress()
        progressDao.clearAllReviews()
        progressDao.clearAllQuizScores()
    }
}
