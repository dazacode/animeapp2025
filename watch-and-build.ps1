# Android Hot Reload File Watcher for VS Code
# This script watches for changes in your Android source files and automatically rebuilds

Write-Host "🚀 Starting Android Hot Reload Watcher..." -ForegroundColor Green
Write-Host "📁 Watching: app/src/" -ForegroundColor Yellow
Write-Host "🔨 Build command: ./gradlew installDebug --daemon" -ForegroundColor Yellow
Write-Host "⏹️  Press Ctrl+C to stop" -ForegroundColor Red
Write-Host ""

# Create file system watcher
$watcher = New-Object System.IO.FileSystemWatcher
$watcher.Path = "app/src"
$watcher.IncludeSubdirectories = $true
$watcher.EnableRaisingEvents = $true

# Filter for relevant file types
$watcher.Filter = "*.*"

# Debounce mechanism to avoid multiple builds for rapid changes
$lastBuildTime = [DateTime]::MinValue
$debounceSeconds = 3

# Build function
function Invoke-Build {
    $currentTime = Get-Date
    $timeSinceLastBuild = ($currentTime - $script:lastBuildTime).TotalSeconds
    
    if ($timeSinceLastBuild -gt $debounceSeconds) {
        $script:lastBuildTime = $currentTime
        Write-Host "📝 File changed at $(Get-Date -Format 'HH:mm:ss')" -ForegroundColor Cyan
        Write-Host "🔨 Building and installing..." -ForegroundColor Yellow
        
        try {
            $result = & ./gradlew installDebug --daemon 2>&1
            if ($LASTEXITCODE -eq 0) {
                Write-Host "✅ Build successful! App updated on device." -ForegroundColor Green
            } else {
                Write-Host "❌ Build failed!" -ForegroundColor Red
                Write-Host $result -ForegroundColor Red
            }
        } catch {
            Write-Host "❌ Build error: $_" -ForegroundColor Red
        }
        Write-Host ""
    }
}

# Register event handlers
Register-ObjectEvent $watcher "Changed" -Action {
    $path = $Event.SourceEventArgs.FullPath
    $name = $Event.SourceEventArgs.Name
    
    # Only trigger for relevant file types
    if ($name -match '\.(kt|java|xml|gradle)$') {
        Invoke-Build
    }
}

Register-ObjectEvent $watcher "Created" -Action {
    $path = $Event.SourceEventArgs.FullPath
    $name = $Event.SourceEventArgs.Name
    
    if ($name -match '\.(kt|java|xml|gradle)$') {
        Invoke-Build
    }
}

# Keep script running
try {
    while ($true) {
        Start-Sleep 1
    }
} finally {
    # Cleanup
    $watcher.EnableRaisingEvents = $false
    $watcher.Dispose()
    Write-Host "🛑 File watcher stopped." -ForegroundColor Red
} 