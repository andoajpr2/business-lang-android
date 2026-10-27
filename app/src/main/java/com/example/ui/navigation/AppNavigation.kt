package com.example.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.model.ExerciseType
import com.example.data.repository.PedagogyRepository
import com.example.data.repository.PreferencesRepository
import com.example.data.repository.UserDataRepository
import com.example.ui.audio.SpeechToTextManager
import com.example.ui.audio.TextToSpeechManager
import com.example.ui.screens.competency.CompetencyRadarScreen
import com.example.ui.screens.competency.CompetencyViewModel
import com.example.ui.screens.dialogues.DialoguesScreen
import com.example.ui.screens.emails.EmailBuilderScreen
import com.example.ui.screens.emails.EmailBuilderViewModel
import com.example.ui.screens.favorites.FavoritesScreen
import com.example.ui.screens.flashcards.FlashcardsScreen
import com.example.ui.screens.flashcards.FlashcardsViewModel
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.home.HomeViewModel
import com.example.ui.screens.lesson.LessonDetailScreen
import com.example.ui.screens.lesson.LessonViewModel
import com.example.ui.screens.modules.LessonsScreen
import com.example.ui.screens.modules.ModulesScreen
import com.example.ui.screens.practice.PracticeScreen
import com.example.ui.screens.progress.ProgressScreen
import com.example.ui.screens.quiz.FillBlanksScreen
import com.example.ui.screens.quiz.QuizScreen
import com.example.ui.screens.quiz.QuizViewModel
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.search.SearchViewModel
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.settings.SettingsViewModel
import com.example.ui.screens.simulation.SimulationScreen
import com.example.ui.screens.simulation.SimulationViewModel

