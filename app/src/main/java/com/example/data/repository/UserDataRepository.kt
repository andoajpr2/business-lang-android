package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.LessonProgressEntity
import com.example.data.local.entity.PronunciationEvaluationEntity
import com.example.data.local.entity.QuizScoreEntity
import com.example.data.local.entity.SimulationHistoryEntity
import com.example.data.local.entity.SrsCardEntity
import com.example.data.local.entity.UserCompetencyEntity
import kotlinx.coroutines.flow.Flow
import kotlin.math.roundToInt

class UserDataRepository(private val database: AppDatabase) {

    private val favoriteDao = database.favoriteDao()
    private val progressDao = database.progressDao()
    private val srsDao = database.srsDao()
    private val simulationDao = database.simulationDao()
    private val pronunciationDao = database.pronunciationDao()
    private val competencyDao = database.competencyDao()

    // --- Favoris ---
    fun getAllFavoriteIds(): Flow<List<String>> = favoriteDao.getAllFavoriteIds()

    fun isFavorite(expressionId: String): Flow<Boolean> = favoriteDao.isFavorite(expressionId)

    suspend fun toggleFavorite(expressionId: String, currentStatus: Boolean) {
        if (currentStatus) {
            favoriteDao.removeFavorite(expressionId)
        } else {
            favoriteDao.addFavorite(FavoriteEntity(expressionId))
        }
    }

    // --- Progression Leçons ---
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

    // --- Algorithme SRS SuperMemo SM-2 ---
    fun getAllSrsCards(): Flow<List<SrsCardEntity>> = srsDao.getAllCards()

    fun getCardsDueForReview(): Flow<List<SrsCardEntity>> = srsDao.getCardsDueForReview()

    /**
     * Traitement SM-2 officiel.
     * grade : 0 = À revoir, 1 = Difficile, 2 = Bon, 3 = Facile
     */
    suspend fun processSrsReview(expressionId: String, grade: Int) {
        val currentCard = srsDao.getCardByExpressionId(expressionId) ?: SrsCardEntity(expressionId)
        val now = System.currentTimeMillis()

        var ef = currentCard.easeFactor + (0.1f - (3 - grade) * (0.08f + (3 - grade) * 0.02f))
        if (ef < 1.3f) ef = 1.3f

        val newRepetitions: Int
        val newIntervalDays: Int

        if (grade >= 2) {
            // Réussite
            newRepetitions = currentCard.repetitions + 1
            newIntervalDays = when (newRepetitions) {
                1 -> 1
                2 -> 6
                else -> (currentCard.intervalDays * ef).roundToInt().coerceAtLeast(1)
            }
        } else {
            // Échec
            newRepetitions = 0
            newIntervalDays = 1
        }

        val nextReview = now + (newIntervalDays.toLong() * 24L * 60L * 60L * 1000L)

        srsDao.saveCard(
            SrsCardEntity(
                expressionId = expressionId,
                easeFactor = ef,
                intervalDays = newIntervalDays,
                repetitions = newRepetitions,
                nextReviewTimestamp = nextReview,
                lastReviewedTimestamp = now,
                lastQualityGrade = grade
            )
        )
    }

    // Compatibilité Flashcards V1
    suspend fun recordReview(expressionId: String, masteryLevel: Int) {
        // Mapper masteryLevel (0, 1, 2) vers grade SM-2 (0, 2, 3)
        val sm2Grade = when (masteryLevel) {
            0 -> 0 // À revoir
            1 -> 2 // Bon
            2 -> 3 // Facile
            else -> 2
        }
        processSrsReview(expressionId, sm2Grade)
    }

    // --- Quiz Scores ---
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

    // --- Simulations & Jeux de rôle ---
    fun getAllSimulationHistory(): Flow<List<SimulationHistoryEntity>> = simulationDao.getAllSimulationHistory()

    suspend fun recordSimulation(scenarioId: String, diplomacy: Int, efficiency: Int, pathSummary: String) {
        simulationDao.recordSimulation(
            SimulationHistoryEntity(
                scenarioId = scenarioId,
                finalDiplomacyScore = diplomacy,
                finalEfficiencyScore = efficiency,
                pathSummary = pathSummary
            )
        )
    }

    // --- Évaluations Prononciation ---
    fun getAllPronunciationEvaluations(): Flow<List<PronunciationEvaluationEntity>> =
        pronunciationDao.getAllEvaluations()

    suspend fun recordPronunciationEvaluation(expressionId: String, spoken: String, accuracy: Int) {
        pronunciationDao.recordEvaluation(
            PronunciationEvaluationEntity(
                expressionId = expressionId,
                spokenText = spoken,
                accuracyPercentage = accuracy
            )
        )
    }

    // --- Radar de Compétences ---
    fun getUserCompetency(): Flow<UserCompetencyEntity?> = competencyDao.getCompetency()

    suspend fun updateUserCompetency(listening: Int, speaking: Int, technical: Int, etiquette: Int) {
        competencyDao.saveCompetency(
            UserCompetencyEntity(
                listeningScore = listening.coerceIn(0, 100),
                speakingScore = speaking.coerceIn(0, 100),
                technicalVocabScore = technical.coerceIn(0, 100),
                etiquetteScore = etiquette.coerceIn(0, 100),
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    // --- Réinitialisation complète ---
    suspend fun resetAllProgress() {
        favoriteDao.clearAllFavorites()
        progressDao.clearAllLessonProgress()
        progressDao.clearAllQuizScores()
        srsDao.clearAllCards()
        simulationDao.clearAllSimulationHistory()
        pronunciationDao.clearAllEvaluations()
        competencyDao.clearCompetency()
    }
}
