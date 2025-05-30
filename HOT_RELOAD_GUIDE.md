# Hot Reloading Setup for Kawaime Android App

## Overview
This guide explains how to use hot reloading features to speed up development without rebuilding APKs constantly.

## Available Hot Reloading Methods

### 1. Android Studio Apply Changes (Recommended)
The fastest way to see changes without rebuilding:

**Setup:**
- Use Android Studio (not just any IDE)
- Connect your device via USB with USB Debugging enabled
- Build and install the debug version first: `./gradlew installDebug`

**Usage:**
- **Apply Changes and Restart Activity** (Ctrl+F10 / Cmd+F10)
  - Use for: UI changes, resource updates, method implementations
  - Restarts current activity with changes
  
- **Apply Code Changes** (Ctrl+Alt+F10 / Cmd+Alt+F10)  
  - Use for: Method body changes, adding/removing methods
  - Fastest option, no activity restart

- **Run App** (Shift+F10 / Cmd+R)
  - Use for: Manifest changes, new classes, dependency changes
  - Full reinstall but faster than building APK

### 2. Gradle Incremental Builds
Optimized build configuration for faster compilation:

**Features Enabled:**
- Parallel builds
- Build caching  
- Incremental Kotlin compilation
- Configuration on demand

**Usage:**
```powershell
# Fast debug build
./gradlew assembleDebug

# Install debug build
./gradlew installDebug

# Clean and rebuild when needed
./gradlew clean assembleDebug
```

### 3. Debug vs Release Builds

**Debug Build Features:**
- Separate package name (`com.kawaidev.kawaime.debug`)
- Debug-specific app name ("Kawaime Debug")
- StrictMode enabled for performance monitoring
- No code obfuscation
- Faster builds

**Commands:**
```powershell
# Debug build (for development)
./gradlew assembleDebug
./gradlew installDebug

# Release build (for distribution)
./gradlew assembleRelease
```

## Development Workflow

### Daily Development:
1. Install debug build once: `./gradlew installDebug`
2. Make code changes in Android Studio
3. Use **Apply Changes** (Ctrl+F10) for most changes
4. Use **Apply Code Changes** (Ctrl+Alt+F10) for method-only changes
5. Only use **Run App** when Apply Changes doesn't work

### When to Rebuild APK:
- Adding new dependencies
- Changing AndroidManifest.xml
- Adding new Activities/Services
- Changing build configuration
- For release/distribution builds

### Performance Tips:
1. **Use Apply Changes first** - fastest option
2. **Keep Android Studio updated** - better Apply Changes support
3. **Use debug builds** - much faster than release builds
4. **Enable USB Debugging** - required for Apply Changes
5. **Close unnecessary apps** - more RAM for faster builds

## Troubleshooting

### Apply Changes Not Working:
- Try "Apply Code Changes" instead
- Check if USB Debugging is enabled
- Restart Android Studio
- Clean and rebuild: `./gradlew clean assembleDebug`

### Slow Builds:
- Increase Gradle memory: Already set to 2GB in `gradle.properties`
- Close other applications
- Use SSD storage
- Enable parallel builds: Already enabled

### Changes Not Appearing:
- Force refresh: Pull down to refresh in app
- Clear app data: Settings > Apps > Kawaime Debug > Storage > Clear Data
- Restart app completely
- Check if you're using debug build

## IDE Setup

### Android Studio Settings:
1. **File > Settings > Build > Compiler**
   - Enable "Build project automatically"
   
2. **File > Settings > Build > Gradle**
   - Use "Gradle Wrapper"
   - Enable "Configuration on demand"

3. **Run/Debug Configurations**
   - Set "Deploy" to "Default APK"
   - Enable "Always install with package manager"

## Commands Reference

```powershell
# Development commands
./gradlew installDebug              # Install debug build
./gradlew assembleDebug            # Build debug APK
./gradlew clean                    # Clean build cache

# Release commands  
./gradlew assembleRelease          # Build release APK
./gradlew installRelease           # Install release build

# Utility commands
./gradlew dependencies             # Show dependencies
./gradlew tasks                    # Show available tasks
```

## Benefits

✅ **No APK rebuilds** for most changes  
✅ **Instant feedback** on UI changes  
✅ **Faster iteration** cycles  
✅ **Separate debug/release** builds  
✅ **Optimized build** performance  
✅ **Better development** experience  

The debug build will have a different package name and app name, so you can have both debug and release versions installed simultaneously for testing. 