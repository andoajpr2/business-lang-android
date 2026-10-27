package com.example.ui.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TextToSpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _currentlySpeakingId = MutableStateFlow<String?>(null)
    val currentlySpeakingId: StateFlow<String?> = _currentlySpeakingId.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private var currentRate: Float = 1.0f

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("TextToSpeechManager", "Error initializing TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.FRENCH)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Essayer Locale.FRANCE ou fallback général
                tts?.setLanguage(Locale.FRANCE)
            }
            tts?.setSpeechRate(currentRate)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _currentlySpeakingId.value = utteranceId
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    if (_currentlySpeakingId.value == utteranceId) {
                        _currentlySpeakingId.value = null
                        _isSpeaking.value = false
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    if (_currentlySpeakingId.value == utteranceId) {
                        _currentlySpeakingId.value = null
                        _isSpeaking.value = false
                    }
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    if (_currentlySpeakingId.value == utteranceId) {
                        _currentlySpeakingId.value = null
                        _isSpeaking.value = false
                    }
                }
            })
            isInitialized = true
        } else {
            Log.e("TextToSpeechManager", "TextToSpeech init failed with status: $status")
        }
    }

    fun speak(text: String, utteranceId: String = text.hashCode().toString()) {
        if (!isInitialized || tts == null) {
            return
        }
        if (_currentlySpeakingId.value == utteranceId && _isSpeaking.value) {
            stop()
            return
        }
        stop()
        tts?.setSpeechRate(currentRate)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        tts?.stop()
        _currentlySpeakingId.value = null
        _isSpeaking.value = false
    }

    fun setSpeechRate(rate: Float) {
        currentRate = rate
        if (isInitialized) {
            tts?.setSpeechRate(rate)
        }
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
