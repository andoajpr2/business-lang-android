package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Parcours complet : onglet Pratique → liste des scénarios → simulation jouée jusqu'au bilan. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SimulationFlowTest {

  @get:Rule val composeRule = createAndroidComposeRule<MainActivity>()

  private fun click(tag: String) {
    composeRule.onNodeWithTag(tag).performScrollTo().performClick()
    composeRule.waitForIdle()
  }

  @Test
  fun `play a scenario with undo until the debriefing`() {
    composeRule.onNodeWithTag("bottom_tab_practice").performClick()
    composeRule.onNodeWithTag("practice_simulation_card").performClick()
    composeRule.onNodeWithTag("simulation_list_screen").assertIsDisplayed()
    composeRule.onNodeWithTag("scenario_card_sim_reunion_arbitrage").performClick()
    composeRule.onNodeWithTag("simulation_screen").assertIsDisplayed()

    // Étape 1 : bonne réponse (+25 diplomatie)
    click("sim_choice_c2")
    composeRule.onAllNodesWithText("75%").onFirst().assertExists()
    click("sim_continue_btn")

    // Étape 2 : on revient en arrière, les jauges reviennent à 50 %
    composeRule.onNodeWithTag("sim_back_step_btn").performClick()
    composeRule.waitForIdle()
    composeRule.onNodeWithTag("sim_choice_c1").assertExists()
    composeRule.onAllNodesWithText("50%").onFirst().assertExists()

    // On rejoue jusqu'à la fin
    click("sim_choice_c2")
    click("sim_continue_btn")
    click("sim_choice_c4")
    click("sim_continue_btn")
    composeRule.onNodeWithText("Simulation terminée !").assertIsDisplayed()
    composeRule.onNodeWithTag("simulation_restart_btn").assertExists()
  }
}
