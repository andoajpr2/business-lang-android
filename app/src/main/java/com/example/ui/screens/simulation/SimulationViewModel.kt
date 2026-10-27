package com.example.ui.screens.simulation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.SimulationChoice
import com.example.data.model.SimulationScenario
import com.example.data.model.SimulationStep
import com.example.data.repository.PedagogyRepository
import com.example.data.repository.UserDataRepository
import com.example.ui.audio.PronunciationEvaluator
import com.example.ui.audio.PronunciationResult
import com.example.ui.audio.SpeechToTextManager
import com.example.ui.audio.TextToSpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SimulationUiState(
    val scenario: SimulationScenario?,
    val currentStep: SimulationStep?,
    val diplomacyScore: Int = 50,
    val efficiencyScore: Int = 50,
    val selectedChoice: SimulationChoice? = null,
    val showFeedback: Boolean = false,
    val isFinished: Boolean = false,
    val finalSummary: String = "",
    val isVoiceMode: Boolean = false,
    val pronunciationResult: PronunciationResult? = null,
    val targetVoiceChoice: SimulationChoice? = null,
    val visitedStepIds: List<String> = emptyList(),
    val choiceHistory: List<SimulationChoice> = emptyList()
)

class SimulationViewModel(
    private val scenarioId: String?,
    private val pedagogyRepository: PedagogyRepository,
    private val userDataRepository: UserDataRepository,
    val ttsManager: TextToSpeechManager,
    val sttManager: SpeechToTextManager
) : ViewModel() {

    private val scenario = if (scenarioId != null) {
        pedagogyRepository.getSimulationById(scenarioId)
    } else {
        pedagogyRepository.getAllSimulations().firstOrNull()
    }

    private val initialStep = scenario?.steps?.firstOrNull { it.stepId == scenario.initialStepId }
        ?: scenario?.steps?.firstOrNull()

    private val _uiState = MutableStateFlow(
        SimulationUiState(
            scenario = scenario,
            currentStep = initialStep,
            visitedStepIds = listOfNotNull(initialStep?.stepId)
        )
    )
    val uiState: StateFlow<SimulationUiState> = _uiState.asStateFlow()

    fun playDialoguePrompt() {
        val prompt = _uiState.value.currentStep?.dialoguePrompt ?: return
        ttsManager.speak(prompt, "sim_prompt_${prompt.hashCode()}")
    }

    fun selectChoice(choice: SimulationChoice) {
        if (_uiState.value.showFeedback) return

        val newDiplomacy = (_uiState.value.diplomacyScore + choice.diplomacyDelta).coerceIn(0, 100)
        val newEfficiency = (_uiState.value.efficiencyScore + choice.efficiencyDelta).coerceIn(0, 100)

        _uiState.value = _uiState.value.copy(
            selectedChoice = choice,
            showFeedback = true,
            diplomacyScore = newDiplomacy,
            efficiencyScore = newEfficiency,
            choiceHistory = _uiState.value.choiceHistory + choice
        )
    }

    fun proceedToNextStep() {
        val choice = _uiState.value.selectedChoice ?: return
        val currentScenario = _uiState.value.scenario ?: return
        val nextStep = currentScenario.steps.firstOrNull { it.stepId == choice.nextStepId }

        if (nextStep == null || nextStep.isTerminal) {
            val summary = nextStep?.terminalSummary ?: "Fin de la simulation."
            _uiState.value = _uiState.value.copy(
                isFinished = true,
                finalSummary = summary,
                showFeedback = false
            )
            // Enregistrer dans Room : chemin complet de la conversation + bilan détaillé
            viewModelScope.launch {
                val pathText = buildString {
                    _uiState.value.choiceHistory.forEachIndexed { index, c ->
                        append("${index + 1}. ${c.text}")
                        if (index != _uiState.value.choiceHistory.lastIndex) append(" → ")
                    }
                }
                userDataRepository.recordSimulation(
                    scenarioId = currentScenario.id,
                    diplomacy = _uiState.value.diplomacyScore,
                    efficiency = _uiState.value.efficiencyScore,
                    pathSummary = summary + "\nChemin : $pathText"
                )
            }
        } else {
            _uiState.value = _uiState.value.copy(
                currentStep = nextStep,
                selectedChoice = null,
                showFeedback = false,
                targetVoiceChoice = null,
                pronunciationResult = null,
                visitedStepIds = _uiState.value.visitedStepIds + nextStep.stepId
            )
        }
    }

    fun setVoiceMode(enabled: Boolean, choice: SimulationChoice? = null) {
        _uiState.value = _uiState.value.copy(
            isVoiceMode = enabled,
            targetVoiceChoice = choice,
            pronunciationResult = null
        )
    }

    fun startVoiceEvaluation(choice: SimulationChoice) {
        _uiState.value = _uiState.value.copy(targetVoiceChoice = choice)
        sttManager.startListening { spokenText ->
            val result = PronunciationEvaluator.evaluate(choice.text, spokenText)
            _uiState.value = _uiState.value.copy(pronunciationResult = result)

            // Alimente l'axe « Élocution & Oral » du radar de compétences
            viewModelScope.launch {
                userDataRepository.recordPronunciationEvaluation(
                    expressionId = choice.choiceId,
                    spoken = spokenText,
                    accuracy = result.accuracyPercentage
                )
            }

            if (result.accuracyPercentage >= 65) {
                selectChoice(choice)
            }
        }
    }

    /**
     * Revient à la réplique précédente : annule le dernier choix effectué
     * et recalcule les jauges à partir de l'historique restant.
     */
    fun goBackOneStep() {
        val state = _uiState.value
        val currentScenario = state.scenario ?: return
        if (state.showFeedback || state.isFinished || state.visitedStepIds.size < 2) return

        sttManager.stopListening()
        val visited = state.visitedStepIds.dropLast(1)
        val history = state.choiceHistory.dropLast(1)
        val previousStep = currentScenario.steps.firstOrNull { it.stepId == visited.last() } ?: return
        val (diplomacy, efficiency) = replayScores(history)

        _uiState.value = state.copy(
            currentStep = previousStep,
            diplomacyScore = diplomacy,
            efficiencyScore = efficiency,
            selectedChoice = null,
            showFeedback = false,
            targetVoiceChoice = null,
            pronunciationResult = null,
            visitedStepIds = visited,
            choiceHistory = history
        )
    }

    fun restart() {
        sttManager.stopListening()
        _uiState.value = SimulationUiState(
            scenario = scenario,
            currentStep = initialStep,
            diplomacyScore = 50,
            efficiencyScore = 50,
            visitedStepIds = listOfNotNull(initialStep?.stepId)
        )
    }

    companion object {
        const val INITIAL_SCORE = 50

        /** Rejoue les deltas des choix en appliquant le même bornage 0..100 qu'en direct. */
        fun replayScores(history: List<SimulationChoice>): Pair<Int, Int> {
            var diplomacy = INITIAL_SCORE
            var efficiency = INITIAL_SCORE
            history.forEach { choice ->
                diplomacy = (diplomacy + choice.diplomacyDelta).coerceIn(0, 100)
                efficiency = (efficiency + choice.efficiencyDelta).coerceIn(0, 100)
            }
            return diplomacy to efficiency
        }
    }
}
