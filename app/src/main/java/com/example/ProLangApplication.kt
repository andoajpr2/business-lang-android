package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.repository.PedagogyRepository
import com.example.data.repository.PreferencesRepository
import com.example.data.repository.UserDataRepository
import com.example.ui.audio.SpeechToTextManager
import com.example.ui.audio.TextToSpeechManager

/**
 * Conteneur des dépendances à l'échelle de l'application.
 *
 * Les ViewModels survivent aux recréations de l'Activity (rotation, changement de thème…) :
 * les moteurs audio qu'ils utilisent doivent donc vivre aussi longtemps que le processus,
 * et non être détruits dans Activity.onDestroy().
 */
class ProLangApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val pedagogyRepository: PedagogyRepository by lazy { PedagogyRepository(this) }
    val userDataRepository: UserDataRepository by lazy { UserDataRepository(database) }
    val preferencesRepository: PreferencesRepository by lazy { PreferencesRepository(this) }

    // SpeechRecognizer doit être créé sur le thread principal : premier accès depuis MainActivity.onCreate
    val ttsManager: TextToSpeechManager by lazy {
        TextToSpeechManager(this).apply { setSpeechRate(preferencesRepository.speechRate.value) }
    }
    val sttManager: SpeechToTextManager by lazy { SpeechToTextManager(this) }
}
