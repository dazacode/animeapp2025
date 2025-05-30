# 🚀 Hot Reloading Shortcuts & Commands

## ✅ **FIXED: VS Code Integration Now Works!**

The issue was that `Ctrl+Shift+B` was trying to install to a device. Now it just builds the APK.

## 🎯 **VS Code Keyboard Shortcuts**

| Shortcut | Command | Speed | Description |
|----------|---------|-------|-------------|
| **`Ctrl+Shift+B`** | Build Debug APK | Fast | **DEFAULT** - Build APK only (no device needed) |
| **`Ctrl+F5`** | Quick Build | **Fastest** | Super-fast incremental build with daemon |
| **`Ctrl+F9`** | Build Debug APK | Fast | Alternative to Ctrl+Shift+B |
| `Ctrl+F10` | Install Debug | Medium | Build + Install (requires connected device) |
| **`Ctrl+Alt+I`** | Install Debug | Medium | **Alternative** if Ctrl+F10 doesn't work |
| `Ctrl+Alt+F10` | Build Debug APK | Fast | Same as Ctrl+Shift+B |
| `Ctrl+Shift+F10` | Build Release | Slow | Production build |

## 🔧 **Troubleshooting Shortcuts**

### **If Ctrl+F10 doesn't work:**
1. **Try `Ctrl+Alt+I`** instead
2. **Or use Command Palette**: `Ctrl+Shift+P` → "Tasks: Run Task" → "Install Debug (Hot Reload)"
3. **Reload VS Code**: `Ctrl+Shift+P` → "Developer: Reload Window"

### **Test if shortcuts work:**
1. **Try `Ctrl+F9`** (should build APK)
2. **Try `Ctrl+F5`** (should build faster)
3. **If these work, shortcuts are fine**

## 🎯 **Recommended Workflow**

### **For Development (No Device Connected):**
1. **`Ctrl+Shift+B`** or **`Ctrl+F9`** - Build APK 
2. **`Ctrl+F5`** - Super-fast incremental builds

### **For Testing (Device Connected):**
1. **`Ctrl+F10`** or **`Ctrl+Alt+I`** - Build and install to device
2. Test your changes on device

## 📱 **Manual Installation**

If shortcuts don't work, use terminal:
```bash
# Build APK
./gradlew assembleDebug

# Install to connected device/emulator
./gradlew installDebug

# APK location:
# app/build/outputs/apk/debug/app-debug.apk
```

## ⚡ **Performance**

- **Incremental builds**: 1-5 seconds
- **Full builds**: ~7 seconds  
- **Clean builds**: ~10 seconds

## 🎉 **Quick Test**

1. **Try `Ctrl+F9`** - Should build APK in ~2 seconds
2. **Try `Ctrl+F5`** - Should build even faster
3. **If these work, your shortcuts are fine!** 