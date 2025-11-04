# Compilation script for Paint application with JavaFX
$projectPath = "C:\Users\goryg\PaintAPP22\Javafx-Paint-Application\Paint\Paint"
cd $projectPath

# Get Java paths
$javac = (Get-Command javac).Source
$java = (Get-Command java).Source
$javaHome = (Get-Item (Get-Command java).Source).Directory.Parent.FullName

# Try to find JavaFX SDK
$javafxPaths = @(
    "C:\javafx-sdk-21.0.3",
    "C:\Program Files\javafx-sdk-21.0.3",
    "$env:USERPROFILE\javafx-sdk-21.0.3",
    "$env:USERPROFILE\Downloads\javafx-sdk-21.0.3"
)

$javafxHome = $null
foreach ($path in $javafxPaths) {
    if (Test-Path "$path\lib") {
        $javafxHome = $path
        break
    }
}

if (-not $javafxHome) {
    Write-Host "ERROR: JavaFX SDK not found!"
    Write-Host "Searched in:"
    $javafxPaths | ForEach-Object { Write-Host "  $_" }
    exit 1
}

Write-Host "Using Java: $java"
Write-Host "Using javac: $javac"
Write-Host "Using JavaFX: $javafxHome"

# Create bin directory if it doesn't exist
if (-not (Test-Path "bin\paint")) {
    New-Item -ItemType Directory -Path "bin\paint\controller" -Force | Out-Null
    New-Item -ItemType Directory -Path "bin\paint\model" -Force | Out-Null
    New-Item -ItemType Directory -Path "bin\paint\view" -Force | Out-Null
}

# Copy FXML and CSS files
Copy-Item "src\paint\view\*.fxml" -Destination "bin\paint\view\" -Force
Copy-Item "src\paint\view\*.css" -Destination "bin\paint\view\" -Force

# Compile Java files
Write-Host "Compiling..."
& $javac --module-path "$javafxHome\lib" `
         --add-modules javafx.controls,javafx.fxml `
         -d bin\paint `
         -sourcepath src `
         src\paint\*.java `
         src\paint\controller\*.java `
         src\paint\model\*.java

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful!"
    exit 0
} else {
    Write-Host "Compilation failed!"
    exit 1
}
