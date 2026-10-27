package com.example.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.PreferencesRepository
import com.example.data.repository.ThemeMode
import com.example.data.repository.UserDataRepository
import com.example.ui.audio.TextToSpeechManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val userDataRepository: UserDataRepository,
    private val ttsManager: TextToSpeechManager
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = preferencesRepository.themeMode
    val speechRate: StateFlow<Float> = preferencesRepository.speechRate

    fun setThemeMode(mode: ThemeMode) {
        preferencesRepository.setThemeMode(mode)
    }

    fun setSpeechRate(rate: Float) {
        preferencesRepository.setSpeechRate(rate)
        ttsManager.setSpeechRate(rate)
    }

    fun testAudio() {
        ttsManager.speak("Bonjour, ceci est un test de la synthèse vocale en français professionnel.", "test_tts")
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            userDataRepository.resetAllProgress()
        }
    }
}
