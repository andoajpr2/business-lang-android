package com.example.ui.screens.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

@Composable
fun QuizScreen(
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
            .testTag("quiz_screen")
    ) {
        AppTopBar(
            title = "Quiz Professionnel",
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
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            if (percentage >= 70) SuccessGreen.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.tertiaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = if (percentage >= 70) SuccessGreen else MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (percentage >= 70) "Félicitations !" else "Bel effort !",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Score : ${uiState.currentScore} / $total ($percentage%)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = when {
                        percentage == 100 -> "Parfait ! Vous maîtrisez les codes de la communication professionnelle en français."
                        percentage >= 70 -> "Très bon niveau ! Vos réponses démontrent un professionnalisme affirmé."
                        else -> "Continuez à vous entraîner pour fluidifier vos réflexes en réunion et avec vos clients."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = { viewModel.restart() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("quiz_restart_btn")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Rejouer le quiz")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Terminer")
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
                        text = "Question $currentNumber / $total",
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

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = "Mise en situation",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = currentExercise.questionOrPrompt,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Choisissez la réponse la plus adaptée :",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                currentExercise.options.forEachIndexed { index, option ->
                    val isSelected = uiState.selectedAnswer == option
                    val isCorrectOption = option.equals(currentExercise.correctAnswer, ignoreCase = true)

                    val (bgColor, borderColor, textColor) = when {
                        !uiState.isAnswerSubmitted -> {
                            if (isSelected) {
                                Triple(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.onSurface
                                )
                            } else {
                                Triple(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        isCorrectOption -> {
                            Triple(
                                SuccessGreen.copy(alpha = 0.15f),
                                SuccessGreen,
                                MaterialTheme.colorScheme.onSurface
                            )
                        }
                        isSelected && !uiState.isCorrect -> {
                            Triple(
                                ErrorRed.copy(alpha = 0.15f),
                                ErrorRed,
                                MaterialTheme.colorScheme.onSurface
                            )
                        }
                        else -> {
                            Triple(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable(enabled = !uiState.isAnswerSubmitted) {
                                viewModel.selectAnswer(option)
                            }
                            .testTag("quiz_option_$index"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = bgColor),
                        border = BorderStroke(1.5.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(borderColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.isAnswerSubmitted) {
                                    if (isCorrectOption) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = SuccessGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else if (isSelected && !uiState.isCorrect) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = null,
                                            tint = ErrorRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Text(
                                            text = ('A' + index).toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = borderColor
                                        )
                                    }
                                } else {
                                    Text(
                                        text = ('A' + index).toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = textColor,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = uiState.isAnswerSubmitted) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
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
                                    text = if (uiState.isCorrect) "✓ Excellente réponse !" else "✗ Analyse pédagogique :",
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
                            .testTag("quiz_submit_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Valider ma réponse", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { viewModel.nextQuestion() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("quiz_next_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (currentNumber >= total) "Voir mon bilan" else "Question suivante",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
