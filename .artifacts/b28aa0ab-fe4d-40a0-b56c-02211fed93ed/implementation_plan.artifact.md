# Implementation Plan - Add SingletonModule with Json Formatter to Core Module

This plan outlines the steps to add a Hilt `SingletonModule` to the `core` module, providing a `Json` formatter instance using Kotlinx Serialization.

## User Review Required

> [!IMPORTANT]
> The plan assumes Hilt is the intended dependency injection framework, as `SingletonModule` is a common pattern in Hilt.
> The `Json` formatter will be configured with `ignoreUnknownKeys = true` by default.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///Users/rithesh/questions/gradle/libs.versions.toml)
- Add `kotlinx-serialization-json` library.
- Add `hilt-android` and `hilt-compiler` libraries.

#### [MODIFY] [build.gradle.kts (core)](file:///Users/rithesh/questions/core/build.gradle.kts)
- Apply `com.google.dagger.hilt.android` and `com.google.devtools.ksp` plugins.
- Add dependencies for Hilt and Kotlinx Serialization JSON.

### Core Module Implementation

#### [NEW] [SingletonModule.kt](file:///Users/rithesh/questions/core/src/main/java/com/rith/core/di/SingletonModule.kt)
- Create a Hilt module in the `com.rith.core.di` package.
- Provide a singleton `Json` instance.

## Verification Plan

### Automated Tests
- Run `./gradlew :core:assembleDebug` to ensure the module builds correctly.
- (Optional) Create a simple unit test in `core` to verify `Json` injection if requested.

### Manual Verification
- Verify that the `core` module now exposes a `Json` instance that can be used in other modules (like `app`) if they depend on `core`.
