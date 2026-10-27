package com.example.ui.screens.lesson

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.AppTopBar
import com.example.ui.components.ExpressionCard
import com.example.ui.theme.SuccessGreen

@Composable
fun LessonDetailScreen(
    viewModel: LessonViewModel,
    onBackClick: () -> Unit,
    onNavigateToQuiz: (String) -> Unit,
    onNavigateToFlashcards: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val speakingId by viewModel.currentlySpeakingId.collectAsState()
    val lesson = uiState.lesson

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("lesson_detail_screen")
    ) {
        AppTopBar(
            title = lesson?.title ?: "Détail de la leçon",
            onBackClick = onBackClick
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Carte Contexte de situation professionnelle
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Contexte professionnel",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = lesson?.contextSummary ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Boutons d'entraînement ciblés
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { lesson?.let { onNavigateToFlashcards(it.id) } },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("lesson_flashcards_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Flashcards")
                            }

                            Button(
                                onClick = { lesson?.let { onNavigateToQuiz(it.id) } },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("lesson_quiz_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Quiz,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Quiz")
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Expressions incontournables (${uiState.expressions.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Liste des expressions
            items(uiState.expressions, key = { it.id }) { expression ->
                ExpressionCard(
                    expression = expression,
                    isFavorite = uiState.favoriteIds.contains(expression.id),
                    isSpeaking = speakingId == expression.id,
                    onPlayAudio = { viewModel.playAudio(expression) },
                    onToggleFavorite = { viewModel.toggleFavorite(expression.id) }
                )
            }

            // Bouton de validation de la leçon
            item {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.toggleLessonCompleted() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("toggle_lesson_completed_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (uiState.isCompleted) SuccessGreen else MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = if (uiState.isCompleted) SuccessGreen else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.isCompleted) "Leçon validée ! (Cliquer pour annuler)" else "Marquer comme maîtrisée",
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
