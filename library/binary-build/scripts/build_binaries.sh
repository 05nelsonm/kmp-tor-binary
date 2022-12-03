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

  local FILE_TOR_TAR_GZ=
  local ARCH=
  for FILE_TOR_TAR_GZ in "$(pwd)"/*.tar.gz; do

    if echo "$FILE_TOR_TAR_GZ" | grep "android-aarch64" >/dev/null; then
        ARCH="arm64-v8a"
    elif echo "$FILE_TOR_TAR_GZ" | grep "android-armv7" >/dev/null; then
        ARCH="armeabi-v7a"
    elif echo "$FILE_TOR_TAR_GZ" | grep "android-x86_64" >/dev/null; then
        ARCH="x86_64"
    elif echo "$FILE_TOR_TAR_GZ" | grep "android-x86" >/dev/null; then
        ARCH="x86"
    else
      echo "ERROR: Something went wrong. Could not identify architecture for $FILE_TOR_TAR_GZ"
      EXIT_CODE=1
      return 1
    fi

    tar -xzf "$FILE_TOR_TAR_GZ"
    sleep 1

    mkdir -p "$TEMP_DIR/jniLibs/$ARCH"
    cp -r "$TOR_OUT_DIR/tor/libTor.so" "$TEMP_DIR/jniLibs/$ARCH/"

    rm -rf "$TOR_OUT_DIR/tor"
    rm -rf "$TOR_OUT_DIR/data"
    sleep 1
  done

  if ! checkDirExists "$TEMP_DIR/jniLibs"; then
    echo "ERROR: Something went wrong... $TEMP_DIR/jniLibs does not exist"
    EXIT_CODE=1
    return 1
  fi

  cp -r "$TEMP_DIR/jniLibs" "$DIR/../../kmp-tor-binary-android/src/androidMain"
  rm -rf "$TEMP_DIR"

  local FILE=
  for FILE in $(find "$DIR/../../kmp-tor-binary-android/src/androidMain/jniLibs" -name "libTor.so" | grep "libTor.so"); do
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
  local TOR_RESOURCE_NATIVE_KT=
  local BINARY_DIR_SRC_SET="commonMain"
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
    TOR_RESOURCE_NATIVE_KT="$DIR/../../kmp-tor-binary-extract/src/linuxX64Main/kotlin/io/matthewnelson/kmp/tor/binary/extract/TorResource.kt"
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
    TOR_RESOURCE_NATIVE_KT="$DIR/../../kmp-tor-binary-extract/src/mingwX64Main/kotlin/io/matthewnelson/kmp/tor/binary/extract/TorResource.kt"
    ./rbm/rbm build tor --target release --target torbrowser-windows-x86_64
  elif [ "$1" == "osx-x86_64" ]; then
    ARCH="x64"
    PLATFORM="macos"
    CONST_KT_NAME="MACOS_X64"
    BINARY_DIR_SRC_SET="jvmMain"
    ./rbm/rbm build tor --target release --target torbrowser-osx-x86_64
  elif [ "$1" == "osx-aarch64" ]; then
    ARCH="arm64"
    PLATFORM="macos"
    CONST_KT_NAME="MACOS_ARM64"
    BINARY_DIR_SRC_SET="jvmMain"
    ./rbm/rbm build tor --target release --target torbrowser-osx-aarch64
  else
    echo "$1 is not a recognized target. Run script again w/o args to see help."
    EXIT_CODE=1
    return 1
  fi

  local TOR_RESOURCE_JVMJS_KT=
  local TOR_RESOURCE_COMMON_KT=
  TOR_RESOURCE_JVMJS_KT="$DIR/../../kmp-tor-binary-extract/src/jvmJsMain/kotlin/io/matthewnelson/kmp/tor/binary/extract/TorResource.kt"
  TOR_RESOURCE_COMMON_KT="$DIR/../../kmp-tor-binary-extract/src/commonMain/kotlin/io/matthewnelson/kmp/tor/binary/extract/TorResource.kt"

  if ! checkFileExists "$TOR_RESOURCE_JVMJS_KT"; then
    echo "ERROR: Something went wrong... TorResource.kt file for jvmJsMain does not exist"
    EXIT_CODE=1
    return 1
  fi

  if ! checkFileExists "$TOR_RESOURCE_COMMON_KT"; then
    echo "ERROR: Something went wrong... TorResource.kt file for commonMain does not exist"
    EXIT_CODE=1
    return 1
  fi

  if [ "$TOR_RESOURCE_NATIVE_KT" != "" ]; then
    if ! checkFileExists "$TOR_RESOURCE_NATIVE_KT"; then
      echo "ERROR: Something went wrong... TorResource.kt file for native does not exist"
      EXIT_CODE=1
      return 1
    fi
  fi

  changeDir "$TOR_OUT_DIR"

  # Extract files
  local FILE_TOR_TAR_GZ=
  for FILE_TOR_TAR_GZ in "$(pwd)"/*.tar.gz; do
    tar -xzf "$FILE_TOR_TAR_GZ"
    sleep 1

    break
  done

  # Get manifest of tor files
  local MANIFEST_TOR=
  changeDir "$TOR_OUT_DIR/tor"
  MANIFEST_TOR=$(find . -type f | sort)

  # Get sha256sum value of all tor files
  #
  # Creates a string with sha256sum values for each
  # file on a new line, then takes the sha256sum
  # value of that string.
  local SHA256SUM_TOR=
  SHA256SUM_TOR=$(sha256sum $MANIFEST_TOR | cut -d ' ' -f 1 | sha256sum | cut -d ' ' -f 1)

  # Compress files
  gzip -rn "$TOR_OUT_DIR/tor"

  # Copy files
  local BINARY_DIR=
  BINARY_DIR="$DIR/../../kmp-tor-binary-$PLATFORM$ARCH/src/$BINARY_DIR_SRC_SET/resources/kmptor/$PLATFORM/$ARCH"

  mkdir -p "$BINARY_DIR"
  cp -r . "$BINARY_DIR"

  # Build the manifest string to write to our kotlin constants file
  local MANIFEST_STRING_TOR=
  local LINE=
  for LINE in $MANIFEST_TOR; do
    # Strip ./
    local STRIPPED=
    STRIPPED=$(echo "${LINE:2}")

    if [ "$MANIFEST_STRING_TOR" == "" ]; then
      MANIFEST_STRING_TOR="\"$STRIPPED.gz\""
    else
      MANIFEST_STRING_TOR="$MANIFEST_STRING_TOR, \"$STRIPPED.gz\""
    fi
  done

  # Write sha256sum & manifest values
  sed -i "s|/\* $CONST_KT_NAME \*/ override val resourceManifest: List<String> get() = .*|/\* $CONST_KT_NAME \*/ override val resourceManifest: List<String> get() = listOf($MANIFEST_STRING_TOR)|g" "$TOR_RESOURCE_JVMJS_KT"
  sed -i "s|/\* $CONST_KT_NAME \*/ override val sha256sum: String get() = .*|/\* $CONST_KT_NAME \*/ override val sha256sum: String get() = \"$SHA256SUM_TOR\"|g" "$TOR_RESOURCE_JVMJS_KT"

  if [ "$TOR_RESOURCE_NATIVE_KT" != "" ]; then
    sed -i "s|/\* $CONST_KT_NAME \*/ override val resourceManifest: List<String> get() = .*|/\* $CONST_KT_NAME \*/ override val resourceManifest: List<String> get() = listOf($MANIFEST_STRING_TOR)|g" "$TOR_RESOURCE_NATIVE_KT"
    sed -i "s|/\* $CONST_KT_NAME \*/ override val sha256sum: String get() = .*|/\* $CONST_KT_NAME \*/ override val sha256sum: String get() = \"$SHA256SUM_TOR\"|g" "$TOR_RESOURCE_NATIVE_KT"
  fi

  echo ""
  echo "Tor files have been copied to module kmp-tor-binary-$PLATFORM$ARCH..."
  echo ""

  if [ $EXTRACT_GEOIP == true ]; then

    changeDir "$TOR_OUT_DIR/data"

    # Get sha256sum for geoip files
    local SHA256SUM_GEOIP=
    local SHA256SUM_GEOIP6=
    SHA256SUM_GEOIP=$(sha256sum "geoip" | cut -d ' ' -f 1)
    SHA256SUM_GEOIP6=$(sha256sum "geoip6" | cut -d ' ' -f 1)

    # Compress files
    gzip -rn "$TOR_OUT_DIR/data"

    if ! checkFileExists "geoip.gz"; then
      echo "ERROR: Something went wrong... geoip file does not exist"
      EXIT_CODE=1
      return 1
    fi

    if ! checkFileExists "geoip6.gz"; then
      echo "ERROR: Something went wrong... geoip6 file does not exist"
      EXIT_CODE=1
      return 1
    fi

    # Copy files
    local GEOIP_MODULE_SRC_DIR=
    GEOIP_MODULE_SRC_DIR="$DIR/../../kmp-tor-binary-geoip/src"

    mkdir -p "$GEOIP_MODULE_SRC_DIR/androidMain/res/raw/"
    mkdir -p "$GEOIP_MODULE_SRC_DIR/jvmMain/resources/kmptor/"
    mkdir -p "$GEOIP_MODULE_SRC_DIR/nativeMain/resources/kmptor/"
    cp -R . "$GEOIP_MODULE_SRC_DIR/androidMain/res/raw/"
    cp -R . "$GEOIP_MODULE_SRC_DIR/jvmMain/resources/kmptor/"
    cp -R . "$GEOIP_MODULE_SRC_DIR/nativeMain/resources/kmptor/"

    # Write sha256sum values
    sed -i "s|/\* GEOIP \*/ override val sha256sum: String get() = .*|/\* GEOIP \*/ override val sha256sum: String get() = \"$SHA256SUM_GEOIP\"|g" "$TOR_RESOURCE_COMMON_KT"
    sed -i "s|/\* GEOIP6 \*/ override val sha256sum: String get() = .*|/\* GEOIP6 \*/ override val sha256sum: String get() = \"$SHA256SUM_GEOIP6\"|g" "$TOR_RESOURCE_COMMON_KT"

    echo ""
    echo "Geoip files have been copied to module kmp-tor-binary-geoip..."
    echo ""
  fi

  # Cleanup
  changeDir "$TOR_OUT_DIR"

  rm -rf "$TOR_OUT_DIR/data"
  rm -rf "$TOR_OUT_DIR/debug"
  rm -rf "$TOR_OUT_DIR/tor"
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
  echo "                osx-aarch64"
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

function buildDesktopAll() {
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

  buildDesktop "osx-aarch64"
  sleep 1
  checkExit

  buildDesktop "windows-i686"
  sleep 1
  checkExit

  buildDesktop "windows-x86_64"
  sleep 1
}

case $1 in
  "all")
    echo "Building android-all..."
    buildAndroid "android-all"
    sleep 1
    checkExit

    buildDesktopAll
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
    buildDesktopAll
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
  "osx-aarch64")
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
