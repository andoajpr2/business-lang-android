package com.example.ui.navigation

sealed class Screen(val route: String) {
    // Bottom Bar tabs
    data object Home : Screen("home")
    data object Modules : Screen("modules")
    data object Practice : Screen("practice")
    data object Favorites : Screen("favorites")

    // Sub screens
    data object Lessons : Screen("lessons/{moduleId}") {
        fun createRoute(moduleId: String) = "lessons/$moduleId"
    }

    data object LessonDetail : Screen("lesson_detail/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson_detail/$lessonId"
    }

    data object Flashcards : Screen("flashcards?lessonId={lessonId}") {
        fun createRoute(lessonId: String? = null) =
            if (lessonId != null) "flashcards?lessonId=$lessonId" else "flashcards"
    }

    data object Quiz : Screen("quiz?lessonId={lessonId}") {
        fun createRoute(lessonId: String? = null) =
            if (lessonId != null) "quiz?lessonId=$lessonId" else "quiz"
    }

    data object FillBlanks : Screen("fill_blanks?lessonId={lessonId}") {
        fun createRoute(lessonId: String? = null) =
            if (lessonId != null) "fill_blanks?lessonId=$lessonId" else "fill_blanks"
    }

    data object Dialogues : Screen("dialogues")
    data object Search : Screen("search")
    data object Progress : Screen("progress")
    data object Settings : Screen("settings")

    // Nouvelles routes V2
    data object Simulation : Screen("simulation?scenarioId={scenarioId}") {
        fun createRoute(scenarioId: String? = null) =
            if (scenarioId != null) "simulation?scenarioId=$scenarioId" else "simulation"
    }
    data object EmailBuilder : Screen("email_builder")
    data object CompetencyRadar : Screen("competency_radar")
}
