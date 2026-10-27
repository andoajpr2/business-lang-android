package com.example.data.model

/**
 * Catégories professionnelles pour contextualiser chaque expression.
 */
enum class Category(val displayName: String, val iconName: String) {
    BUREAU("Bureau & Équipe", "business"),
    REUNION("Réunions", "groups"),
    CLIENT("Relation Client", "handshake"),
    EMAIL("Emails & Écrits", "mail"),
    APPEL("Appels Pro", "phone"),
    TECHNIQUE("Technique & Projets", "code");

    companion object {
        fun fromString(value: String): Category {
            return try {
                valueOf(value.uppercase())
            } catch (e: Exception) {
                BUREAU
            }
        }
    }
}

/**
 * Niveaux de langue professionnelle.
 */
enum class DifficultyLevel(val displayName: String) {
    DEBUTANT("Débutant"),
    INTERMEDIAIRE("Intermédiaire"),
    AVANCE("Avancé");

    companion object {
        fun fromString(value: String): DifficultyLevel {
            return try {
                valueOf(value.uppercase())
            } catch (e: Exception) {
                INTERMEDIAIRE
            }
        }
    }
}

/**
 * Types d'exercices d'apprentissage.
 */
enum class ExerciseType {
    MCQ,
    FILL_BLANKS;

    companion object {
        fun fromString(value: String): ExerciseType {
            return try {
                valueOf(value.uppercase())
            } catch (e: Exception) {
                MCQ
            }
        }
    }
}
