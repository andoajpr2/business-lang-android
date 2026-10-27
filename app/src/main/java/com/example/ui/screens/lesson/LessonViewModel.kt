package com.example.ui.screens.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Exercise
import com.example.data.model.Expression
import com.example.data.model.Lesson
import com.example.data.repository.PedagogyRepository
import com.example.data.repository.UserDataRepository
import com.example.ui.audio.TextToSpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LessonDetailUiState(
    val lesson: Lesson?,
    val expressions: List<Expression> = emptyList(),
    val exercises: List<Exercise> = emptyList(),
    val isCompleted: Boolean = false,
    val favoriteIds: Set<String> = emptySet()
)

class LessonViewModel(
    private val lessonId: String,
    private val pedagogyRepository: PedagogyRepository,
    private val userDataRepository: UserDataRepository,
    private val ttsManager: TextToSpeechManager
) : ViewModel() {

    val currentlySpeakingId: StateFlow<String?> = ttsManager.currentlySpeakingId

    private val lesson = pedagogyRepository.getLessonById(lessonId)
    private val expressions = pedagogyRepository.getExpressionsForLesson(lessonId)
    private val exercises = pedagogyRepository.getExercisesForLesson(lessonId)

    val uiState: StateFlow<LessonDetailUiState> = combine(
        userDataRepository.getCompletedLessonIds(),
        userDataRepository.getAllFavoriteIds()
    ) { completedIds, favoriteIds ->
        LessonDetailUiState(
            lesson = lesson,
            expressions = expressions,
            exercises = exercises,
            isCompleted = completedIds.contains(lessonId),
            favoriteIds = favoriteIds.toSet()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LessonDetailUiState(
            lesson = lesson,
            expressions = expressions,
            exercises = exercises
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

    fun toggleLessonCompleted() {
        viewModelScope.launch {
            userDataRepository.toggleLessonCompleted(lessonId, uiState.value.isCompleted)
        }
    }
}
