#!/usr/bin/env bash
# Copyright (c) 2021 Matthew Nelson
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null && pwd)"

SCRIPT_START_TIME=$(date +%s)
TOR_BUILD_DIR="$DIR/../tor-browser-build"
TOR_OUT_DIR="$TOR_BUILD_DIR/out/tor"

function changeDir() {
  if ! cd "$1"; then
    echo "ERROR: Failed to change dirs to $1"
    exit 1
  fi
}

function checkDirExists() {
  if [[ "$1" != "" && "$1" && -d "$1" ]]; then
    return 0
  else
    return 1
  fi
}

function checkFileExists() {
  if [[ "$1" != "" && "$1" && -f "$1" ]]; then
    return 0
  else
    return 1
  fi
}

function deleteTorOutIfPresent() {
  if checkDirExists "$TOR_OUT_DIR"; then
    rm -rf "$TOR_OUT_DIR"
  fi
}

if ! checkDirExists "$TOR_BUILD_DIR"; then
  echo "ERROR: Directory $TOR_BUILD_DIR"
  echo "ERROR: does not exists. Did you forget to initialize"
  echo "ERROR: the repo's submodules?"
  exit 1
fi

changeDir "$TOR_BUILD_DIR"
# Apply patches here
#git apply "$DIR/../patches/0001-set-tor-version-0.4.6.10.patch"

