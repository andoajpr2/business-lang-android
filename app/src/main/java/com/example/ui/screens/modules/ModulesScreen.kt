package com.example.ui.screens.modules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.repository.PedagogyRepository
import com.example.data.repository.UserDataRepository
import com.example.ui.components.AppTopBar
import com.example.ui.components.ModuleCard

@Composable
fun ModulesScreen(
    pedagogyRepository: PedagogyRepository,
    userDataRepository: UserDataRepository,
    onNavigateToLessons: (String) -> Unit
) {
    val modules = pedagogyRepository.getAllModules()
    val completedLessonIds by userDataRepository.getCompletedLessonIds().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("modules_screen")
    ) {
        AppTopBar(title = "Parcours Métier")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "Choisissez votre univers professionnel pour perfectionner votre communication orale et écrite.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(modules, key = { it.id }) { module ->
                val moduleLessons = pedagogyRepository.getLessonsForModule(module.id)
                val completedCount = moduleLessons.count { completedLessonIds.contains(it.id) }

                ModuleCard(
                    module = module,
                    completedLessons = completedCount,
                    onClick = { onNavigateToLessons(module.id) }
                )
            }
        }
    }
}
