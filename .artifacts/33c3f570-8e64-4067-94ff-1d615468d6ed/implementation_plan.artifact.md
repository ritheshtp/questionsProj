# Fix "Android BaseExtension not found" Error

The error `Android BaseExtension not found` occurs during Gradle sync because the Hilt Gradle plugin (version `2.57.1`) is incompatible with the new DSL structure introduced in Android Gradle Plugin (AGP) `9.3.1`. Specifically, `BaseExtension` has been hidden or removed in favor of newer DSL interfaces, and older Hilt versions still depend on it.

## Proposed Changes

### 1. Update Hilt to 2.60.1
Upgrading to the latest stable Hilt version (`2.60.1`) is the recommended fix, as newer versions are designed to be compatible with recent AGP releases.

#### [MODIFY] [libs.versions.toml](file:///Users/rithesh/questions/gradle/libs.versions.toml)
- Add `hilt = "2.60.1"` to the `[versions]` block.
- Add `hilt-android` and `hilt-compiler` to the `[libraries]` block for consistency.
- Add `hilt` to the `[plugins]` block.

#### [MODIFY] [build.gradle.kts](file:///Users/rithesh/questions/build.gradle.kts)
- Use `alias(libs.plugins.hilt) apply false` instead of the hardcoded plugin ID and version.

#### [MODIFY] [app/build.gradle.kts](file:///Users/rithesh/questions/app/build.gradle.kts)
- Use `alias(libs.plugins.hilt)` in the `plugins` block.
- Update dependencies to use the catalog: `libs.hilt.android` and `libs.hilt.compiler`.

### 2. Align KSP and Kotlin Versions
The current KSP version (`2.0.20-1.0.24`) does not match the Kotlin version (`2.2.10`). While not the direct cause of the Hilt error, this version mismatch will cause other build issues.

#### [MODIFY] [libs.versions.toml](file:///Users/rithesh/questions/gradle/libs.versions.toml)
- Update `kotlinSerialization` to `2.2.10` to match `kotlin`.
- Update KSP plugin version to `2.2.10-1.0.28` (or the latest compatible with Kotlin 2.2.10).

### 3. Workaround: Disable New DSL (if needed)
If the Hilt upgrade alone does not resolve the issue (due to AGP 9.3.1 being very new), we will temporarily opt-out of the new DSL.

#### [MODIFY] [gradle.properties](file:///Users/rithesh/questions/gradle.properties)
- Add `android.newDsl=false`.

## Verification Plan

### Automated Tests
- Run `gradle sync` to verify that the `Android BaseExtension not found` error is resolved.
- Run `./gradlew app:assembleDebug` to ensure the project builds correctly with Hilt and KSP.

### Manual Verification
- Verify that the Hilt-generated classes are present in the build folder.
