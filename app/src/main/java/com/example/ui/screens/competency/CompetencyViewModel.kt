package com.example.ui.screens.competency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.UserDataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class CompetencyUiState(
    val listeningScore: Int = 65,
    val speakingScore: Int = 55,
    val technicalVocabScore: Int = 70,
    val etiquetteScore: Int = 75,
    val topStrength: String = "Diplomatie & Étiquette en réunion",
    val topAreaToImprove: String = "Élocution orale & Prononciation",
    val recommendedAction: String = "Pratiquez 5 minutes de prononciation avec le micro ou lancez une simulation de réunion."
)

class CompetencyViewModel(
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    val uiState: StateFlow<CompetencyUiState> = combine(
        userDataRepository.getAllSrsCards(),
        userDataRepository.getAllQuizScores(),
        userDataRepository.getAllSimulationHistory(),
        userDataRepository.getAllPronunciationEvaluations()
    ) { srsCards, quizScores, simulations, pronunciations ->

        // 1. Vocabulaire Technique (base sur le SRS SM-2)
        val srsMastered = srsCards.count { it.repetitions >= 2 || it.lastQualityGrade >= 2 }
        val vocabScore = (50 + (srsMastered * 5)).coerceIn(40, 95)

        // 2. Écoute & Quiz
        val quizAccuracy = if (quizScores.isNotEmpty()) {
            val totalScore = quizScores.sumOf { it.score }
            val totalQ = quizScores.sumOf { it.totalQuestions }
            if (totalQ > 0) (totalScore * 100 / totalQ) else 60
        } else 60
        val listeningScore = quizAccuracy.coerceIn(40, 100)

        // 3. Élocution & Oral
        val speakingScore = if (pronunciations.isNotEmpty()) {
            pronunciations.map { it.accuracyPercentage }.average().toInt()
        } else 55

        // 4. Diplomatie & Étiquette
        val etiquetteScore = if (simulations.isNotEmpty()) {
            val avgDiplomacy = simulations.map { it.finalDiplomacyScore }.average().toInt()
            val avgEfficiency = simulations.map { it.finalEfficiencyScore }.average().toInt()
            ((avgDiplomacy + avgEfficiency) / 2).coerceIn(40, 100)
        } else 70

        val scores = listOf(
            "Écoute & Compréhension" to listeningScore,
            "Élocution & Oral" to speakingScore,
            "Vocabulaire Technique" to vocabScore,
            "Étiquette & Diplomatie" to etiquetteScore
        )

        val best = scores.maxByOrNull { it.second }?.first ?: "Étiquette & Diplomatie"
        val lowest = scores.minByOrNull { it.second }?.first ?: "Élocution & Oral"

        CompetencyUiState(
            listeningScore = listeningScore,
            speakingScore = speakingScore,
            technicalVocabScore = vocabScore,
            etiquetteScore = etiquetteScore,
            topStrength = best,
            topAreaToImprove = lowest,
            recommendedAction = when (lowest) {
                "Élocution & Oral" -> "Entraînez-vous avec les exercices de répétition vocale guidée."
                "Vocabulaire Technique" -> "Passez en revue votre paquet de Flashcards quotidiennes."
                "Écoute & Compréhension" -> "Écoutez les dialogues professionnels en fermant les yeux."
                else -> "Affinez vos réflexes dans les simulations de négociation client."
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CompetencyUiState()
    )
}
