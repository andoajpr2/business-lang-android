package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        fun fromString(value: String): ThemeMode {
            return try {
                valueOf(value)
            } catch (e: Exception) {
                SYSTEM
            }
        }
    }
}

class PreferencesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("francais_pro_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(
        ThemeMode.fromString(prefs.getString(KEY_THEME, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name)
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _speechRate = MutableStateFlow(
        prefs.getFloat(KEY_SPEECH_RATE, 1.0f)
    )
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _uiLanguage = MutableStateFlow(
        prefs.getString(KEY_UI_LANGUAGE, "fr") ?: "fr"
    )
    val uiLanguage: StateFlow<String> = _uiLanguage.asStateFlow()

    private val _targetLanguage = MutableStateFlow(
        prefs.getString(KEY_TARGET_LANGUAGE, "fr") ?: "fr"
    )
    val targetLanguage: StateFlow<String> = _targetLanguage.asStateFlow()

    fun setUiLanguage(lang: String) {
        prefs.edit().putString(KEY_UI_LANGUAGE, lang).apply()
        _uiLanguage.value = lang
    }

    fun setTargetLanguage(lang: String) {
        prefs.edit().putString(KEY_TARGET_LANGUAGE, lang).apply()
        _targetLanguage.value = lang
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME, mode.name).apply()
        _themeMode.value = mode
    }

    fun setSpeechRate(rate: Float) {
        val clampedRate = rate.coerceIn(0.5f, 2.0f)
        prefs.edit().putFloat(KEY_SPEECH_RATE, clampedRate).apply()
        _speechRate.value = clampedRate
    }

    companion object {
        private const val KEY_THEME = "key_theme_mode"
        private const val KEY_SPEECH_RATE = "key_speech_rate"
        private const val KEY_UI_LANGUAGE = "key_ui_language"
        private const val KEY_TARGET_LANGUAGE = "key_target_language"
    }
}
