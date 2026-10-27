# ProLang

Application Android (Kotlin / Jetpack Compose) pour maîtriser la communication professionnelle en français :
parcours de leçons, flashcards à répétition espacée (SM-2), quiz, textes à trous, dialogues audio,
simulateur de conversation avec réponse vocale, générateur d'emails et radar de compétences.
Tout fonctionne hors ligne : contenu dans `app/src/main/assets/pedagogy_data.json`, progression dans Room.

## Prérequis

- JDK 17 ou plus récent
- Android SDK avec la plateforme `android-36` (Android Studio l'installe automatiquement)

## Premier build

1. Indiquer le SDK : créer `local.properties` à la racine avec `sdk.dir=/chemin/vers/Android/sdk`
   (ou définir `ANDROID_HOME`). Android Studio le fait tout seul.
2. Créer le keystore de debug attendu par la configuration de signature (fichier ignoré par git) :

   ```bash
   keytool -genkeypair -keystore debug.keystore -storepass android -alias androiddebugkey \
     -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=Android,C=US"
   ```

3. Compiler et installer :

   ```bash
   ./gradlew assembleDebug          # APK : app/build/outputs/apk/debug/app-debug.apk
   ./gradlew installDebug           # sur un appareil / émulateur connecté
   ./gradlew testDebugUnitTest      # tests unitaires + parcours UI (Robolectric)
   ```

Le build `release` est signé avec `my-upload-key.jks` et les variables d'environnement
`STORE_PASSWORD` / `KEY_PASSWORD` (ou `KEYSTORE_PATH` pour un autre emplacement).

## Architecture

| Dossier | Rôle |
| --- | --- |
| `ProLangApplication.kt` | Dépendances partagées (base Room, dépôts, moteurs TTS / reconnaissance vocale) |
| `data/` | Modèles, entités et DAO Room, dépôts (contenu JSON, données utilisateur, préférences) |
| `ui/navigation/` | Routes et graphe de navigation (4 onglets + écrans secondaires) |
| `ui/screens/` | Un dossier par écran, avec son ViewModel |
| `ui/audio/` | Synthèse vocale, reconnaissance vocale et évaluation de prononciation |

La réponse orale du simulateur demande la permission micro au premier usage ; sans elle,
on peut toujours répondre en touchant une proposition.
