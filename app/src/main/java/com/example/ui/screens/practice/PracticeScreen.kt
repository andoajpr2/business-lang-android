package com.example.ui.screens.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.AppTopBar

@Composable
fun PracticeScreen(
    onNavigateToSimulation: () -> Unit,
    onNavigateToEmailBuilder: () -> Unit,
    onNavigateToRadar: () -> Unit,
    onNavigateToFlashcards: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToFillBlanks: () -> Unit,
    onNavigateToDialogues: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("practice_screen")
    ) {
        AppTopBar(title = "Entraînement & Ancrage V2")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "Pratiquez en situations réelles : simulations décisionnelles, rédaction d'emails, prononciation et radar de compétences.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // NOUVEAUTÉ V2 : Simulation
            item {
                PracticeCategoryCard(
                    title = "Jeu de Rôle : Simulation de Réunion",
                    description = "Affrontez des situations sous tension, parlez au micro et suivez vos jauges de diplomatie.",
                    badge = "Immersion & Voix (V2)",
                    icon = Icons.Default.SportsKabaddi,
                    onClick = onNavigateToSimulation,
                    tag = "practice_simulation_card"
                )
            }

            // NOUVEAUTÉ V2 : Générateur d'emails
            item {
                PracticeCategoryCard(
                    title = "Générateur d'Emails Pro",
                    description = "Rédigez des emails percutants (relance devis, arbitrage, excuses) prêts à copier.",
                    badge = "Écrit Professionnel (V2)",
                    icon = Icons.Default.MarkEmailRead,
                    onClick = onNavigateToEmailBuilder,
                    tag = "practice_email_builder_card"
                )
            }

            // NOUVEAUTÉ V2 : Radar de compétences
            item {
                PracticeCategoryCard(
                    title = "Radar de Compétences",
                    description = "Visualisez votre profil sur 4 axes : Écoute, Oral, Vocabulaire technique et Diplomatie.",
                    badge = "Analyse & Graphique (V2)",
                    icon = Icons.Default.AutoGraph,
                    onClick = onNavigateToRadar,
                    tag = "practice_radar_card"
                )
            }

            // Flashcards SRS SM-2
            item {
                PracticeCategoryCard(
                    title = "Flashcards & Répétition Espacée",
                    description = "Mémorisez le vocabulaire avec l'algorithme SuperMemo SM-2 optimisé.",
                    badge = "Mémorisation SM-2",
                    icon = Icons.Default.Psychology,
                    onClick = onNavigateToFlashcards,
                    tag = "practice_flashcards_card"
                )
            }

            // Quiz QCM
            item {
                PracticeCategoryCard(
                    title = "Quiz QCM Professionnel",
                    description = "Testez vos réactions et votre sens de la diplomatie dans des situations réelles.",
                    badge = "Évaluation",
                    icon = Icons.Default.Quiz,
                    onClick = onNavigateToQuiz,
                    tag = "practice_quiz_card"
                )
            }

            // Exercices à trous
            item {
                PracticeCategoryCard(
                    title = "Exercices à trous",
                    description = "Retrouvez le mot juste et précis pour chaque formule de politesse et de projet.",
                    badge = "Précision",
                    icon = Icons.Default.Edit,
                    onClick = onNavigateToFillBlanks,
                    tag = "practice_fill_blanks_card"
                )
            }

            // Dialogues
            item {
                PracticeCategoryCard(
                    title = "Dialogues de Bureau & Réunion",
                    description = "Écoutez et répétez des échanges complets entre collègues et avec des clients.",
                    badge = "Immersion Audio",
                    icon = Icons.Default.Forum,
                    onClick = onNavigateToDialogues,
                    tag = "practice_dialogues_card"
                )
            }
        }
    }
}

@Composable
fun PracticeCategoryCard(
    title: String,
    description: String,
    badge: String,
    icon: ImageVector,
    onClick: () -> Unit,
    tag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(tag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
