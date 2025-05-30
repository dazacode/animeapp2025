# Hot Reloading in VS Code for Android Development

## Overview
Since you're using VS Code instead of Android Studio, the hot reloading approach is different. Here are the best methods for fast development cycles.

## 🚀 VS Code Hot Reloading Methods

### 1. **Gradle Daemon + Fast Builds (Primary Method)**
The fastest approach for VS Code users:

**Setup:**
```powershell
# Install debug build once
./gradlew installDebug

# Keep Gradle daemon running for faster subsequent builds
./gradlew --daemon
```

**Development Workflow:**
```powershell
# Make code changes in VS Code, then run:
./gradlew installDebug --daemon

# For even faster builds (assembly only):
./gradlew assembleDebug --daemon

# Then manually install the APK:
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 2. **ADB Install with File Watching**
Set up automatic rebuilds when files change:

**Install a file watcher (choose one):**

**Option A - Using PowerShell:**
```powershell
# Create a simple file watcher script
# Save this as watch-and-build.ps1
$watcher = New-Object System.IO.FileSystemWatcher
$watcher.Path = "app/src"
$watcher.IncludeSubdirectories = $true
$watcher.EnableRaisingEvents = $true

Register-ObjectEvent $watcher "Changed" -Action {
    Write-Host "File changed, rebuilding..."
    & ./gradlew installDebug --daemon
}

Write-Host "Watching for changes... Press Ctrl+C to stop"
try { while ($true) { Start-Sleep 1 } }
finally { $watcher.Dispose() }
```

**Option B - Using Node.js (if you have it):**
```powershell
# Install nodemon globally
npm install -g nodemon

# Create package.json with watch script
```

### 3. **VS Code Tasks for Quick Commands**
Set up VS Code tasks for one-click builds:

**Create `.vscode/tasks.json`:**
```json
{
    "version": "2.0.0",
    "tasks": [
        {
            "label": "Install Debug",
            "type": "shell",
            "command": "./gradlew",
            "args": ["installDebug", "--daemon"],
            "group": {
                "kind": "build",
                "isDefault": true
            },
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            },
            "problemMatcher": []
        },
        {
            "label": "Build Debug",
            "type": "shell",
            "command": "./gradlew",
            "args": ["assembleDebug", "--daemon"],
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            },
            "problemMatcher": []
        },
        {
            "label": "Clean Build",
            "type": "shell",
            "command": "./gradlew",
            "args": ["clean", "assembleDebug"],
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            },
            "problemMatcher": []
        }
    ]
}
```

**Usage:**
- Press `Ctrl+Shift+P` → "Tasks: Run Task" → "Install Debug"
- Or use `Ctrl+Shift+B` for default build task

### 4. **ADB Wireless Development**
Set up wireless debugging for faster deployment:

```powershell
# Enable wireless debugging (Android 11+)
adb pair <IP>:<PORT>
adb connect <IP>:<PORT>

# Now you can deploy without USB cable
./gradlew installDebug
```

## ⚡ Optimized Development Workflow

### **Daily Workflow:**
1. **Start Gradle daemon:** `./gradlew --daemon`
2. **Install debug build:** `./gradlew installDebug --daemon`
3. **Make changes in VS Code**
4. **Quick rebuild:** `Ctrl+Shift+B` (or `./gradlew installDebug --daemon`)
5. **App automatically updates on device**

### **VS Code Extensions for Android:**
Install these extensions for better development experience:

```
- Android iOS Emulator
- Kotlin Language
- Gradle for Java
- Android Studio Emulator
- ADB Interface
```

### **Keyboard Shortcuts Setup:**
Add to VS Code `keybindings.json`:
```json
[
    {
        "key": "ctrl+f10",
        "command": "workbench.action.tasks.runTask",
        "args": "Install Debug"
    },
    {
        "key": "ctrl+alt+f10", 
        "command": "workbench.action.tasks.runTask",
        "args": "Build Debug"
    }
]
```

## 🔧 Performance Optimizations

### **Gradle Optimizations (Already Applied):**
- ✅ Parallel builds enabled
- ✅ Build caching enabled  
- ✅ Incremental compilation
- ✅ Daemon mode
- ✅ Configuration on demand

### **Additional Speed Tips:**
1. **Use `--daemon` flag** - keeps Gradle in memory
2. **Use `--parallel`** - builds modules in parallel
3. **Use `--build-cache`** - reuses previous build outputs
4. **Use `--configure-on-demand`** - only configures relevant projects

**Fastest build command:**
```powershell
./gradlew installDebug --daemon --parallel --build-cache --configure-on-demand
```

### **Memory Optimization:**
Already configured in `gradle.properties`:
- JVM heap: 2GB
- Parallel builds: enabled
- Build cache: enabled

## 📱 Device Setup

### **Enable Developer Options:**
1. Settings → About Phone → Tap "Build Number" 7 times
2. Settings → Developer Options → Enable "USB Debugging"
3. Settings → Developer Options → Enable "Install via USB"
4. Settings → Developer Options → Enable "USB Debugging (Security Settings)"

### **ADB Commands:**
```powershell
# Check connected devices
adb devices

# Install APK manually
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Uninstall app
adb uninstall com.kawaidev.kawaime.debug

# Clear app data
adb shell pm clear com.kawaidev.kawaime.debug

# View logs
adb logcat | findstr "Kawaime"
```

## 🚫 Limitations vs Android Studio

**What VS Code CAN'T do:**
- ❌ Instant Apply Changes (Android Studio exclusive)
- ❌ Hot Swap code without reinstall
- ❌ Layout Inspector
- ❌ Built-in emulator management

**What VS Code CAN do:**
- ✅ Fast Gradle builds with daemon
- ✅ Automated file watching
- ✅ Custom build tasks
- ✅ Wireless debugging
- ✅ Terminal integration
- ✅ Git integration

## 🎯 Recommended Workflow

**For VS Code users, this is the fastest approach:**

1. **One-time setup:**
   ```powershell
   ./gradlew installDebug --daemon
   ```

2. **Development loop:**
   - Make changes in VS Code
   - Press `Ctrl+Shift+B` (or run task)
   - Wait ~10-30 seconds for build + install
   - App updates on device

3. **When to do full rebuild:**
   - Adding dependencies
   - Manifest changes
   - New activities/services
   - Release builds

**Expected build times:**
- First build: 1-3 minutes
- Incremental builds: 10-30 seconds
- Clean builds: 1-2 minutes

This is much faster than building full APKs each time, though not as instant as Android Studio's Apply Changes feature. 