EXIT_CODE=0
function buildAndroid() {
  changeDir "$TOR_BUILD_DIR"
  deleteTorOutIfPresent

  if [ "$1" == "android-all" ]; then
    ./rbm/rbm build tor --target release --target torbrowser-android-armv7
    ./rbm/rbm build tor --target release --target torbrowser-android-aarch64
    ./rbm/rbm build tor --target release --target torbrowser-android-x86
    ./rbm/rbm build tor --target release --target torbrowser-android-x86_64
  elif [ "$1" == "android-armv7" ]; then
    ./rbm/rbm build tor --target release --target torbrowser-android-armv7
  elif [ "$1" == "android-aarch64" ]; then
    ./rbm/rbm build tor --target release --target torbrowser-android-aarch64
  elif [ "$1" == "android-x86" ]; then
    ./rbm/rbm build tor --target release --target torbrowser-android-x86
  elif [ "$1" == "android-x86_64" ]; then
    ./rbm/rbm build tor --target release --target torbrowser-android-x86_64
  else
    echo "$1 is not a recognized target for Android. Run script again w/o args to see help."
    EXIT_CODE=1
    return 1
  fi

  changeDir "$TOR_OUT_DIR"
  local TEMP_DIR=
  TEMP_DIR=$(mktemp -d -p "$DIR")
  mkdir "$TEMP_DIR/jniLibs"

  local DIRECTORY=
  for DIRECTORY in "$(pwd)"/*; do
    mkdir "$DIRECTORY/tor"
    tar -xzf "$DIRECTORY/tor.tar.gz" -C "$DIRECTORY/tor"
    sleep 1
    cp -r "$DIRECTORY/tor/jniLibs/." "$TEMP_DIR/jniLibs/"
    rm -rf "$DIRECTORY/tor"
  done

  if ! checkDirExists "$TEMP_DIR/jniLibs"; then
    echo "ERROR: Something went wrong... $TEMP_DIR/jniLibs does not exist"
    EXIT_CODE=1
    return 1
  fi

  cp -r "$TEMP_DIR/jniLibs" "$DIR/../../../kmp-tor-binary-android/src/androidMain"
  rm -rf "$TEMP_DIR"

  local FILE=
  for FILE in $(find "$DIR/../../../kmp-tor-binary-android/src/androidMain/jniLibs" -name "libTor.so" | grep "libTor.so"); do
    mv $FILE $(echo $FILE | sed -e 's|libTor.so|libKmpTor.so|g')
  done

  echo "Binaries have been extracted and moved to kmp-tor-binary-android/src/androidMain/jniLibs"
}

function buildDesktop() {
  changeDir "$TOR_BUILD_DIR"
  deleteTorOutIfPresent

  local ARCH=
  local PLATFORM=
  local CONST_KT_NAME=
  local TAR_CONTENT_PATH="Tor"
  local EXTRACT_GEOIP=false
  if [ "$1" == "linux-i686" ]; then
    ARCH="x86"
    PLATFORM="linux"
    CONST_KT_NAME="LINUX_X86"
    ./rbm/rbm build tor --target release --target torbrowser-linux-i686
  elif [ "$1" == "linux-x86_64" ]; then
    ARCH="x64"
    PLATFORM="linux"
    CONST_KT_NAME="LINUX_X64"
    EXTRACT_GEOIP=true
    ./rbm/rbm build tor --target release --target torbrowser-linux-x86_64
  elif [ "$1" == "windows-i686" ]; then
    ARCH="x86"
    PLATFORM="mingw"
    CONST_KT_NAME="MINGW_X86"
    ./rbm/rbm build tor --target release --target torbrowser-windows-i686
  elif [ "$1" == "windows-x86_64" ]; then
    ARCH="x64"
    PLATFORM="mingw"
    CONST_KT_NAME="MINGW_X64"
    ./rbm/rbm build tor --target release --target torbrowser-windows-x86_64
  elif [ "$1" == "osx-x86_64" ]; then
    ARCH="x64"
    PLATFORM="macos"
    CONST_KT_NAME="MACOS_X64"
    TAR_CONTENT_PATH="Contents/MacOS/Tor"
    ./rbm/rbm build tor --target release --target torbrowser-osx-x86_64
  else
    echo "$1 is not a recognized target. Run script again w/o args to see help."
    EXIT_CODE=1
    return 1
  fi

  # All files within the directory prior to being zipped need to have their
  # timestamps set to a static value for reproducibility.
  local ZIP_TOUCH_TIME=198401010000.01
  local ZIP_TIME=01011984

  changeDir "$TOR_OUT_DIR"
  local TEMP_DIR
  TEMP_DIR=$(mktemp -d -p "$DIR")
  mkdir -p "$TEMP_DIR/$PLATFORM/$ARCH"
  mkdir "$TEMP_DIR/kmptor"

  local KMP_TOR_ZIP=
  local GEOIP_ZIP=
  KMP_TOR_ZIP="$TEMP_DIR/$PLATFORM/$ARCH/kmptor.zip"
  GEOIP_ZIP="$TEMP_DIR/kmptor/geoips.zip"

  local DIRECTORY=
  local KMP_TOR_ZIP_MANIFEST=
  local KMP_GEOIP_ZIP_MANIFEST=
  local SORTED=
  for DIRECTORY in "$(pwd)"/*; do
    mkdir "$DIRECTORY/tor"
    tar -xzf "$DIRECTORY/tor.tar.gz" -C "$DIRECTORY/tor"
    sleep 1
    changeDir "$DIRECTORY/tor/$TAR_CONTENT_PATH"
    find . -exec touch -t $ZIP_TOUCH_TIME {} +
    SORTED=$(find . -type f | sort)
    zip -t $ZIP_TIME "$KMP_TOR_ZIP" $SORTED
    KMP_TOR_ZIP_MANIFEST=$(zip -sf "$KMP_TOR_ZIP" | grep -v "Archive contains:" | grep -v "Total " | awk '{$1=$1};1')

    if [ $EXTRACT_GEOIP == true ]; then
      # Only occurs for linux-x86_64 target
      changeDir "$DIRECTORY/tor/Data/Tor"
      find . -exec touch -t $ZIP_TOUCH_TIME {} +
      SORTED=$(find . -type f | sort)
      zip -t $ZIP_TIME "$GEOIP_ZIP" $SORTED
      KMP_GEOIP_ZIP_MANIFEST=$(zip -sf "$GEOIP_ZIP" | grep -v "Archive contains:" | grep -v "Total " | awk '{$1=$1};1')
    fi

    rm -rf "$DIRECTORY/tor"

    break
  done

  changeDir "$TOR_OUT_DIR"

  local MANIFEST_STRING_TOR=
  local LINE=
  for LINE in $KMP_TOR_ZIP_MANIFEST; do
    if [ "$MANIFEST_STRING_TOR" == "" ]; then
      MANIFEST_STRING_TOR="\"$LINE\""
    else
      MANIFEST_STRING_TOR="$MANIFEST_STRING_TOR, \"$LINE\""
    fi
  done

  local MANIFEST_STRING_GEOIP=
  if [ $EXTRACT_GEOIP == true ]; then
    for LINE in $KMP_GEOIP_ZIP_MANIFEST; do
      if [ "$MANIFEST_STRING_GEOIP" == "" ]; then
        MANIFEST_STRING_GEOIP="\"$LINE\""
      else
        MANIFEST_STRING_GEOIP="$MANIFEST_STRING_GEOIP, \"$LINE\""
      fi
    done
  fi


  sleep 1

  if ! checkFileExists "$KMP_TOR_ZIP"; then
    echo "ERROR: Something went wrong... $KMP_TOR_ZIP does not exist"
    EXIT_CODE=1
    return 1
  fi

  if [ $EXTRACT_GEOIP == true ]; then
    if ! checkFileExists "$GEOIP_ZIP"; then
      echo "ERROR: Something went wrong... $GEOIP_ZIP does not exist"
      EXIT_CODE=1
      return 1
    fi
  fi

  local SHA256_KMP_TOR_ZIP=
  SHA256_KMP_TOR_ZIP=$(sha256sum "$KMP_TOR_ZIP" | cut -d ' ' -f 1)

  local KMP_EXTRACT_SRC_DIR=
  local KMP_EXTRACT_CONSTANTS_KT_PATH=
  KMP_EXTRACT_SRC_DIR="$DIR/../../../kmp-tor-binary-extract/src"
  KMP_EXTRACT_CONSTANTS_KT_PATH="kotlin/io/matthewnelson/kmp/tor/binary/extract/ConstantsBinaries.kt"

  local JVM_JS_RES_DIR=
  local JVM_JS_CONSTANTS_KT=
  JVM_JS_RES_DIR="$DIR/../../../kmp-tor-binary-$PLATFORM$ARCH/src/jvmJsMain/resources/kmptor/$PLATFORM/$ARCH"
  JVM_JS_CONSTANTS_KT="$KMP_EXTRACT_SRC_DIR/jvmJsMain/$KMP_EXTRACT_CONSTANTS_KT_PATH"

  if ! checkFileExists "$JVM_JS_CONSTANTS_KT"; then
    echo "ERROR: Something went wrong... $JVM_JS_CONSTANTS_KT does not exist"
    EXIT_CODE=1
    return 1
  fi

  mkdir -p "$JVM_JS_RES_DIR"
  mv "$KMP_TOR_ZIP" "$JVM_JS_RES_DIR"
  echo "Binaries have been extracted and moved to $JVM_JS_RES_DIR"

  sed -i "s|const val ZIP_SHA256_$CONST_KT_NAME = .*|const val ZIP_SHA256_$CONST_KT_NAME = \"$SHA256_KMP_TOR_ZIP\"|g" "$JVM_JS_CONSTANTS_KT"
  sed -i "s|val ZIP_MANIFEST_$CONST_KT_NAME get() = listOf(.*|val ZIP_MANIFEST_$CONST_KT_NAME get() = listOf($MANIFEST_STRING_TOR)|g" "$JVM_JS_CONSTANTS_KT"

  if [ $EXTRACT_GEOIP == true ]; then
    echo "Extracting geoip files..."
    local SHA256_GEOIP=
    SHA256_GEOIP=$(sha256sum "$GEOIP_ZIP" | cut -d ' ' -f 1)

    local GEOIP_SRC=
    local GEOIP_CONSTANTS_KT_ANDROID=
    local GEOIP_CONSTANTS_KT_JVM_JS=
    local GEOIP_CONSTANTS_KT_NATIVE=
    GEOIP_SRC="$DIR/../../../kmp-tor-binary-geoip/src"
    GEOIP_CONSTANTS_KT_ANDROID="$KMP_EXTRACT_SRC_DIR/androidMain/$KMP_EXTRACT_CONSTANTS_KT_PATH"
    GEOIP_CONSTANTS_KT_JVM_JS="$JVM_JS_CONSTANTS_KT"
    GEOIP_CONSTANTS_KT_NATIVE="$KMP_EXTRACT_SRC_DIR/nativeMain/$KMP_EXTRACT_CONSTANTS_KT_PATH"

    if ! checkFileExists "$GEOIP_CONSTANTS_KT_ANDROID"; then
        echo "ERROR: Something went wrong... $GEOIP_CONSTANTS_KT_ANDROID does not exist"
        EXIT_CODE=1
        return 1
    fi

    if ! checkFileExists "$GEOIP_CONSTANTS_KT_JVM_JS"; then
        echo "ERROR: Something went wrong... $GEOIP_CONSTANTS_KT_JVM_JS does not exist"
        EXIT_CODE=1
        return 1
    fi

    if ! checkFileExists "$GEOIP_CONSTANTS_KT_NATIVE"; then
        echo "ERROR: Something went wrong... $GEOIP_CONSTANTS_KT_NATIVE does not exist"
        EXIT_CODE=1
        return 1
    fi

    mkdir -p "$GEOIP_SRC/androidMain/assets/kmptor/"
    mkdir -p "$GEOIP_SRC/jvmJsMain/resources/kmptor/"
    mkdir -p "$GEOIP_SRC/nativeMain/resources/kmptor/"

    cp "$GEOIP_ZIP" "$GEOIP_SRC/androidMain/assets/kmptor/"
    cp "$GEOIP_ZIP" "$GEOIP_SRC/jvmJsMain/resources/kmptor/"
    cp "$GEOIP_ZIP" "$GEOIP_SRC/nativeMain/resources/kmptor/"

    sed -i "s|private const val _ZIP_SHA256_GEOIP = .*|private const val _ZIP_SHA256_GEOIP = \"$SHA256_GEOIP\"|g" "$GEOIP_CONSTANTS_KT_ANDROID"
    sed -i "s|private const val _ZIP_SHA256_GEOIP = .*|private const val _ZIP_SHA256_GEOIP = \"$SHA256_GEOIP\"|g" "$GEOIP_CONSTANTS_KT_JVM_JS"
    sed -i "s|private const val _ZIP_SHA256_GEOIP = .*|private const val _ZIP_SHA256_GEOIP = \"$SHA256_GEOIP\"|g" "$GEOIP_CONSTANTS_KT_NATIVE"

    sed -i "s|actual val ZIP_MANIFEST_GEOIP: List<String> get() = listOf(.*|actual val ZIP_MANIFEST_GEOIP: List<String> get() = listOf($MANIFEST_STRING_GEOIP)|g" "$GEOIP_CONSTANTS_KT_ANDROID"
    sed -i "s|actual val ZIP_MANIFEST_GEOIP: List<String> get() = listOf(.*|actual val ZIP_MANIFEST_GEOIP: List<String> get() = listOf($MANIFEST_STRING_GEOIP)|g" "$GEOIP_CONSTANTS_KT_JVM_JS"
    sed -i "s|actual val ZIP_MANIFEST_GEOIP: List<String> get() = listOf(.*|actual val ZIP_MANIFEST_GEOIP: List<String> get() = listOf($MANIFEST_STRING_GEOIP)|g" "$GEOIP_CONSTANTS_KT_NATIVE"
  else
    echo "Skipping geoip file extraction..."
  fi

  rm -rf "$TEMP_DIR"
}

function help() {
  SCRIPT_START_TIME=""
  echo ""
  echo "HELP:"
  echo "    build_binaries.sh <target>"
  echo ""
  echo "    targets:"
  echo "                all"
  echo ""
  echo "                android-all"
  echo "                android-armv7"
  echo "                android-aarch64"
  echo "                android-x86"
  echo "                android-x86_64"
  echo ""
  echo "                desktop-all"
  echo "                linux-i686"
  echo "                linux-x86_64"
  echo "                osx-x86_64"
  echo "                windows-i686"
  echo "                windows-x86_64"
  echo ""
}

function checkExit() {
  if [[ $EXIT_CODE != 0 || "$1" == "exit" ]]; then
    if [ "$SCRIPT_START_TIME" != "" ]; then
      local SCRIPT_END_TIME=
      local SCRIPT_RUN_TIME=
      SCRIPT_END_TIME=$(date +%s)
      SCRIPT_RUN_TIME=$((SCRIPT_END_TIME-SCRIPT_START_TIME))
      echo ""
      echo "Script runtime: ${SCRIPT_RUN_TIME}s"
    fi

    changeDir "$TOR_BUILD_DIR"
    # Revert patches here (reverse order)
#    git apply -R "$DIR/../patches/0001-set-tor-version-0.4.6.10.patch"
    exit $EXIT_CODE
  fi
}

case $1 in
  "all")
    echo "Building android-all..."
    buildAndroid "android-all"
    sleep 1
    checkExit

    echo "Building desktop-all..."

    buildDesktop "linux-i686"
    sleep 1
    checkExit

    buildDesktop "linux-x86_64"
    sleep 1
    checkExit

    buildDesktop "osx-x86_64"
    sleep 1
    checkExit

    buildDesktop "windows-i686"
    sleep 1
    checkExit

    buildDesktop "windows-x86_64"
    sleep 1
    ;;
  "android-all")
    buildAndroid "$1"
    ;;
  "android-armv7")
    buildAndroid "$1"
    ;;
  "android-aarch64")
    buildAndroid "$1"
    ;;
  "android-x86")
    buildAndroid "$1"
    ;;
  "android-x86_64")
    buildAndroid "$1"
    ;;
  "desktop-all")
    buildDesktop "linux-i686"
    sleep 1
    checkExit

    buildDesktop "linux-x86_64"
    sleep 1
    checkExit

    buildDesktop "osx-x86_64"
    sleep 1
    checkExit

    buildDesktop "windows-i686"
    sleep 1
    checkExit

    buildDesktop "windows-x86_64"
    sleep 1
    ;;
  "linux-i686")
    buildDesktop "$1"
    ;;
  "linux-x86_64")
    buildDesktop "$1"
    ;;
  "osx-x86_64")
    buildDesktop "$1"
    ;;
  "windows-i686")
    buildDesktop "$1"
    ;;
  "windows-x86_64")
    buildDesktop "$1"
    ;;
  *)
    help
    ;;

esac

checkExit "exit"
