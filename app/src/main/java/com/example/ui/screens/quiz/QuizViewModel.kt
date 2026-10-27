package com.example.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Exercise
import com.example.data.model.ExerciseType
import com.example.data.repository.PedagogyRepository
import com.example.data.repository.UserDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizUiState(
    val exercises: List<Exercise> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswer: String? = null,
    val isAnswerSubmitted: Boolean = false,
    val isCorrect: Boolean = false,
    val currentScore: Int = 0,
    val isFinished: Boolean = false
)

class QuizViewModel(
    private val lessonId: String?,
    private val exerciseType: ExerciseType,
    private val pedagogyRepository: PedagogyRepository,
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    private val exercisesList: List<Exercise> = if (exerciseType == ExerciseType.MCQ) {
        pedagogyRepository.getMCQExercises(lessonId).ifEmpty {
            pedagogyRepository.getMCQExercises(null)
        }.shuffled()
    } else {
        pedagogyRepository.getFillBlanksExercises(lessonId).ifEmpty {
            pedagogyRepository.getFillBlanksExercises(null)
        }.shuffled()
    }

    private val _uiState = MutableStateFlow(QuizUiState(exercises = exercisesList))
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun selectAnswer(answer: String) {
        if (_uiState.value.isAnswerSubmitted) return
        _uiState.value = _uiState.value.copy(selectedAnswer = answer)
    }

    fun submitAnswer() {
        val currentEx = currentExercise() ?: return
        val selected = _uiState.value.selectedAnswer ?: return
        val correct = selected.equals(currentEx.correctAnswer, ignoreCase = true)
        val newScore = if (correct) _uiState.value.currentScore + 1 else _uiState.value.currentScore

        _uiState.value = _uiState.value.copy(
            isAnswerSubmitted = true,
            isCorrect = correct,
            currentScore = newScore
        )
    }

    fun nextQuestion() {
        val nextIndex = _uiState.value.currentIndex + 1
        if (nextIndex >= _uiState.value.exercises.size) {
            // Terminé : enregistrer le score
            val finalScore = _uiState.value.currentScore
            val total = _uiState.value.exercises.size
            viewModelScope.launch {
                userDataRepository.recordQuizScore(lessonId ?: "global", finalScore, total)
            }
            _uiState.value = _uiState.value.copy(isFinished = true)
        } else {
            _uiState.value = _uiState.value.copy(
                currentIndex = nextIndex,
                selectedAnswer = null,
                isAnswerSubmitted = false,
                isCorrect = false
            )
        }
    }

    fun restart() {
        _uiState.value = QuizUiState(
            exercises = exercisesList.shuffled(),
            currentIndex = 0,
            selectedAnswer = null,
            isAnswerSubmitted = false,
            isCorrect = false,
            currentScore = 0,
            isFinished = false
        )
    }

    fun currentExercise(): Exercise? {
        val list = _uiState.value.exercises
        val idx = _uiState.value.currentIndex
        return if (idx in list.indices) list[idx] else null
    }
}
