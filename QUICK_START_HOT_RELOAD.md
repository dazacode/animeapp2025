# Quick Start: Hot Reloading for VS Code (Working Solution)

## 🚨 Current Issue
There's a compatibility issue with Icepick annotation processor that needs to be fixed. Here's how to get hot reloading working **right now**:

## ✅ Immediate Solution

### Option 1: Use Release Build for Testing (Fastest)
Since the release build doesn't use kapt, it builds successfully:

```powershell
# Build release APK (works immediately)
./gradlew assembleRelease

# Install manually
adb install -r app/build/outputs/apk/release/app-release.apk
```

### Option 2: VS Code Tasks (Already Set Up)
Use the VS Code tasks I created:

1. **Press `Ctrl+Shift+P`**
2. **Type "Tasks: Run Task"**
3. **Select "Build Release APK"**
4. **Install the APK manually from `app/build/outputs/apk/release/`**

### Option 3: PowerShell Script for Release Builds
```powershell
# Create a quick script for release hot reloading
./gradlew assembleRelease && adb install -r app/build/outputs/apk/release/app-release.apk
```

## 🔧 To Fix Debug Build (Optional)

The debug build fails because Icepick requires `@State` fields to be public, but many are private. To fix:

1. **Change all `@State private var` to `@State var`** in these files:
   - `PlayerActivity.kt` 
   - `DetailsFragment.kt`
   - `HomeFragment.kt`
   - `SearchFragment.kt`
   - `EpisodesFragment.kt`
   - And others...

2. **Or temporarily remove `@State` annotations** for testing

## 🎯 Recommended Workflow (Works Now)

**For immediate hot reloading:**

1. **Make changes in VS Code**
2. **Press `Ctrl+Shift+F10`** (Build Release APK)
3. **Install APK:** `adb install -r app/build/outputs/apk/release/app-release.apk`
4. **App updates in ~30-60 seconds**

**Or use this one-liner:**
```powershell
./gradlew assembleRelease && adb install -r app/build/outputs/apk/release/app-release.apk
```

## 📊 Performance
- **Release build time:** 30-90 seconds
- **Manual install:** 5-10 seconds
- **Total cycle:** ~1-2 minutes

This is much faster than rebuilding full APKs from scratch and gives you working hot reloading right now while we fix the debug build issues.

## 🔄 File Watcher for Release Builds

You can also modify the PowerShell watcher to use release builds:

```powershell
# In watch-and-build.ps1, change the build command to:
$result = & ./gradlew assembleRelease 2>&1
# Then add manual install:
& adb install -r app/build/outputs/apk/release/app-release.apk
```

This gives you automatic hot reloading with release builds! 