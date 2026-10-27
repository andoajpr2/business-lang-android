package com.example.data.model

/**
 * Module thématique d'apprentissage.
 */
data class Module(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val level: String,
    val lessonCount: Int = 0
)

/**
 * Leçon structurée au sein d'un module.
 */
data class Lesson(
    val id: String,
    val moduleId: String,
    val title: String,
    val contextSummary: String,
    val order: Int
)

/**
 * Expression professionnelle avec explication et exemple de mise en situation.
 */
data class Expression(
    val id: String,
    val lessonId: String,
    val frenchText: String,
    val explanation: String,
    val exampleUsage: String,
    val category: Category,
    val level: DifficultyLevel,
    val keywords: List<String>
)

/**
 * Exercice pédagogique (QCM ou texte à trous).
 */
data class Exercise(
    val id: String,
    val lessonId: String,
    val type: ExerciseType,
    val questionOrPrompt: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String
)

/**
 * Réplique d'un dialogue professionnel.
 */
data class DialogueLine(
    val speaker: String,
    val role: String,
    val frenchText: String,
    val notes: String
)

/**
 * Dialogue complet en situation de travail.
 */
data class Dialogue(
    val id: String,
    val title: String,
    val situationContext: String,
    val lines: List<DialogueLine>
)

/**
 * Conteneur racine du jeu de données pédagogique.
 */
data class PedagogyData(
    val modules: List<Module>,
    val lessons: List<Lesson>,
    val expressions: List<Expression>,
    val exercises: List<Exercise>,
    val dialogues: List<Dialogue>
)
