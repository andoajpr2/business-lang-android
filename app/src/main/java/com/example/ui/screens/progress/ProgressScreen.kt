package com.example.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.repository.PedagogyRepository
import com.example.data.repository.UserDataRepository
import com.example.ui.components.AppTopBar
import com.example.ui.components.StatMetricCard
import com.example.ui.theme.SuccessGreen

@Composable
fun ProgressScreen(
    pedagogyRepository: PedagogyRepository,
    userDataRepository: UserDataRepository,
    onBackClick: () -> Unit
) {
    val completedLessonIds by userDataRepository.getCompletedLessonIds().collectAsState(initial = emptyList())
    val reviews by userDataRepository.getAllReviews().collectAsState(initial = emptyList())
    val quizScores by userDataRepository.getAllQuizScores().collectAsState(initial = emptyList())

    val allLessons = pedagogyRepository.getPedagogyData().lessons
    val allModules = pedagogyRepository.getAllModules()

    val totalLessons = allLessons.size
    val completedCount = completedLessonIds.size
    val masteredExpressionsCount = reviews.count { it.masteryLevel >= 2 }
    val reviewedTotal = reviews.size

    val quizPrecision = if (quizScores.isNotEmpty()) {
        val totalCorrect = quizScores.sumOf { it.score }
        val totalQ = quizScores.sumOf { it.totalQuestions }
        if (totalQ > 0) totalCorrect * 100 / totalQ else 0
    } else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("progress_screen")
    ) {
        AppTopBar(
            title = "Ma Progression Professionnelle",
            onBackClick = onBackClick
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cartes de synthèse
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMetricCard(
                        title = "Leçons",
                        value = "$completedCount / $totalLessons",
                        subtitle = "validées",
                        icon = Icons.Default.CheckCircle,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        title = "SRS Maîtrisé",
                        value = "$masteredExpressionsCount",
                        subtitle = "sur $reviewedTotal vues",
                        icon = Icons.Default.Star,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        title = "Précision",
                        value = "$quizPrecision%",
                        subtitle = "${quizScores.size} quiz",
                        icon = Icons.Default.Quiz,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Progression par Module
            item {
                Text(
                    text = "Avancement par parcours",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(allModules, key = { it.id }) { module ->
                val moduleLessons = pedagogyRepository.getLessonsForModule(module.id)
                val moduleCompleted = moduleLessons.count { completedLessonIds.contains(it.id) }
                val progress = if (moduleLessons.isNotEmpty()) moduleCompleted.toFloat() / moduleLessons.size else 0f

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = module.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$moduleCompleted / ${moduleLessons.size}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (progress >= 1f) SuccessGreen else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            // Badges & Trophées professionnels
            item {
                Text(
                    text = "Trophées d'apprentissage",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AchievementBadgeRow(
                        title = "Premier pas en entreprise",
                        description = "Terminer votre première leçon professionnelle.",
                        isUnlocked = completedCount >= 1,
                        icon = Icons.Default.CheckCircle
                    )
                    AchievementBadgeRow(
                        title = "Animateur de réunion",
                        description = "Valider les leçons consacrées aux réunions.",
                        isUnlocked = completedLessonIds.any { it.contains("reunion") },
                        icon = Icons.Default.EmojiEvents
                    )
                    AchievementBadgeRow(
                        title = "Négociateur aguerri",
                        description = "Compléter les modules de relation client.",
                        isUnlocked = completedLessonIds.any { it.contains("client") },
                        icon = Icons.Default.MilitaryTech
                    )
                    AchievementBadgeRow(
                        title = "Rigueur & Sans faute",
                        description = "Obtenir 100% de bonnes réponses à un quiz.",
                        isUnlocked = quizScores.any { it.score == it.totalQuestions && it.totalQuestions > 0 },
                        icon = Icons.Default.Star
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementBadgeRow(
    title: String,
    description: String,
    isUnlocked: Boolean,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) SuccessGreen.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isUnlocked) SuccessGreen else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isUnlocked) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (isUnlocked) "Débloqué" else "Verrouillé",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isUnlocked) SuccessGreen else MaterialTheme.colorScheme.outline,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
