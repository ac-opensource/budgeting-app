# Stop Being Poor

An Android budgeting application used for tracking accounts, transactions and budgets.

## Build requirements
- Android Studio Hedgehog or later
- Android SDK with API level 36
- JDK 11+
- Gradle 8.13 (use the provided Gradle wrapper)

## Building
Open the project in Android Studio and let it handle the sync.
Alternatively, run the wrapper on the command line:

```bash
./gradlew assembleDebug
```

## Running tests
Unit tests can be executed with:

```bash
./gradlew test
```

Instrumentation tests require a connected device or emulator:

```bash
./gradlew connectedAndroidTest
```

Tests can also be run inside Android Studio using the standard test runners.
