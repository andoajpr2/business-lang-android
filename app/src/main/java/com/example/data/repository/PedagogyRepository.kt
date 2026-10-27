package com.example.data.repository

import android.content.Context
import com.example.data.model.Category
import com.example.data.model.Dialogue
import com.example.data.model.DialogueLine
import com.example.data.model.DifficultyLevel
import com.example.data.model.Exercise
import com.example.data.model.ExerciseType
import com.example.data.model.Expression
import com.example.data.model.Lesson
import com.example.data.model.Module
import com.example.data.model.PedagogyData
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar

class PedagogyRepository(private val context: Context) {

    private var cachedData: PedagogyData? = null

    @Synchronized
    fun getPedagogyData(): PedagogyData {
        cachedData?.let { return it }

        val data = loadDataFromAssets()
        cachedData = data
        return data
    }

    private fun loadDataFromAssets(): PedagogyData {
        val jsonString = try {
            val inputStream = context.assets.open("pedagogy_data.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val sb = java.lang.StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            reader.close()
            sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            "{}"
        }

        val jsonObject = JSONObject(jsonString)

        // Parse Modules
        val modulesList = mutableListOf<Module>()
        val modulesArray = jsonObject.optJSONArray("modules")
        if (modulesArray != null) {
            for (i in 0 until modulesArray.length()) {
                val item = modulesArray.getJSONObject(i)
                modulesList.add(
                    Module(
                        id = item.optString("id"),
                        title = item.optString("title"),
                        description = item.optString("description"),
                        iconName = item.optString("iconName"),
                        level = item.optString("level")
                    )
                )
            }
        }

        // Parse Lessons
        val lessonsList = mutableListOf<Lesson>()
        val lessonsArray = jsonObject.optJSONArray("lessons")
        if (lessonsArray != null) {
            for (i in 0 until lessonsArray.length()) {
                val item = lessonsArray.getJSONObject(i)
                lessonsList.add(
                    Lesson(
                        id = item.optString("id"),
                        moduleId = item.optString("moduleId"),
                        title = item.optString("title"),
                        contextSummary = item.optString("contextSummary"),
                        order = item.optInt("order", 1)
                    )
                )
            }
        }

        // Attach lesson count to modules
        val modulesWithCount = modulesList.map { mod ->
            mod.copy(lessonCount = lessonsList.count { it.moduleId == mod.id })
        }

        // Parse Expressions
        val expressionsList = mutableListOf<Expression>()
        val expressionsArray = jsonObject.optJSONArray("expressions")
        if (expressionsArray != null) {
            for (i in 0 until expressionsArray.length()) {
                val item = expressionsArray.getJSONObject(i)
                val keywordsList = mutableListOf<String>()
                val kwArray = item.optJSONArray("keywords")
                if (kwArray != null) {
                    for (k in 0 until kwArray.length()) {
                        keywordsList.add(kwArray.getString(k))
                    }
                }
                expressionsList.add(
                    Expression(
                        id = item.optString("id"),
                        lessonId = item.optString("lessonId"),
                        frenchText = item.optString("frenchText"),
                        explanation = item.optString("explanation"),
                        exampleUsage = item.optString("exampleUsage"),
                        category = Category.fromString(item.optString("category")),
                        level = DifficultyLevel.fromString(item.optString("level")),
                        keywords = keywordsList
                    )
                )
            }
        }

        // Parse Exercises
        val exercisesList = mutableListOf<Exercise>()
        val exercisesArray = jsonObject.optJSONArray("exercises")
        if (exercisesArray != null) {
            for (i in 0 until exercisesArray.length()) {
                val item = exercisesArray.getJSONObject(i)
                val optList = mutableListOf<String>()
                val optArray = item.optJSONArray("options")
                if (optArray != null) {
                    for (o in 0 until optArray.length()) {
                        optList.add(optArray.getString(o))
                    }
                }
                exercisesList.add(
                    Exercise(
                        id = item.optString("id"),
                        lessonId = item.optString("lessonId"),
                        type = ExerciseType.fromString(item.optString("type")),
                        questionOrPrompt = item.optString("questionOrPrompt"),
                        options = optList,
                        correctAnswer = item.optString("correctAnswer"),
                        explanation = item.optString("explanation")
                    )
                )
            }
        }

        // Parse Dialogues
        val dialoguesList = mutableListOf<Dialogue>()
        val dialoguesArray = jsonObject.optJSONArray("dialogues")
        if (dialoguesArray != null) {
            for (i in 0 until dialoguesArray.length()) {
                val item = dialoguesArray.getJSONObject(i)
                val linesList = mutableListOf<DialogueLine>()
                val linesArray = item.optJSONArray("lines")
                if (linesArray != null) {
                    for (l in 0 until linesArray.length()) {
                        val lineItem = linesArray.getJSONObject(l)
                        linesList.add(
                            DialogueLine(
                                speaker = lineItem.optString("speaker"),
                                role = lineItem.optString("role"),
                                frenchText = lineItem.optString("frenchText"),
                                notes = lineItem.optString("notes")
                            )
                        )
                    }
                }
                dialoguesList.add(
                    Dialogue(
                        id = item.optString("id"),
                        title = item.optString("title"),
                        situationContext = item.optString("situationContext"),
                        lines = linesList
                    )
                )
            }
        }

        return PedagogyData(
            modules = modulesWithCount,
            lessons = lessonsList,
            expressions = expressionsList,
            exercises = exercisesList,
            dialogues = dialoguesList
        )
    }

    fun getAllModules(): List<Module> = getPedagogyData().modules

    fun getModuleById(moduleId: String): Module? =
        getPedagogyData().modules.firstOrNull { it.id == moduleId }

    fun getLessonsForModule(moduleId: String): List<Lesson> =
        getPedagogyData().lessons.filter { it.moduleId == moduleId }.sortedBy { it.order }

    fun getLessonById(lessonId: String): Lesson? =
        getPedagogyData().lessons.firstOrNull { it.id == lessonId }

    fun getExpressionsForLesson(lessonId: String): List<Expression> =
        getPedagogyData().expressions.filter { it.lessonId == lessonId }

    fun getAllExpressions(): List<Expression> = getPedagogyData().expressions

    fun getExpressionById(id: String): Expression? =
        getPedagogyData().expressions.firstOrNull { it.id == id }

    fun getDailyFeaturedExpression(): Expression {
        val expressions = getAllExpressions()
        if (expressions.isEmpty()) {
            return Expression(
                id = "default",
                lessonId = "",
                frenchText = "Bonjour, auriez-vous un instant pour échanger ?",
                explanation = "Formule de politesse professionnelle.",
                exampleUsage = "Bonjour, auriez-vous un instant pour échanger sur le dossier ?",
                category = Category.BUREAU,
                level = DifficultyLevel.DEBUTANT,
                keywords = listOf("bonjour", "échange")
            )
        }
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val index = (dayOfYear % expressions.size).coerceAtLeast(0)
        return expressions[index]
    }

    fun searchExpressions(
        query: String,
        category: Category? = null,
        level: DifficultyLevel? = null
    ): List<Expression> {
        val cleanQuery = query.trim().lowercase()
        return getAllExpressions().filter { exp ->
            val matchesCategory = category == null || exp.category == category
            val matchesLevel = level == null || exp.level == level
            val matchesQuery = cleanQuery.isEmpty() ||
                exp.frenchText.lowercase().contains(cleanQuery) ||
                exp.explanation.lowercase().contains(cleanQuery) ||
                exp.exampleUsage.lowercase().contains(cleanQuery) ||
                exp.keywords.any { it.lowercase().contains(cleanQuery) }

            matchesCategory && matchesLevel && matchesQuery
        }
    }

    fun getExercisesForLesson(lessonId: String): List<Exercise> =
        getPedagogyData().exercises.filter { it.lessonId == lessonId }

    fun getAllExercises(): List<Exercise> = getPedagogyData().exercises

    fun getMCQExercises(lessonId: String? = null): List<Exercise> =
        getPedagogyData().exercises.filter {
            it.type == ExerciseType.MCQ && (lessonId == null || it.lessonId == lessonId)
        }

    fun getFillBlanksExercises(lessonId: String? = null): List<Exercise> =
        getPedagogyData().exercises.filter {
            it.type == ExerciseType.FILL_BLANKS && (lessonId == null || it.lessonId == lessonId)
        }

    fun getAllDialogues(): List<Dialogue> = getPedagogyData().dialogues

    fun getDialogueById(id: String): Dialogue? =
        getPedagogyData().dialogues.firstOrNull { it.id == id }
}
