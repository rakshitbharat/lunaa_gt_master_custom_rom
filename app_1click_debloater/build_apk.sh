#!/usr/bin/env bash
set -e

# Automatically locate Android SDK
if [ -z "$ANDROID_HOME" ]; then
    if [ -d "$HOME/Library/Android/sdk" ]; then
        ANDROID_HOME="$HOME/Library/Android/sdk"
    elif [ -d "$HOME/Android/Sdk" ]; then
        ANDROID_HOME="$HOME/Android/Sdk"
    else
        echo "Error: ANDROID_HOME not set and Android SDK not found."
        exit 1
    fi
fi

# Pick highest build-tools and platform
BUILD_TOOLS=$(ls -d "$ANDROID_HOME/build-tools/"* | sort -V | tail -n 1)
PLATFORM_JAR=$(ls "$ANDROID_HOME/platforms/"*/android.jar | sort -V | tail -n 1)

echo "Using Build-Tools: $BUILD_TOOLS"
echo "Using Platform JAR: $PLATFORM_JAR"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

mkdir -p build/compiled_res build/gen build/obj build/bin bin

# 1. Compile XML resources
"$BUILD_TOOLS/aapt2" compile --dir res -o build/compiled_res/

# 2. Link resources and generate R.java
"$BUILD_TOOLS/aapt2" link -I "$PLATFORM_JAR" \
    --manifest AndroidManifest.xml \
    --java build/gen \
    -o build/bin/app-unsigned.apk \
    build/compiled_res/*.flat \
    --auto-add-overlay

# 3. Compile Java source code
javac -d build/obj -cp "$PLATFORM_JAR" \
    build/gen/com/lunaa/debloater/R.java \
    src/com/lunaa/debloater/AdminReceiver.java \
    src/com/lunaa/debloater/MainActivity.java

# 4. Dex bytecode with d8
"$BUILD_TOOLS/d8" --lib "$PLATFORM_JAR" \
    --output build/bin \
    $(find build/obj -name "*.class")

# 5. Pack classes.dex into APK
cd build/bin
jar uf app-unsigned.apk classes.dex
cd "$SCRIPT_DIR"

# 6. Keystore setup
KEYSTORE=~/.android/debug.keystore
if [ ! -f "$KEYSTORE" ]; then
    mkdir -p ~/.android
    keytool -genkey -v -keystore "$KEYSTORE" -storepass android -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=Android,C=US"
fi

# 7. Zipalign & Sign
"$BUILD_TOOLS/zipalign" -f -p 4 build/bin/app-unsigned.apk bin/Debloater.apk
"$BUILD_TOOLS/apksigner" sign --ks "$KEYSTORE" --ks-pass pass:android --ks-key-alias androiddebugkey --key-pass pass:android bin/Debloater.apk

echo "SUCCESS: Built and signed APK at: $SCRIPT_DIR/bin/Debloater.apk"
