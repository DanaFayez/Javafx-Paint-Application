#!/usr/bin/env pwsh
# Run script for Paint application

$projectPath = "C:\Users\goryg\PaintAPP22\Javafx-Paint-Application\Paint\Paint"
cd $projectPath

# Get paths
$java = (Get-Command java).Source  
$javafxHome = "$env:USERPROFILE\Downloads\javafx-sdk-21.0.3"

Write-Host "Java: $java"
Write-Host "JavaFX: $javafxHome"

# Check if build/classes exists
if (-not (Test-Path "build\classes")) {
    Write-Host "ERROR: build\classes directory not found!"
    exit 1
}

# Copy FXML and CSS files to build
Copy-Item "src\paint\view\*.fxml" -Destination "build\classes\paint\view\" -Force 2>$null
Copy-Item "src\paint\view\*.css" -Destination "build\classes\paint\view\" -Force 2>$null

# Run the application
Write-Host "Starting Paint application..."
& $java --module-path "$javafxHome\lib" `
        --add-modules javafx.controls,javafx.fxml `
        -cp "build\classes" `
        paint.Paint
