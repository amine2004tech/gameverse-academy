# GameVerse Academy - Server Edition Packager
Write-Host "Packaging GameVerse Academy Server Edition..." -ForegroundColor Cyan

# 1. Build the project
Write-Host "Building Fat JAR (Tomcat + SQL Seed Embedded)..." -ForegroundColor Yellow
.\mvnw.cmd clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

# 2. Prepare release directories
$ExeReleaseDir = "release\Server-EXE"
$JarReleaseDir = "release\Server-JAR"

if (Test-Path $ExeReleaseDir) { Remove-Item -Recurse -Force $ExeReleaseDir }
if (Test-Path $JarReleaseDir) { Remove-Item -Recurse -Force $JarReleaseDir }

New-Item -ItemType Directory -Path $ExeReleaseDir | Out-Null
New-Item -ItemType Directory -Path $JarReleaseDir | Out-Null

# 3. Copy FAT JAR to temp name for embedding and JAR release
$FatJar = "target\gameverseacademy-1.0-SNAPSHOT-portable.jar"
$EmbedJar = "gameverse.jar"
Copy-Item $FatJar $EmbedJar
Copy-Item $FatJar "$JarReleaseDir\gameverse.jar"

# 4. Compile C# Launcher with embedded JAR
Write-Host "Compiling Launcher.cs and embedding FAT JAR into EXE..." -ForegroundColor Yellow
$Csc = "$env:WINDIR\Microsoft.NET\Framework64\v4.0.30319\csc.exe"
if (-not (Test-Path $Csc)) {
    $Csc = "$env:WINDIR\Microsoft.NET\Framework\v4.0.30319\csc.exe"
}

if (-not (Test-Path $Csc)) {
    Write-Host "Could not find csc.exe (C# Compiler). Cannot build EXE." -ForegroundColor Red
    exit 1
}

$ExeOut = "$ExeReleaseDir\GameVerseAcademy.exe"
& $Csc /nologo /target:winexe /out:$ExeOut /resource:$EmbedJar Launcher.cs

if ($LASTEXITCODE -ne 0) {
    Write-Host "EXE Compilation failed!" -ForegroundColor Red
    Remove-Item $EmbedJar -Force
    exit $LASTEXITCODE
}

# 5. Create launch scripts for JAR release
Write-Host "Creating launch scripts for JAR release..." -ForegroundColor Yellow
$BatchContent = "@echo off`r`n" +
                 "echo Starting GameVerse Academy Server...`r`n" +
                 "java -jar gameverse.jar`r`n" +
                 "pause"
$BatchContent | Out-File -FilePath "$JarReleaseDir\start-server.bat" -Encoding ascii

$ShContent = "#!/bin/bash`n" +
               "echo 'Starting GameVerse Academy Server...'`n" +
               "java -jar gameverse.jar"
$ShContent | Out-File -FilePath "$JarReleaseDir\start-server.sh" -Encoding ascii

# 6. Cleanup & Copy External Assets to both folders
Remove-Item $EmbedJar -Force
Write-Host "Copying external assets to release folders..." -ForegroundColor Yellow
Copy-Item -Recurse "src\main\webapp\assets" "$ExeReleaseDir\assets"
Copy-Item -Recurse "src\main\webapp\assets" "$JarReleaseDir\assets"

Write-Host "Server Edition packaged successfully!" -ForegroundColor Green
Write-Host "Single Executable created at: $ExeReleaseDir\GameVerseAcademy.exe" -ForegroundColor Cyan
Write-Host "Independent JAR & Launch scripts created at: $JarReleaseDir" -ForegroundColor Cyan

