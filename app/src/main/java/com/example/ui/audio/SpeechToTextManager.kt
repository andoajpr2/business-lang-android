package com.example.ui.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechToTextManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var currentCallback: ((String) -> Unit)? = null

    init {
        initRecognizer()
    }

    private fun initRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            try {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            _isListening.value = true
                            _errorMessage.value = null
                        }

                        override fun onBeginningOfSpeech() {}

                        override fun onRmsChanged(rmsdB: Float) {}

                        override fun onBufferReceived(buffer: ByteArray?) {}

                        override fun onEndOfSpeech() {
                            _isListening.value = false
                        }

                        override fun onError(error: Int) {
                            _isListening.value = false
                            val msg = when (error) {
                                SpeechRecognizer.ERROR_AUDIO -> "Erreur audio"
                                SpeechRecognizer.ERROR_CLIENT -> "Erreur client"
                                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permission micro requise"
                                SpeechRecognizer.ERROR_NETWORK -> "Erreur réseau"
                                SpeechRecognizer.ERROR_NO_MATCH -> "Aucune voix détectée, veuillez réessayer"
                                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Reconnaissance vocale occupée"
                                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Délai d'élocution dépassé"
                                else -> "Erreur de reconnaissance vocale ($error)"
                            }
                            _errorMessage.value = msg
                            Log.w("SpeechToTextManager", "Speech recognition error: $msg")
                        }

                        override fun onResults(results: Bundle?) {
                            _isListening.value = false
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val bestResult = matches?.firstOrNull() ?: ""
                            _recognizedText.value = bestResult
                            currentCallback?.invoke(bestResult)
                        }

                        override fun onPartialResults(partialResults: Bundle?) {
                            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            matches?.firstOrNull()?.let {
                                _recognizedText.value = it
                            }
                        }

                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }
            } catch (e: Exception) {
                Log.e("SpeechToTextManager", "Failed to create SpeechRecognizer", e)
            }
        } else {
            _errorMessage.value = "Reconnaissance vocale non disponible sur cet appareil"
        }
    }

    fun startListening(locale: Locale = Locale.FRENCH, onResult: (String) -> Unit) {
        currentCallback = onResult
        _recognizedText.value = ""
        _errorMessage.value = null

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }

        try {
            speechRecognizer?.startListening(intent)
            _isListening.value = true
        } catch (e: Exception) {
            _isListening.value = false
            _errorMessage.value = "Impossible de démarrer l'écoute : ${e.localizedMessage}"
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e("SpeechToTextManager", "Error stopping listener", e)
        }
        _isListening.value = false
    }

    fun shutdown() {
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.e("SpeechToTextManager", "Error destroying speech recognizer", e)
        }
        speechRecognizer = null
    }
}
