# Настройки NDK
$env:ANDROID_NDK_HOME = "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk\ndk\29.0.14206865"

if (-not (Test-Path $env:ANDROID_NDK_HOME)) {
    Write-Host "NDK not found at: $env:ANDROID_NDK_HOME"
    exit 1
}

Write-Host "Using NDK: $env:ANDROID_NDK_HOME"

# Toolchain paths
$ndkToolchain = "$env:ANDROID_NDK_HOME\toolchains\llvm\prebuilt\windows-x86_64\bin"

$env:AR_aarch64_linux_android = "$ndkToolchain\llvm-ar.exe"
$env:CC_aarch64_linux_android = "$ndkToolchain\aarch64-linux-android21-clang.cmd"

$env:AR_armv7_linux_androideabi = "$ndkToolchain\llvm-ar.exe"
$env:CC_armv7_linux_androideabi = "$ndkToolchain\armv7a-linux-androideabi21-clang.cmd"

$env:AR_x86_64_linux_android = "$ndkToolchain\llvm-ar.exe"
$env:CC_x86_64_linux_android = "$ndkToolchain\x86_64-linux-android21-clang.cmd"

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
Copy-Item "target\aarch64-linux-android\release\valera_crypto.dll" "$jniLibsPath\arm64-v8a\libvalera_crypto.so" -Force
Copy-Item "target\armv7-linux-androideabi\release\valera_crypto.dll" "$jniLibsPath\armeabi-v7a\libvalera_crypto.so" -Force
Copy-Item "target\x86_64-linux-android\release\valera_crypto.dll" "$jniLibsPath\x86_64\libvalera_crypto.so" -Force

Write-Host "Build complete! Libraries copied to jniLibs"
Write-Host "  - arm64-v8a/libvalera_crypto.so"
Write-Host "  - armeabi-v7a/libvalera_crypto.so"
Write-Host "  - x86_64/libvalera_crypto.so"cd C:\Users\lykye\Desktop\SEMESTER\myValera\valera\rust-crypto