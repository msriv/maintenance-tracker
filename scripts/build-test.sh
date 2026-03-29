#!/usr/bin/env bash
# Local build test — mirrors exactly what the GitHub Actions release workflow does.
# Run from the project root: ./scripts/build-test.sh
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

# Pin to Java 21 locally (Codespace ships Java 25 which breaks Kotlin compiler)
JAVA21="/usr/local/sdkman/candidates/java/21.0.9-ms"
if [ -d "$JAVA21" ]; then
  export JAVA_HOME="$JAVA21"
fi

echo "=== MotoTracker local build test ==="

# 1. google-services.json must exist
if [ ! -f "app/google-services.json" ]; then
  echo "ERROR: app/google-services.json not found."
  echo "       Download it from Firebase Console and place it at app/google-services.json"
  exit 1
fi
echo "[1/4] google-services.json found."

# 2. Generate debug keystore if missing (same as CI)
KEYSTORE="$HOME/.android/debug.keystore"
if [ ! -f "$KEYSTORE" ]; then
  echo "[2/4] Generating debug keystore..."
  mkdir -p "$HOME/.android"
  keytool -genkeypair -v \
    -keystore "$KEYSTORE" \
    -storepass android \
    -alias androiddebugkey \
    -keypass android \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -dname "CN=Android Debug,O=Android,C=US"
else
  echo "[2/4] Debug keystore already exists."
fi

# 3. Build release APK with debug signing
echo "[3/4] Building release APK..."
chmod +x gradlew
./gradlew assembleRelease --no-daemon \
  -Pandroid.injected.signing.store.file="$KEYSTORE" \
  -Pandroid.injected.signing.store.password=android \
  -Pandroid.injected.signing.key.alias=androiddebugkey \
  -Pandroid.injected.signing.key.password=android

# 4. Locate and report APK
APK=$(find app/build/outputs/apk/release -name "*.apk" | head -1)
if [ -z "$APK" ]; then
  echo "ERROR: APK not found after build."
  exit 1
fi
echo "[4/4] Build successful!"
echo ""
echo "  APK: $APK"
echo "  Size: $(du -sh "$APK" | cut -f1)"
