package com.example.ui.audio

import kotlin.math.max
import kotlin.math.min

enum class WordStatus {
    CORRECT,
    APPROXIMATE,
    MISSING_OR_WRONG
}

data class WordEvaluation(
    val word: String,
    val status: WordStatus
)

data class PronunciationResult(
    val accuracyPercentage: Int,
    val wordEvaluations: List<WordEvaluation>,
    val spokenText: String,
    val referenceText: String,
    val feedbackMessage: String
)

object PronunciationEvaluator {

    fun evaluate(reference: String, spoken: String): PronunciationResult {
        val cleanRef = cleanForComparison(reference)
        val cleanSpoken = cleanForComparison(spoken)

        if (cleanSpoken.isBlank()) {
            return PronunciationResult(
                accuracyPercentage = 0,
                wordEvaluations = reference.split("\\s+".toRegex()).map { WordEvaluation(it, WordStatus.MISSING_OR_WRONG) },
                spokenText = spoken,
                referenceText = reference,
                feedbackMessage = "Aucune parole détectée. Veuillez prononcer la phrase à voix haute."
            )
        }

        val refWords = reference.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val spokenWordsClean = cleanSpoken.split("\\s+".toRegex()).filter { it.isNotBlank() }

        val wordEvaluations = mutableListOf<WordEvaluation>()
        var correctCount = 0

        for (refWord in refWords) {
            val cleanWord = cleanForComparison(refWord)
            val matchedExact = spokenWordsClean.any { it == cleanWord }
            val matchedSimilar = if (!matchedExact) {
                spokenWordsClean.any { levenshtein(it, cleanWord) <= (cleanWord.length / 3).coerceAtLeast(1) }
            } else false

            when {
                matchedExact -> {
                    wordEvaluations.add(WordEvaluation(refWord, WordStatus.CORRECT))
                    correctCount += 2
                }
                matchedSimilar -> {
                    wordEvaluations.add(WordEvaluation(refWord, WordStatus.APPROXIMATE))
                    correctCount += 1
                }
                else -> {
                    wordEvaluations.add(WordEvaluation(refWord, WordStatus.MISSING_OR_WRONG))
                }
            }
        }

        val maxPossibleScore = (refWords.size * 2).coerceAtLeast(1)
        val rawPercentage = ((correctCount.toFloat() / maxPossibleScore) * 100).toInt().coerceIn(0, 100)

        // Global Levenshtein check
        val globalLevenshtein = levenshtein(cleanRef, cleanSpoken)
        val maxLen = max(cleanRef.length, cleanSpoken.length).coerceAtLeast(1)
        val stringSimilarity = (((maxLen - globalLevenshtein).toFloat() / maxLen) * 100).toInt().coerceIn(0, 100)

        val finalScore = ((rawPercentage * 0.7f) + (stringSimilarity * 0.3f)).toInt().coerceIn(0, 100)

        val feedback = when {
            finalScore >= 85 -> "Excellente élocution ! Votre prononciation est claire et professionnelle."
            finalScore >= 65 -> "Très bon niveau. Veillez à bien articuler les mots en orange."
            else -> "Effort honorable. Entraînez-vous à écouter le modèle audio avant de répéter."
        }

        return PronunciationResult(
            accuracyPercentage = finalScore,
            wordEvaluations = wordEvaluations,
            spokenText = spoken,
            referenceText = reference,
            feedbackMessage = feedback
        )
    }

    private fun cleanForComparison(text: String): String {
        return text.lowercase()
            .replace("[.,!?;:\"«»()\\-]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }

    private fun levenshtein(lhs: CharSequence, rhs: CharSequence): Int {
        val lhsLength = lhs.length
        val rhsLength = rhs.length

        var cost = IntArray(lhsLength + 1) { it }
        var newCost = IntArray(lhsLength + 1) { 0 }

        for (i in 1..rhsLength) {
            newCost[0] = i
            for (j in 1..lhsLength) {
                val match = if (lhs[j - 1] == rhs[i - 1]) 0 else 1
                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1
                newCost[j] = min(min(costInsert, costDelete), costReplace)
            }
            val swap = cost
            cost = newCost
            newCost = swap
        }

        return cost[lhsLength]
    }
}
