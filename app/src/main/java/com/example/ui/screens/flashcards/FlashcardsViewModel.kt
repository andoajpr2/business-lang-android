package com.example.ui.screens.flashcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Expression
import com.example.data.repository.PedagogyRepository
import com.example.data.repository.UserDataRepository
import com.example.ui.audio.TextToSpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FlashcardsUiState(
    val expressions: List<Expression> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isCompleted: Boolean = false,
    val reviewedCount: Int = 0,
    val masteredCount: Int = 0
)

class FlashcardsViewModel(
    lessonId: String?,
    private val pedagogyRepository: PedagogyRepository,
    private val userDataRepository: UserDataRepository,
    private val ttsManager: TextToSpeechManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FlashcardsUiState(
            expressions = if (lessonId != null) {
                pedagogyRepository.getExpressionsForLesson(lessonId).shuffled()
            } else {
                pedagogyRepository.getAllExpressions().shuffled()
            }
        )
    )
    val uiState: StateFlow<FlashcardsUiState> = _uiState.asStateFlow()
    val isSpeaking: StateFlow<Boolean> = ttsManager.isSpeaking

    fun flipCard() {
        _uiState.value = _uiState.value.copy(isFlipped = !_uiState.value.isFlipped)
    }

    fun playAudio() {
        val current = currentExpression() ?: return
        ttsManager.speak(current.frenchText, current.id)
    }

    fun submitReview(masteryLevel: Int) {
        val current = currentExpression() ?: return
        viewModelScope.launch {
            userDataRepository.recordReview(current.id, masteryLevel)
        }

        val nextIndex = _uiState.value.currentIndex + 1
        val newMastered = if (masteryLevel >= 2) _uiState.value.masteredCount + 1 else _uiState.value.masteredCount

        if (nextIndex >= _uiState.value.expressions.size) {
            _uiState.value = _uiState.value.copy(
                isCompleted = true,
                reviewedCount = _uiState.value.reviewedCount + 1,
                masteredCount = newMastered
            )
        } else {
            _uiState.value = _uiState.value.copy(
                currentIndex = nextIndex,
                isFlipped = false,
                reviewedCount = _uiState.value.reviewedCount + 1,
                masteredCount = newMastered
            )
        }
    }

    fun restart() {
        _uiState.value = _uiState.value.copy(
            expressions = _uiState.value.expressions.shuffled(),
            currentIndex = 0,
            isFlipped = false,
            isCompleted = false,
            reviewedCount = 0,
            masteredCount = 0
        )
    }

    fun currentExpression(): Expression? {
        val list = _uiState.value.expressions
        val idx = _uiState.value.currentIndex
        return if (idx in list.indices) list[idx] else null
    }
}
