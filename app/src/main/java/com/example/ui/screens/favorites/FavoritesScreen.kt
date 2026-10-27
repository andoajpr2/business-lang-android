package com.example.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.repository.PedagogyRepository
import com.example.data.repository.UserDataRepository
import com.example.ui.audio.TextToSpeechManager
import com.example.ui.components.AppTopBar
import com.example.ui.components.ExpressionCard
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    pedagogyRepository: PedagogyRepository,
    userDataRepository: UserDataRepository,
    ttsManager: TextToSpeechManager,
    onExploreClick: () -> Unit
) {
    val favoriteIds by userDataRepository.getAllFavoriteIds().collectAsState(initial = emptyList())
    val speakingId by ttsManager.currentlySpeakingId.collectAsState()
    val scope = rememberCoroutineScope()

    val favoriteExpressions = favoriteIds.mapNotNull { id ->
        pedagogyRepository.getExpressionById(id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("favorites_screen")
    ) {
        AppTopBar(title = "Mes Favoris (${favoriteExpressions.size})")

        if (favoriteExpressions.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Aucun favori enregistré",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enregistrez les formules clés qui vous sont utiles en réunion ou au bureau pour les réviser à tout moment.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onExploreClick,
                    modifier = Modifier.testTag("explore_lessons_btn")
                ) {
                    Text("Explorer les expressions")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteExpressions, key = { it.id }) { expression ->
                    ExpressionCard(
                        expression = expression,
                        isFavorite = true,
                        isSpeaking = speakingId == expression.id,
                        onPlayAudio = { ttsManager.speak(expression.frenchText, expression.id) },
                        onToggleFavorite = {
                            scope.launch {
                                userDataRepository.toggleFavorite(expression.id, true)
                            }
                        }
                    )
                }
            }
        }
    }
}
