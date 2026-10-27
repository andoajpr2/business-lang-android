package com.example.ui.screens.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppTopBar
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FillBlanksScreen(
    viewModel: QuizViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentExercise = viewModel.currentExercise()
    val total = uiState.exercises.size
    val currentNumber = uiState.currentIndex + 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("fill_blanks_screen")
    ) {
        AppTopBar(
            title = "Exercices à trous",
            onBackClick = onBackClick
        )

        if (uiState.isFinished || currentExercise == null) {
            val percentage = if (total > 0) (uiState.currentScore * 100 / total) else 0

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Exercices complétés !",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Score : ${uiState.currentScore} / $total ($percentage%)",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.restart() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("fill_blanks_restart_btn")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Recommencer")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Retour")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Phrase $currentNumber / $total",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Score : ${uiState.currentScore} pt(s)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { if (total > 0) currentNumber.toFloat() / total else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Carte phrase à compléter
                val displaySentence = if (uiState.selectedAnswer != null) {
                    currentExercise.questionOrPrompt.replace("___", "[ ${uiState.selectedAnswer} ]")
                } else {
                    currentExercise.questionOrPrompt
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Complétez l'expression :",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = displaySentence,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 28.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Sélectionnez le mot manquant :",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    currentExercise.options.forEach { option ->
                        val isSelected = uiState.selectedAnswer == option
                        val isCorrect = option.equals(currentExercise.correctAnswer, ignoreCase = true)

                        val (bgColor, textColor) = when {
                            !uiState.isAnswerSubmitted -> {
                                if (isSelected) MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface
                            }
                            isCorrect -> SuccessGreen to MaterialTheme.colorScheme.onPrimary
                            isSelected && !uiState.isCorrect -> ErrorRed to MaterialTheme.colorScheme.onPrimary
                            else -> MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = bgColor,
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .clickable(enabled = !uiState.isAnswerSubmitted) {
                                    viewModel.selectAnswer(option)
                                }
                                .testTag("fill_option_$option")
                        ) {
                            Text(
                                text = option,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = uiState.isAnswerSubmitted) {
                    Column(modifier = Modifier.padding(top = 20.dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (uiState.isCorrect) SuccessGreen.copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (uiState.isCorrect) "✓ Parfaitement inséré !" else "✗ Réponse attendue : ${currentExercise.correctAnswer}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.isCorrect) SuccessGreen else ErrorRed
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentExercise.explanation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (!uiState.isAnswerSubmitted) {
                    Button(
                        onClick = { viewModel.submitAnswer() },
                        enabled = uiState.selectedAnswer != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("fill_blanks_submit_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Vérifier le mot", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { viewModel.nextQuestion() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("fill_blanks_next_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (currentNumber >= total) "Terminer la série" else "Phrase suivante",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
