package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Expression ajoutée aux favoris par l'utilisateur.
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val expressionId: String,
    val savedAt: Long = System.currentTimeMillis()
)

/**
 * Progression de l'utilisateur sur une leçon.
 */
@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey
    val lessonId: String,
    val isCompleted: Boolean = true,
    val completedAt: Long = System.currentTimeMillis()
)

/**
 * État de mémorisation d'une expression pour le système de répétition espacée.
 * masteryLevel: 0 (À revoir), 1 (En apprentissage), 2 (Maîtrisé)
 */
@Entity(tableName = "expression_reviews")
data class ExpressionReviewEntity(
    @PrimaryKey
    val expressionId: String,
    val masteryLevel: Int = 1,
    val reviewCount: Int = 1,
    val lastReviewedAt: Long = System.currentTimeMillis()
)

/**
 * Historique des scores obtenus aux quiz.
 */
@Entity(tableName = "quiz_scores")
data class QuizScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val lessonId: String,
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long = System.currentTimeMillis()
)
