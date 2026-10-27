package com.example.ui.screens.flashcards

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppTopBar
import com.example.ui.components.AudioPlayButton
import com.example.ui.components.CategoryBadge
import com.example.ui.components.LevelBadge
import com.example.ui.theme.SuccessGreen

@Composable
fun FlashcardsScreen(
    viewModel: FlashcardsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val currentExpression = viewModel.currentExpression()
    val total = uiState.expressions.size
    val currentNumber = uiState.currentIndex + 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("flashcards_screen")
    ) {
        AppTopBar(
            title = "Flashcards Professionnelles",
            onBackClick = onBackClick
        )

        if (uiState.isCompleted || currentExpression == null) {
            // Écran de fin de session
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
                        imageVector = Icons.Default.Celebration,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Session terminée !",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Vous avez révisé ${uiState.reviewedCount} expressions professionnelles. ${uiState.masteredCount} sont marquées comme maîtrisées.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.restart() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("flashcard_restart_btn")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Recommencer une série")
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
            // Contenu actif de la session
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progression
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Carte $currentNumber sur $total",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${uiState.masteredCount} maîtrisée(s)",
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold
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

                // Carte Interactive (Recto / Verso)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clickable { viewModel.flipCard() }
                        .testTag("interactive_flashcard"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.isFlipped) MaterialTheme.colorScheme.surfaceVariant
                        else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        // En-tête de la carte
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopStart),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CategoryBadge(category = currentExpression.category)
                                LevelBadge(level = currentExpression.level)
                            }

                            AudioPlayButton(
                                isSpeaking = isSpeaking,
                                onPlayClick = { viewModel.playAudio() }
                            )
                        }

                        // Corps central (Face avant ou arrière)
                        AnimatedContent(
                            targetState = uiState.isFlipped,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(vertical = 48.dp),
                            label = "flip_animation"
                        ) { isVerso ->
                            if (!isVerso) {
                                // RECTO : Expression française
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "« ${currentExpression.frenchText} »",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 32.sp
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text(
                                        text = "Touchez la carte pour voir l'explication et l'exemple",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            } else {
                                // VERSO : Explication & Exemple
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        text = "Explication & Nuances :",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = currentExpression.explanation,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Exemple en entreprise :",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = currentExpression.exampleUsage,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Indication de basculement au bas de la carte
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlipCameraAndroid,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.isFlipped) "Face avant" else "Retourner la carte",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Boutons d'auto-évaluation Spaced Repetition (SRS)
                Text(
                    text = "Comment avez-vous trouvé cette expression ?",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.submitReview(0) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("flashcard_srs_review_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("À revoir", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.submitReview(1) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("flashcard_srs_good_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Bien", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.submitReview(2) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("flashcard_srs_mastered_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreen
                        )
                    ) {
                        Text("Maîtrisé", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
