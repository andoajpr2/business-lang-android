package com.example.data.model

/**
 * Texte bilingue pour préparer l'internationalisation FR/EN.
 */
data class LocalizedText(
    val fr: String,
    val en: String
)

/**
 * Module thématique d'apprentissage.
 */
data class Module(
    val id: String,
    val title: String,
    val titleEn: String = "",
    val description: String,
    val descriptionEn: String = "",
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
    val titleEn: String = "",
    val contextSummary: String,
    val contextSummaryEn: String = "",
    val order: Int
)

/**
 * Expression professionnelle avec explication, traduction et exemple.
 */
data class Expression(
    val id: String,
    val lessonId: String,
    val frenchText: String,
    val englishTranslation: String = "",
    val explanation: String,
    val explanationEn: String = "",
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
    val questionOrPromptEn: String = "",
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String,
    val explanationEn: String = ""
)

/**
 * Réplique d'un dialogue professionnel.
 */
data class DialogueLine(
    val speaker: String,
    val role: String,
    val frenchText: String,
    val englishTranslation: String = "",
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

// ================= NOUVELLES STRUCTURES V2 =================

/**
 * Choix disponible lors d'une étape de simulation interactive.
 */
data class SimulationChoice(
    val choiceId: String,
    val text: String,
    val englishTranslation: String = "",
    val diplomacyDelta: Int,
    val efficiencyDelta: Int,
    val nextStepId: String,
    val pedagogicalFeedback: String
)

/**
 * Étape dans un jeu de rôle ou scénario branché.
 */
data class SimulationStep(
    val stepId: String,
    val characterSpeaker: String,
    val characterRole: String = "",
    val dialoguePrompt: String,
    val dialoguePromptEn: String = "",
    val choices: List<SimulationChoice>,
    val isTerminal: Boolean = false,
    val terminalSummary: String = ""
)

/**
 * Scénario complet de simulation (Jeu de rôle pro).
 */
data class SimulationScenario(
    val id: String,
    val title: String,
    val titleEn: String = "",
    val context: String,
    val contextEn: String = "",
    val category: Category,
    val initialStepId: String,
    val steps: List<SimulationStep>
)

/**
 * Niveau de formalisme d'un email professionnel.
 */
enum class EmailFormality(val displayName: String) {
    FORMEL("Formel classique"),
    AGILE("Collaboratif / Direct"),
    DIPLOMATE("Diplomate & Nuancé");

    companion object {
        fun fromString(value: String): EmailFormality = try {
            valueOf(value.uppercase())
        } catch (e: Exception) {
            FORMEL
        }
    }
}

/**
 * Modèle de template d'email professionnel.
 */
data class EmailTemplate(
    val id: String,
    val title: String,
    val titleEn: String = "",
    val category: Category,
    val formalityLevel: EmailFormality,
    val subjectTemplate: String,
    val bodyTemplate: String,
    val placeholders: List<String>,
    val tips: String = ""
)

/**
 * Phrase réflexe du Phrasebook de Survie.
 */
data class SurvivalPhrase(
    val id: String,
    val contextKey: String,
    val situationLabel: String,
    val situationLabelEn: String = "",
    val frenchPhrase: String,
    val englishTranslation: String = "",
    val recommendedSpeed: Float = 0.85f
)

/**
 * Conteneur racine du jeu de données pédagogique V2.
 */
data class PedagogyData(
    val supportedLanguages: List<String> = listOf("fr", "en"),
    val modules: List<Module> = emptyList(),
    val lessons: List<Lesson> = emptyList(),
    val expressions: List<Expression> = emptyList(),
    val exercises: List<Exercise> = emptyList(),
    val dialogues: List<Dialogue> = emptyList(),
    val simulations: List<SimulationScenario> = emptyList(),
    val emailTemplates: List<EmailTemplate> = emptyList(),
    val survivalPhrases: List<SurvivalPhrase> = emptyList()
)