@Composable
fun AppNavigation(
    pedagogyRepository: PedagogyRepository,
    userDataRepository: UserDataRepository,
    preferencesRepository: PreferencesRepository,
    ttsManager: TextToSpeechManager,
    sttManager: SpeechToTextManager
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Modules.route,
        Screen.Practice.route,
        Screen.Favorites.route
    )

    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier.testTag("app_bottom_bar")
                ) {
                    val items = listOf(
                        Triple(Screen.Home.route, "Accueil", Icons.Filled.Home to Icons.Outlined.Home),
                        Triple(Screen.Modules.route, "Parcours", Icons.Filled.MenuBook to Icons.Outlined.MenuBook),
                        Triple(Screen.Practice.route, "Pratique", Icons.Filled.Psychology to Icons.Outlined.Psychology),
                        Triple(Screen.Favorites.route, "Favoris", Icons.Filled.Bookmark to Icons.Outlined.BookmarkBorder)
                    )

                    items.forEach { (route, label, icons) ->
                        val selected = currentRoute == route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != route) {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) icons.first else icons.second,
                                    contentDescription = label
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("bottom_tab_${route.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Accueil
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel {
                    HomeViewModel(pedagogyRepository, userDataRepository, ttsManager)
                }
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToModule = { moduleId ->
                        navController.navigate(Screen.Lessons.createRoute(moduleId))
                    },
                    onNavigateToFlashcards = {
                        navController.navigate(Screen.Flashcards.createRoute())
                    },
                    onNavigateToQuiz = {
                        navController.navigate(Screen.Quiz.createRoute())
                    },
                    onNavigateToDialogues = {
                        navController.navigate(Screen.Dialogues.route)
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    },
                    onNavigateToProgress = {
                        navController.navigate(Screen.Progress.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            // 2. Modules
            composable(Screen.Modules.route) {
                ModulesScreen(
                    pedagogyRepository = pedagogyRepository,
                    userDataRepository = userDataRepository,
                    onNavigateToLessons = { moduleId ->
                        navController.navigate(Screen.Lessons.createRoute(moduleId))
                    }
                )
            }

            // 3. Pratique V2 Hub
            composable(Screen.Practice.route) {
                PracticeScreen(
                    onNavigateToSimulation = {
                        navController.navigate(Screen.Simulation.createRoute())
                    },
                    onNavigateToEmailBuilder = {
                        navController.navigate(Screen.EmailBuilder.route)
                    },
                    onNavigateToRadar = {
                        navController.navigate(Screen.CompetencyRadar.route)
                    },
                    onNavigateToFlashcards = {
                        navController.navigate(Screen.Flashcards.createRoute())
                    },
                    onNavigateToQuiz = {
                        navController.navigate(Screen.Quiz.createRoute())
                    },
                    onNavigateToFillBlanks = {
                        navController.navigate(Screen.FillBlanks.createRoute())
                    },
                    onNavigateToDialogues = {
                        navController.navigate(Screen.Dialogues.route)
                    }
                )
            }

            // 4. Favoris
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    pedagogyRepository = pedagogyRepository,
                    userDataRepository = userDataRepository,
                    ttsManager = ttsManager,
                    onExploreClick = {
                        navController.navigate(Screen.Modules.route)
                    }
                )
            }

            // Leçons d'un module
            composable(
                route = Screen.Lessons.route,
                arguments = listOf(navArgument("moduleId") { type = NavType.StringType })
            ) { backStackEntry ->
                val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
                LessonsScreen(
                    moduleId = moduleId,
                    pedagogyRepository = pedagogyRepository,
                    userDataRepository = userDataRepository,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToLessonDetail = { lessonId ->
                        navController.navigate(Screen.LessonDetail.createRoute(lessonId))
                    }
                )
            }

            // Détail d'une leçon
            composable(
                route = Screen.LessonDetail.route,
                arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
                val lessonViewModel: LessonViewModel = viewModel(key = "lesson_$lessonId") {
                    LessonViewModel(lessonId, pedagogyRepository, userDataRepository, ttsManager)
                }
                LessonDetailScreen(
                    viewModel = lessonViewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToQuiz = { lid ->
                        navController.navigate(Screen.Quiz.createRoute(lid))
                    },
                    onNavigateToFlashcards = { lid ->
                        navController.navigate(Screen.Flashcards.createRoute(lid))
                    }
                )
            }

            // Flashcards (avec SM-2)
            composable(
                route = Screen.Flashcards.route,
                arguments = listOf(navArgument("lessonId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId")
                val flashcardsViewModel: FlashcardsViewModel = viewModel(key = "flashcards_$lessonId") {
                    FlashcardsViewModel(lessonId, pedagogyRepository, userDataRepository, ttsManager)
                }
                FlashcardsScreen(
                    viewModel = flashcardsViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Quiz QCM
            composable(
                route = Screen.Quiz.route,
                arguments = listOf(navArgument("lessonId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId")
                val quizViewModel: QuizViewModel = viewModel(key = "quiz_mcq_$lessonId") {
                    QuizViewModel(lessonId, ExerciseType.MCQ, pedagogyRepository, userDataRepository)
                }
                QuizScreen(
                    viewModel = quizViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Exercices à trous
            composable(
                route = Screen.FillBlanks.route,
                arguments = listOf(navArgument("lessonId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId")
                val fillBlanksViewModel: QuizViewModel = viewModel(key = "quiz_fill_$lessonId") {
                    QuizViewModel(lessonId, ExerciseType.FILL_BLANKS, pedagogyRepository, userDataRepository)
                }
                FillBlanksScreen(
                    viewModel = fillBlanksViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Dialogues
            composable(Screen.Dialogues.route) {
                DialoguesScreen(
                    pedagogyRepository = pedagogyRepository,
                    ttsManager = ttsManager,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Recherche
            composable(Screen.Search.route) {
                val searchViewModel: SearchViewModel = viewModel {
                    SearchViewModel(pedagogyRepository, userDataRepository, ttsManager)
                }
                SearchScreen(
                    viewModel = searchViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Progression
            composable(Screen.Progress.route) {
                ProgressScreen(
                    pedagogyRepository = pedagogyRepository,
                    userDataRepository = userDataRepository,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Paramètres
            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = viewModel {
                    SettingsViewModel(preferencesRepository, userDataRepository, ttsManager)
                }
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // NOUVEAUTÉ V2 : Simulation & Jeu de rôle
            composable(
                route = Screen.Simulation.route,
                arguments = listOf(navArgument("scenarioId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val scenarioId = backStackEntry.arguments?.getString("scenarioId")
                val simViewModel: SimulationViewModel = viewModel(key = "sim_$scenarioId") {
                    SimulationViewModel(scenarioId, pedagogyRepository, userDataRepository, ttsManager, sttManager)
                }
                SimulationScreen(
                    viewModel = simViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // NOUVEAUTÉ V2 : Générateur d'emails
            composable(Screen.EmailBuilder.route) {
                val emailViewModel: EmailBuilderViewModel = viewModel {
                    EmailBuilderViewModel(pedagogyRepository)
                }
                EmailBuilderScreen(
                    viewModel = emailViewModel,
                    ttsManager = ttsManager,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // NOUVEAUTÉ V2 : Radar de compétences
            composable(Screen.CompetencyRadar.route) {
                val competencyViewModel: CompetencyViewModel = viewModel {
                    CompetencyViewModel(userDataRepository)
                }
                CompetencyRadarScreen(
                    viewModel = competencyViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
