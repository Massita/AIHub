# AI Hub

AI Hub is an Android app for configuring AI models and using them to browse the web, search for information, and run guided tasks on the device.

## Current setup

- `Jetpack Compose` is configured as the UI toolkit for the app.
- `Material 3` is used for the default design system and screen scaffolding.
- `Koin` is configured for dependency injection and currently provides the main screen state through `MainViewModel`.
- `MainActivity` renders a Compose-based landing screen that introduces the app's core capabilities:
  - AI model setup
  - web navigation
  - device task execution
  - workspace memory

## Project structure

- `app/src/main/java/com/massita/aihub/AiHubApplication.kt`
  Starts Koin and loads the application module.
- `app/src/main/java/com/massita/aihub/di/AppModule.kt`
  Declares Koin dependencies for the app.
- `app/src/main/java/com/massita/aihub/MainActivity.kt`
  Hosts the Compose UI for the main dashboard.
- `app/src/main/java/com/massita/aihub/ui/main/MainViewModel.kt`
  Exposes the main screen UI state.
- `app/src/main/java/com/massita/aihub/ui/theme/AiHubTheme.kt`
  Defines the Compose Material 3 color theme.

## Requirements

- Android Studio with current Android SDK support
- JDK 17
- Android SDK 36

## Build and run

1. Open the project in Android Studio.
2. Let Gradle sync the new Compose and Koin dependencies.
3. Run the `app` configuration on an emulator or Android device.

If you prefer the command line, use:

```bash
./gradlew assembleDebug
```

## Next implementation steps

- Add a model provider setup flow for local and remote models.
- Introduce a navigation graph for onboarding, models, search, and task execution.
- Add persistence for user settings, task history, and saved providers.
- Connect real web browsing and task orchestration services behind the Koin module.
