package com.example.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Category
import com.example.data.model.DifficultyLevel
import com.example.data.model.Expression
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

data class SearchUiState(
    val query: String = "",
    val selectedCategory: Category? = null,
    val selectedLevel: DifficultyLevel? = null,
    val results: List<Expression> = emptyList(),
    val favoriteIds: Set<String> = emptySet()
)

class SearchViewModel(
    private val pedagogyRepository: PedagogyRepository,
    private val userDataRepository: UserDataRepository,
    private val ttsManager: TextToSpeechManager
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _selectedLevel = MutableStateFlow<DifficultyLevel?>(null)
    val selectedLevel: StateFlow<DifficultyLevel?> = _selectedLevel.asStateFlow()

    val currentlySpeakingId: StateFlow<String?> = ttsManager.currentlySpeakingId

    val uiState: StateFlow<SearchUiState> = combine(
        _query,
        _selectedCategory,
        _selectedLevel,
        userDataRepository.getAllFavoriteIds()
    ) { q, cat, lev, favIds ->
        val results = pedagogyRepository.searchExpressions(
            query = q,
            category = cat,
            level = lev
        )
        SearchUiState(
            query = q,
            selectedCategory = cat,
            selectedLevel = lev,
            results = results,
            favoriteIds = favIds.toSet()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState(
            results = pedagogyRepository.getAllExpressions()
        )
    )

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun onSelectCategory(category: Category?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun onSelectLevel(level: DifficultyLevel?) {
        _selectedLevel.value = if (_selectedLevel.value == level) null else level
    }

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
