# NPU-X

NPU-X is an experimental Android application for investigating AI
acceleration hardware on Qualcomm-based devices.

The project was created with a practical objective: identify the
device's AI/NPU infrastructure and, ultimately, verify whether a local
neural-network inference can actually be executed on the Qualcomm
Hexagon/HTP rather than falling back to the CPU or GPU.

## Current status

### v0.1 --- Initial working APK

The first version successfully builds directly on the target POCO X5 Pro
5G using Termux and runs as an Android APK.

Current diagnostic information includes:

-   Device model
-   Manufacturer
-   Android version
-   SDK level
-   Hardware identifier
-   Board/codename

Example target device:

-   Device: POCO X5 Pro 5G
-   Model: `22101320G`
-   SoC: Snapdragon 778G / SM7325
-   Board: `redwood`
-   Android: 14
-   Hardware: `qcom`

## Roadmap

The project is intentionally being developed in stages.

``` text
Device identification
        ↓
Android AI capability detection
        ↓
NNAPI / Qualcomm runtime investigation
        ↓
HTP/NPU backend detection
        ↓
Small neural-network inference
        ↓
Verify actual NPU execution
        ↓
Small local LLM
        ↓
Local chatbot
```

The most important milestone is not simply detecting Qualcomm libraries
or reporting that an NPU exists. The goal is to perform an actual
inference and establish which hardware/backend executed it.

## Development environment

The project is currently built directly on Android using Termux.

Relevant components:

-   Android SDK
-   Gradle 8.7
-   Android Gradle Plugin 8.6.1
-   Kotlin
-   Java 21
-   ARM64 AAPT2 supplied by Termux

Because the development environment is ARM64, the standard x86-64 AAPT2
distributed through the Android SDK cannot be executed natively. NPU-X
therefore uses the ARM64 AAPT2 provided by Termux.

## Project structure

``` text
NPU-X/
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/
│           │   └── com/npux/app/
│           │       └── MainActivity.kt
│           └── res/
│               └── values/
│                   └── styles.xml
├── build.gradle
├── settings.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── gradle/
│   └── wrapper/
└── .gitignore
```

## Building

From the project directory:

``` bash
./gradlew assembleDebug
```

The resulting APK is generated at:

``` text
app/build/outputs/apk/debug/app-debug.apk
```

To clean the project:

``` bash
./gradlew clean
```

## Installation

With ADB available:

``` bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

The `-r` option reinstalls the application while preserving its existing
application data.

## Git

Generated build files and APKs are ignored by Git.

The Gradle wrapper itself is intentionally tracked:

``` text
gradlew
gradlew.bat
gradle/wrapper/
```

Stable versions can be marked with Git tags such as:

``` bash
git tag v0.1
git push origin v0.1
```

APK binaries can be distributed through GitHub Releases instead of being
stored in the Git history.

## Why NPU-X?

Modern Qualcomm SoCs contain dedicated AI acceleration hardware, but the
presence of hardware or vendor libraries alone does not prove that an
application can use it.

NPU-X exists to investigate that complete path on real Android hardware:

``` text
Application
    ↓
Android / Qualcomm AI API
    ↓
Runtime / Driver
    ↓
Hexagon HTP
    ↓
AI inference
```

The project will favor measurable hardware tests over assumptions based
only on device specifications.

## Disclaimer

NPU-X is an experimental research and learning project. Hardware
acceleration support depends on the device, Android version, vendor
implementation, drivers, and available runtime APIs.

No claim of NPU execution should be made until an actual inference test
provides sufficient evidence.

## License

License not yet defined.
