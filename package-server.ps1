# GameVerse Academy - Server Edition Packager
Write-Host "Packaging GameVerse Academy Server Edition..." -ForegroundColor Cyan

# 1. Build the project
Write-Host "Building Fat JAR..." -ForegroundColor Yellow
.\mvnw.cmd clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

# 2. Prepare release directory
$ReleaseDir = "release\Server-JAR"
if (Test-Path $ReleaseDir) {
    Remove-Item -Recurse -Force $ReleaseDir
}
New-Item -ItemType Directory -Path $ReleaseDir | Out-Null

# 3. Copy artifacts
Write-Host "Copying files to $ReleaseDir..." -ForegroundColor Yellow
Copy-Item "target\gameverseacademy-1.0-SNAPSHOT.war" "$ReleaseDir\gameverse.jar"
Copy-Item -Recurse "src\main\webapp" "$ReleaseDir\src\main\webapp"
Copy-Item -Recurse "data" "$ReleaseDir\data"
Copy-Item -Recurse "assets" "$ReleaseDir\assets"

# 4. Create launch scripts
Write-Host "Creating launch scripts..." -ForegroundColor Yellow

$BatchContent = "@echo off`r`n" +
                 "echo Starting GameVerse Academy Server...`r`n" +
                 "java -Ddatabase.path=data/gameverse.db -jar gameverse.jar`r`n" +
                 "pause"
$BatchContent | Out-File -FilePath "$ReleaseDir\start-server.bat" -Encoding ascii

$ShContent = "#!/bin/bash`n" +
               "echo 'Starting GameVerse Academy Server...'`n" +
               "java -Ddatabase.path=data/gameverse.db -jar gameverse.jar"
$ShContent | Out-File -FilePath "$ReleaseDir\start-server.sh" -Encoding ascii

Write-Host "Server Edition packaged successfully in $ReleaseDir" -ForegroundColor Green
Write-Host "Run $ReleaseDir\start-server.bat to launch!" -ForegroundColor Cyan
