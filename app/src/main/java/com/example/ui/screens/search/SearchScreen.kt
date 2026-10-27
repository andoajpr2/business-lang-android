package com.example.ui.screens.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Category
import com.example.data.model.DifficultyLevel
import com.example.ui.components.AppTopBar
import com.example.ui.components.ExpressionCard

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val speakingId by viewModel.currentlySpeakingId.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("search_screen")
    ) {
        AppTopBar(
            title = "Recherche d'expressions",
            onBackClick = onBackClick
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Barre de saisie
            OutlinedTextField(
                value = uiState.query,
                onValueChange = { viewModel.onQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_text_field"),
                placeholder = { Text("Rechercher par mot-clé, thème, situation...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Effacer")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filtres par catégorie
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Category.entries.forEach { category ->
                    val isSelected = uiState.selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onSelectCategory(category) },
                        label = { Text(category.displayName) },
                        modifier = Modifier.testTag("filter_cat_${category.name.lowercase()}")
                    )
                }
            }

            // Filtres par niveau
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DifficultyLevel.entries.forEach { level ->
                    val isSelected = uiState.selectedLevel == level
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onSelectLevel(level) },
                        label = { Text(level.displayName) },
                        modifier = Modifier.testTag("filter_level_${level.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${uiState.results.size} expression(s) trouvée(s)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.results, key = { it.id }) { expression ->
                ExpressionCard(
                    expression = expression,
                    isFavorite = uiState.favoriteIds.contains(expression.id),
                    isSpeaking = speakingId == expression.id,
                    onPlayAudio = { viewModel.playAudio(expression) },
                    onToggleFavorite = { viewModel.toggleFavorite(expression.id) }
                )
            }
        }
    }
}
