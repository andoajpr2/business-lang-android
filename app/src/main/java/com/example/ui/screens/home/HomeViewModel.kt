package com.example.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Expression
import com.example.data.model.Module
import com.example.data.repository.PedagogyRepository
import com.example.data.repository.UserDataRepository
import com.example.ui.audio.TextToSpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val featuredExpression: Expression,
    val modules: List<Module> = emptyList(),
    val completedLessonIds: Set<String> = emptySet(),
    val favoriteIds: Set<String> = emptySet(),
    val totalLessonsCount: Int = 0,
    val masteredCount: Int = 0,
    val quizAccuracyPercent: Int = 0
)

class HomeViewModel(
    private val pedagogyRepository: PedagogyRepository,
    private val userDataRepository: UserDataRepository,
    private val ttsManager: TextToSpeechManager
) : ViewModel() {

    private val _featuredExpression = MutableStateFlow(pedagogyRepository.getDailyFeaturedExpression())
    val currentlySpeakingId: StateFlow<String?> = ttsManager.currentlySpeakingId

    val uiState: StateFlow<HomeUiState> = combine(
        userDataRepository.getCompletedLessonIds(),
        userDataRepository.getAllFavoriteIds(),
        userDataRepository.getAllReviews(),
        userDataRepository.getAllQuizScores()
    ) { completedLessons, favoriteIds, reviews, quizScores ->
        val allLessons = pedagogyRepository.getPedagogyData().lessons
        val mastered = reviews.count { it.masteryLevel >= 2 }

        val accuracy = if (quizScores.isNotEmpty()) {
            val totalScore = quizScores.sumOf { it.score }
            val totalQuestions = quizScores.sumOf { it.totalQuestions }
            if (totalQuestions > 0) (totalScore * 100 / totalQuestions) else 0
        } else {
            0
        }

        HomeUiState(
            featuredExpression = _featuredExpression.value,
            modules = pedagogyRepository.getAllModules(),
            completedLessonIds = completedLessons.toSet(),
            favoriteIds = favoriteIds.toSet(),
            totalLessonsCount = allLessons.size,
            masteredCount = mastered,
            quizAccuracyPercent = accuracy
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(
            featuredExpression = _featuredExpression.value,
            modules = pedagogyRepository.getAllModules(),
            totalLessonsCount = pedagogyRepository.getPedagogyData().lessons.size
        )
    )

    fun playAudio(expression: Expression) {
        ttsManager.speak(expression.frenchText, expression.id)
    }

    fun toggleFavorite(expressionId: String) {
        viewModelScope.launch {
            val isFav = uiState.value.favoriteIds.contains(expressionId)
            userDataRepository.toggleFavorite(expressionId, isFav)
        }
    }
}
