package com.example

import com.example.data.model.SimulationChoice
import com.example.ui.audio.PronunciationEvaluator
import com.example.ui.screens.simulation.SimulationViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulationLogicTest {

  private fun choice(diplomacy: Int, efficiency: Int) =
      SimulationChoice(
          choiceId = "c",
          text = "",
          diplomacyDelta = diplomacy,
          efficiencyDelta = efficiency,
          nextStepId = "",
          pedagogicalFeedback = "")

  @Test
  fun `replay without history returns initial scores`() {
    assertEquals(50 to 50, SimulationViewModel.replayScores(emptyList()))
  }

  @Test
  fun `replay applies the same clamping as live play`() {
    val history = listOf(choice(40, -60), choice(30, 10))
    // 50+40=90 → 90+30=120 borné à 100 ; 50-60 borné à 0 → 0+10=10
    assertEquals(100 to 10, SimulationViewModel.replayScores(history))
  }

  @Test
  fun `identical sentence scores near 100`() {
    val result =
        PronunciationEvaluator.evaluate(
            "Je vous propose de revoir le planning.", "je vous propose de revoir le planning")
    assertTrue(result.accuracyPercentage >= 95)
  }

  @Test
  fun `empty speech scores zero`() {
    assertEquals(0, PronunciationEvaluator.evaluate("Bonjour à tous", "").accuracyPercentage)
  }
}
