# Инициализация Visual Studio Build Tools
$vsPath = "C:\Program Files (x86)\Microsoft Visual Studio\2022\BuildTools"
$vcvarsPath = "$vsPath\VC\Auxiliary\Build\vcvars64.bat"

if (-not (Test-Path $vcvarsPath)) {
    Write-Host "ERROR: Visual Studio Build Tools not found at: $vsPath"
    exit 1
}

# Запускаем vcvars64.bat и сохраняем переменные окружения
$tempFile = [System.IO.Path]::GetTempFileName()
cmd /c "`"$vcvarsPath`" && set" > $tempFile

Get-Content $tempFile | ForEach-Object {
    if ($_ -match "^(.*?)=(.*)$") {
        Set-Item -Path "Env:$($matches[1])" -Value $matches[2]
    }
}

Remove-Item $tempFile

Write-Host "Visual Studio Build Tools initialized"

# Проверка NDK
$env:ANDROID_NDK_HOME = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk\ndk\29.0.14206865"

if (-not (Test-Path $env:ANDROID_NDK_HOME)) {
    Write-Host "ERROR: NDK not found at: $env:ANDROID_NDK_HOME"
    exit 1
}

Write-Host "Using NDK: $env:ANDROID_NDK_HOME"
Write-Host "Building for Android..."

# Компиляция
cargo build --target aarch64-linux-android --release
if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed for aarch64"
    exit 1
}

cargo build --target armv7-linux-androideabi --release
if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed for armv7"
    exit 1
}

cargo build --target x86_64-linux-android --release
if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed for x86_64"
    exit 1
}

Write-Host "Compilation complete!"

# Создаём директории для библиотек
$jniLibsPath = "..\androidApp\src\main\jniLibs"
New-Item -ItemType Directory -Force -Path "$jniLibsPath\arm64-v8a" | Out-Null
New-Item -ItemType Directory -Force -Path "$jniLibsPath\armeabi-v7a" | Out-Null
New-Item -ItemType Directory -Force -Path "$jniLibsPath\x86_64" | Out-Null

# Копируем библиотеки
Copy-Item "target\aarch64-linux-android\release\libvalera_crypto.so" "$jniLibsPath\arm64-v8a\libvalera_crypto.so" -Force
Copy-Item "target\armv7-linux-androideabi\release\libvalera_crypto.so" "$jniLibsPath\armeabi-v7a\libvalera_crypto.so" -Force
Copy-Item "target\x86_64-linux-android\release\libvalera_crypto.so" "$jniLibsPath\x86_64\libvalera_crypto.so" -Force

Write-Host "Build complete! Libraries copied to jniLibs:"
Write-Host "  - arm64-v8a/libvalera_crypto.so"
Write-Host "  - armeabi-v7a/libvalera_crypto.so"
Write-Host "  - x86_64/libvalera_crypto.so"