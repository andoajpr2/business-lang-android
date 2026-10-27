package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.PedagogyRepository
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PedagogyDataTest {

  private val repository =
      PedagogyRepository(ApplicationProvider.getApplicationContext<Context>())

  @Test
  fun `assets json is loaded with content`() {
    val data = repository.getPedagogyData()
    assertTrue(data.modules.isNotEmpty())
    assertTrue(data.lessons.isNotEmpty())
    assertTrue(data.expressions.isNotEmpty())
    assertTrue(data.exercises.isNotEmpty())
    assertTrue(data.simulations.isNotEmpty())
    assertTrue(data.emailTemplates.isNotEmpty())
  }

  @Test
  fun `every lesson belongs to an existing module`() {
    val moduleIds = repository.getAllModules().map { it.id }.toSet()
    repository.getPedagogyData().lessons.forEach {
      assertTrue("Leçon ${it.id} : module ${it.moduleId} inconnu", it.moduleId in moduleIds)
    }
  }

  @Test
  fun `mcq correct answers are among the options`() {
    repository.getMCQExercises().forEach {
      assertTrue("Exercice ${it.id} : réponse absente des options", it.correctAnswer in it.options)
    }
  }

  @Test
  fun `simulation graphs have no dangling step`() {
    repository.getAllSimulations().forEach { scenario ->
      val stepIds = scenario.steps.map { it.stepId }.toSet()
      assertTrue("${scenario.id} : étape initiale inconnue", scenario.initialStepId in stepIds)
      scenario.steps.forEach { step ->
        assertTrue(
            "${scenario.id}/${step.stepId} : étape sans issue",
            step.isTerminal || step.choices.isNotEmpty())
        step.choices.forEach { choice ->
          assertTrue(
              "${scenario.id}/${step.stepId} → ${choice.nextStepId} introuvable",
              choice.nextStepId in stepIds)
        }
      }
      assertNotNull(repository.getSimulationById(scenario.id))
    }
  }
}
