package com.example.ui.screens.emails

import androidx.lifecycle.ViewModel
import com.example.data.model.EmailTemplate
import com.example.data.repository.PedagogyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class EmailBuilderUiState(
    val templates: List<EmailTemplate> = emptyList(),
    val selectedTemplate: EmailTemplate? = null,
    val fieldValues: Map<String, String> = emptyMap(),
    val generatedSubject: String = "",
    val generatedBody: String = ""
)

class EmailBuilderViewModel(
    private val pedagogyRepository: PedagogyRepository
) : ViewModel() {

    private val allTemplates = pedagogyRepository.getAllEmailTemplates()

    private val _uiState = MutableStateFlow(
        EmailBuilderUiState(
            templates = allTemplates,
            selectedTemplate = allTemplates.firstOrNull()
        )
    )
    val uiState: StateFlow<EmailBuilderUiState> = _uiState.asStateFlow()

    init {
        _uiState.value.selectedTemplate?.let { initFields(it) }
    }

    fun selectTemplate(template: EmailTemplate) {
        _uiState.value = _uiState.value.copy(selectedTemplate = template)
        initFields(template)
    }

    fun updateField(placeholderKey: String, value: String) {
        val updated = _uiState.value.fieldValues.toMutableMap()
        updated[placeholderKey] = value
        val template = _uiState.value.selectedTemplate ?: return

        var subject = template.subjectTemplate
        var body = template.bodyTemplate

        template.placeholders.forEach { key ->
            val placeholderTag = "[$key]"
            val replacement = updated[key]?.ifBlank { placeholderTag } ?: placeholderTag
            subject = subject.replace(placeholderTag, replacement)
            body = body.replace(placeholderTag, replacement)
        }

        _uiState.value = _uiState.value.copy(
            fieldValues = updated,
            generatedSubject = subject,
            generatedBody = body
        )
    }

    private fun initFields(template: EmailTemplate) {
        val initialMap = template.placeholders.associateWith { "" }
        _uiState.value = _uiState.value.copy(
            fieldValues = initialMap,
            generatedSubject = template.subjectTemplate,
            generatedBody = template.bodyTemplate
        )
    }
}
