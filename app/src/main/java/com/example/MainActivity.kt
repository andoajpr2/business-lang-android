package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.data.local.AppDatabase
import com.example.data.repository.PedagogyRepository
import com.example.data.repository.PreferencesRepository
import com.example.data.repository.UserDataRepository
import com.example.ui.audio.SpeechToTextManager
import com.example.ui.audio.TextToSpeechManager
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.FrancaisProTheme

class MainActivity : ComponentActivity() {

    private lateinit var ttsManager: TextToSpeechManager
    private lateinit var sttManager: SpeechToTextManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialisation des couches de données locales et audio/voix
        val database = AppDatabase.getInstance(this)
        val pedagogyRepository = PedagogyRepository(this)
        val userDataRepository = UserDataRepository(database)
        val preferencesRepository = PreferencesRepository(this)
        ttsManager = TextToSpeechManager(this)
        sttManager = SpeechToTextManager(this)

        // Appliquer la vitesse de lecture sauvegardée
        ttsManager.setSpeechRate(preferencesRepository.speechRate.value)

        setContent {
            val themeMode by preferencesRepository.themeMode.collectAsState()

            FrancaisProTheme(themeMode = themeMode) {
                AppNavigation(
                    pedagogyRepository = pedagogyRepository,
                    userDataRepository = userDataRepository,
                    preferencesRepository = preferencesRepository,
                    ttsManager = ttsManager,
                    sttManager = sttManager
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::ttsManager.isInitialized) {
            ttsManager.shutdown()
        }
        if (::sttManager.isInitialized) {
            sttManager.shutdown()
        }
    }
}
