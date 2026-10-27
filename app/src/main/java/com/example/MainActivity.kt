package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.FrancaisProTheme

class MainActivity : ComponentActivity() {

    private val app: ProLangApplication
        get() = application as ProLangApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Dépendances partagées à l'échelle de l'application (survivent aux rotations)
        val preferencesRepository = app.preferencesRepository

        setContent {
            val themeMode by preferencesRepository.themeMode.collectAsState()

            FrancaisProTheme(themeMode = themeMode) {
                AppNavigation(
                    pedagogyRepository = app.pedagogyRepository,
                    userDataRepository = app.userDataRepository,
                    preferencesRepository = preferencesRepository,
                    ttsManager = app.ttsManager,
                    sttManager = app.sttManager
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // L'application passe en arrière-plan : on coupe la lecture et l'écoute en cours
        app.ttsManager.stop()
        app.sttManager.stopListening()
    }
}
