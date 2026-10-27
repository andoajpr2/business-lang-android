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
 * Répétition Espacée Avancée : Algorithme SuperMemo SM-2.
 */
@Entity(tableName = "srs_cards")
data class SrsCardEntity(
    @PrimaryKey
    val expressionId: String,
    val easeFactor: Float = 2.5f,        // Facteur de facilité (min 1.3f)
    val intervalDays: Int = 0,           // Intervalle actuel en jours
    val repetitions: Int = 0,            // Nombre consécutif de réussites
    val nextReviewTimestamp: Long = System.currentTimeMillis(), // Échéance de révision
    val lastReviewedTimestamp: Long = System.currentTimeMillis(),
    val lastQualityGrade: Int = 2        // 0: Blackout, 1: Difficile, 2: Bon, 3: Facile
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

/**
 * Historique des simulations de jeux de rôle et réunions.
 */
@Entity(tableName = "simulation_history")
data class SimulationHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scenarioId: String,
    val finalDiplomacyScore: Int,
    val finalEfficiencyScore: Int,
    val pathSummary: String = "",
    val completedAt: Long = System.currentTimeMillis()
)

/**
 * Historique des évaluations de prononciation orale (Speech-to-Text).
 */
@Entity(tableName = "pronunciation_evaluations")
data class PronunciationEvaluationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val expressionId: String,
    val spokenText: String,
    val accuracyPercentage: Int,
    val testedAt: Long = System.currentTimeMillis()
)

/**
 * Scores de compétences globales pour le Radar Chart.
 */
@Entity(tableName = "user_competency")
data class UserCompetencyEntity(
    @PrimaryKey
    val userId: String = "local_user",
    val listeningScore: Int = 50,       // 0-100 Écoute & Compréhension
    val speakingScore: Int = 50,        // 0-100 Élocution & Prononciation
    val technicalVocabScore: Int = 50,  // 0-100 Vocabulaire Technique
    val etiquetteScore: Int = 50,       // 0-100 Diplomatie & Étiquette
    val updatedAt: Long = System.currentTimeMillis()
)
