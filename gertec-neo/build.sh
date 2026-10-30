#!/usr/bin/env bash
# Gera o bridge da Gertec Neo (libgedi 2.x) que roda na GPOS780S num classloader isolado.
#
# Saida (versionada) em android/src/main/assets/gertec-neo/:
#   neo-bridge.jar                 dex com libgedi Neo + easy layer Neo + deps + NeoGertecBackend
#   armeabi-v7a/libgedi.so         libgedi nativa da Neo (nome igual ao da 1.16, por isso fica fora de jniLibs)
#   arm64-v8a/libgedi.so
#   VERSION                        chave de cache da extracao em runtime
#
# Uso: gertec-neo/build.sh   (precisa de JDK 17, Android SDK com platforms;android-35 e build-tools 35.0.0)
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SDK_DIR="$ROOT/gertec-neo/sdk"
ADAPTER_DIR="$ROOT/gertec-neo/adapter"
SHARED_DIR="$ROOT/android/src/main/java/com/gertec/backend"
OUT_DIR="$ROOT/android/src/main/assets/gertec-neo"

ANDROID_SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-/opt/homebrew/share/android-commandlinetools}}"
BUILD_TOOLS="$ANDROID_SDK/build-tools/35.0.0"
ANDROID_JAR="$ANDROID_SDK/platforms/android-35/android.jar"

GEDI_AAR="$SDK_DIR/libgedi-2.0.10-9e53cdd-gposNeo-payment-release.aar"
EASY_AAR="$SDK_DIR/android-easy-layer-release_gpos720.aar"

for f in "$GEDI_AAR" "$EASY_AAR" "$BUILD_TOOLS/d8" "$ANDROID_JAR"; do
  [ -e "$f" ] || { echo "faltando: $f" >&2; exit 1; }
done

WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

mkdir -p "$WORK/gedi" "$WORK/easy" "$WORK/shared" "$WORK/adapter"
unzip -q "$GEDI_AAR" -d "$WORK/gedi"
unzip -q "$EASY_AAR" -d "$WORK/easy"

CP="$ANDROID_JAR:$WORK/gedi/classes.jar:$WORK/easy/classes.jar"
for jar in "$WORK"/easy/libs/*.jar; do CP="$CP:$jar"; done

# Interface compartilhada: compilada so p/ resolver o adaptador, NAO entra no dex (vem do classloader do app).
javac --release 8 -cp "$ANDROID_JAR" -d "$WORK/shared" "$SHARED_DIR"/*.java
javac --release 8 -cp "$CP:$WORK/shared" -d "$WORK/adapter" "$ADAPTER_DIR"/com/gertec/neo/*.java

INPUTS=("$WORK/gedi/classes.jar" "$WORK/easy/classes.jar")
for jar in "$WORK"/easy/libs/*.jar; do INPUTS+=("$jar"); done
while IFS= read -r cls; do INPUTS+=("$cls"); done < <(find "$WORK/adapter" -name '*.class')

rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR/armeabi-v7a" "$OUT_DIR/arm64-v8a"

"$BUILD_TOOLS/d8" --release --min-api 21 --lib "$ANDROID_JAR" --classpath "$WORK/shared" \
  --output "$OUT_DIR/neo-bridge.jar" "${INPUTS[@]}"

cp "$WORK/gedi/jni/armeabi-v7a/libgedi.so" "$OUT_DIR/armeabi-v7a/libgedi.so"
cp "$WORK/gedi/jni/arm64-v8a/libgedi.so" "$OUT_DIR/arm64-v8a/libgedi.so"

# Chave de cache: muda sempre que o bridge ou as .so mudam.
HASH="$(cat "$OUT_DIR/neo-bridge.jar" "$OUT_DIR/armeabi-v7a/libgedi.so" "$OUT_DIR/arm64-v8a/libgedi.so" | shasum -a 256 | cut -c1-12)"
printf '2.0.10-%s' "$HASH" > "$OUT_DIR/VERSION"

echo "ok: $OUT_DIR"
ls -l "$OUT_DIR" "$OUT_DIR"/*/
