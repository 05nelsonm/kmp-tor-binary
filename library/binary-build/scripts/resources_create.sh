#!/bin/sh
# Copyright (c) 2023 Matthew Nelson
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

export LC_ALL=C
set -e

# Commands
readonly CMD_ALL="all"
readonly CMD_LINUX="linux"
readonly CMD_MACOS="macos"
readonly CMD_MINGW="mingw"
readonly CMD_GEOIPS="geoips"

# Absolute path to the directory which this script resides in
readonly DIR=$( cd "$( dirname "$0" )" >/dev/null && pwd )
readonly DIR_BUILT="$DIR/../built"
readonly DIR_PROJECT="$DIR/../../.."

# Programs
readonly TOOLING="$DIR_PROJECT/tooling"
readonly OPENSSL=$(which openssl)
readonly GZIP=$(which gzip)

help() {
  echo "
    $0
    Copyright (C) 2023 Matthew Nelson
    License Apache 2.0

    Apply codesignatures (if applicable), gzips, and moves files to their
    respective module resource directories.

    Location: $DIR
    Syntax: $0 [command]

    Commands:
          $CMD_ALL
          $CMD_GEOIPS
          $CMD_LINUX
          $CMD_MACOS
          $CMD_MINGW

    Example: $0 $CMD_MACOS
  "
}

initialize() {
  if ! . "$DIR/source.sh"; then
    echo "
    ERROR: Failed to source source.sh
    "
    exit 1
  fi

  check_find
  check_tar

  if [ "$OPENSSL" = "" ]; then
    echo "
    ERROR: openssl is required to be installed to run this script
    "
    exit 1
  fi

  if [ "$GZIP" = "" ]; then
    echo "
    ERROR: gzip is required to be installed to run this script
    "
    exit 1
  fi
}

SHA256() {
  if [ ! -f "$1" ]; then
    exit 1
  fi

  ${OPENSSL} dgst -sha256 "$1" | rev | cut -d ' ' -f 1 | rev
}

geoips() {
  change_dir_or_exit "$DIR_BUILT"

  if [ ! -f "$CMD_GEOIPS.tar.gz" ]; then
    echo "
    ERROR: File does not exist [$DIR_BUILT/$CMD_GEOIPS.tar.gz]
    "
    exit 1
  fi

  # shellcheck disable=SC2115
  rm -rf "$DIR_BUILT/$CMD_GEOIPS"
  mkdir "$DIR_BUILT/$CMD_GEOIPS"

  # Extract geoip file archive contents
  ${TAR} -xz -C "$DIR_BUILT/$CMD_GEOIPS" -f "$DIR_BUILT/$CMD_GEOIPS.tar.gz"

  # Take pre-gzipped sha256 values
  SHA256_GEOIP=$(SHA256 "$DIR_BUILT/$CMD_GEOIPS/geoip")
  SHA256_GEOIP6=$(SHA256 "$DIR_BUILT/$CMD_GEOIPS/geoip6")

  # Gzip directory contents
  ${GZIP} -rn "$DIR_BUILT/$CMD_GEOIPS"

  # Copy gzipped files to module resource directories
  DIR_GEOIP_SRC="$DIR/../../kmp-tor-binary-geoip/src"

  mkdir -p "$DIR_GEOIP_SRC/androidMain/res/raw/"
  mkdir -p "$DIR_GEOIP_SRC/jvmMain/resources/kmptor/"
  mkdir -p "$DIR_GEOIP_SRC/nativeMain/resources/kmptor/"

  change_dir_or_exit "$DIR_BUILT/$CMD_GEOIPS"
  cp -R . "$DIR_GEOIP_SRC/androidMain/res/raw/"
  cp -R . "$DIR_GEOIP_SRC/jvmMain/resources/kmptor/"
  cp -R . "$DIR_GEOIP_SRC/nativeMain/resources/kmptor/"

  # Write sha256 values
  TOR_RESOURCE_KT_COMMON="$DIR/../../kmp-tor-binary-extract/src/commonMain/kotlin/io/matthewnelson/kmp/tor/binary/extract/TorResource.kt"
  sed -i "s|/\* GEOIP \*/ override val sha256sum: String get() = .*|/\* GEOIP \*/ override val sha256sum: String get() = \"$SHA256_GEOIP\"|g" "$TOR_RESOURCE_KT_COMMON"
  sed -i "s|/\* GEOIP6 \*/ override val sha256sum: String get() = .*|/\* GEOIP6 \*/ override val sha256sum: String get() = \"$SHA256_GEOIP6\"|g" "$TOR_RESOURCE_KT_COMMON"

  # Clean up
  change_dir_or_exit "$DIR"
  # shellcheck disable=SC2115
  rm -rf "$DIR_BUILT/$CMD_GEOIPS"

  echo "
    Geoip files have been copied to
    the kmp-tor-binary-geoip module
  "
}

check_architecture() {
  case $1 in
    "arm64"|"x64"|"x86")
      return 0
      ;;
  esac

  echo "
    ERROR: Unknown architecture [$1]
  "
  exit 1
}

binaries() {
  change_dir_or_exit "$DIR_BUILT/$1"

  for ARCHIVE in "$(pwd)"/*-unsigned.tar.gz; do
    DIR_WORK=$(echo "$ARCHIVE" | sed "s|-unsigned.tar.gz||g")
    ARCH=$(echo "$DIR_WORK" | rev | cut -d '/' -f 1 | rev | sed "s|tor-$1-||g")

    check_architecture "$ARCH"

    rm -rf "$DIR_WORK"
    mkdir -p "$DIR_WORK"
    ${TAR} -xz -C "$DIR_WORK" -f "$ARCHIVE"

    change_dir_or_exit "$DIR_WORK"
    # shellcheck disable=SC2035
    FILES=$(${FIND} * -type f)

    # Apply signatures for macos and windows binaries
    if [ "$1" = "$CMD_MACOS" ] || [ "$1" = "$CMD_MINGW" ]; then
      DIR_SIGNATURES="$DIR_WORK-signatures"

      # Extract signatures
      rm -rf "$DIR_SIGNATURES"
      mkdir -p "$DIR_SIGNATURES"
      ${TAR} -xz -C "$DIR_SIGNATURES" -f "$DIR_SIGNATURES.tar.gz"

      change_dir_or_exit "$DIR_PROJECT"

      # Apply signatures to each file
      for FILE in $FILES; do
        if ! ${TOOLING} diff-cli apply --quiet "$DIR_SIGNATURES/$FILE.signature" "$DIR_WORK/$FILE"; then
          exit 1
        fi

        # Fix file permissions
        chmod 700 "$DIR_WORK/$FILE"

        echo "    Detached signature has been applied to $1[$ARCH] file $FILE"
      done

      # Clean up
      change_dir_or_exit "$DIR_WORK"
      rm -rf "$DIR_SIGNATURES"
      sleep 1
    fi

    # Build sha256 values + file manifest list
    FILES_MANIFEST=""
    FILES_SHA256=""
    for FILE in $FILES; do
      H=$(SHA256 "$FILE")

      if [ "$FILES_SHA256" = "" ]; then
        FILES_MANIFEST="\"$FILE.gz\""
        FILES_SHA256="$H"
      else
        FILES_MANIFEST="$FILES_MANIFEST, \"$FILE.gz\""
        FILES_SHA256="$FILES_SHA256\n$H"
      fi
    done

    # Take the sha256 hash of all the sha256 values combined
    FILES_SHA256=$(echo "$FILES_SHA256" | ${OPENSSL} dgst -sha256 | rev | cut -d ' ' -f 1 | rev)

    # Gzip directory contents
    ${GZIP} -rn "$DIR_WORK"

    SOURCE_SET="commonMain"

    # macOS binaries go in jvmMain source set
    if [ "$1" = "$CMD_MACOS" ]; then
      SOURCE_SET="jvmMain"
    fi

    DIR_RESOURCES="$DIR/../../kmp-tor-binary-$1$ARCH/src/$SOURCE_SET/resources/kmptor/$1/$ARCH"

    # Remove all old files and copy over new
    rm -rf "$DIR_RESOURCES"
    mkdir -p "$DIR_RESOURCES"
    cp -ar . "$DIR_RESOURCES"

    # Update TorResource.kt data for jvmJs
    TOR_RESOURCE_KT_JVMJS="$DIR/../../kmp-tor-binary-extract/src/jvmJsMain/kotlin/io/matthewnelson/kmp/tor/binary/extract/TorResource.kt"
    # shellcheck disable=SC2060
    CONST_KT_TAG=$(echo "$1_$ARCH" | tr [a-z] [A-Z])

    # Write sha256 & manifest values for jvmJs TorResource.kt
    sed -i "s|/\* $CONST_KT_TAG \*/ override val resourceManifest: List<String> get() = .*|/\* $CONST_KT_TAG \*/ override val resourceManifest: List<String> get() = listOf($FILES_MANIFEST)|g" "$TOR_RESOURCE_KT_JVMJS"
    sed -i "s|/\* $CONST_KT_TAG \*/ override val sha256sum: String get() = .*|/\* $CONST_KT_TAG \*/ override val sha256sum: String get() = \"$FILES_SHA256\"|g" "$TOR_RESOURCE_KT_JVMJS"

    # Linux x64 & Mingw x64 binaries also have values that
    # need to be updated for native TorResource.kt
    if [ "$ARCH" = "x64" ]; then
      if [ "$1" != "$CMD_MACOS" ]; then
        TOR_RESOURCE_KT_NATIVE="$DIR/../../kmp-tor-binary-extract/src/$1X64Main/kotlin/io/matthewnelson/kmp/tor/binary/extract/TorResource.kt"
        sed -i "s|/\* $CONST_KT_TAG \*/ override val resourceManifest: List<String> get() = .*|/\* $CONST_KT_TAG \*/ override val resourceManifest: List<String> get() = listOf($FILES_MANIFEST)|g" "$TOR_RESOURCE_KT_NATIVE"
        sed -i "s|/\* $CONST_KT_TAG \*/ override val sha256sum: String get() = .*|/\* $CONST_KT_TAG \*/ override val sha256sum: String get() = \"$FILES_SHA256\"|g" "$TOR_RESOURCE_KT_NATIVE"
      fi
    fi

    change_dir_or_exit "$DIR_BUILT/$1"
    rm -rf "$DIR_WORK"

    echo "
    Tor binaries for $1[$ARCH] have been moved to
    kmp-tor-binary-$1$ARCH module $SOURCE_SET/resources
    "
  done
}

case $1 in
  "$CMD_ALL")
    initialize

    geoips
    binaries "$CMD_LINUX"
    binaries "$CMD_MACOS"
    binaries "$CMD_MINGW"
    ;;
  "$CMD_GEOIPS")
    initialize
    geoips
    ;;
  "$CMD_LINUX"|"$CMD_MACOS"|"$CMD_MINGW")
    initialize
    binaries "$1"
    ;;
  *)
    help
    ;;
esac

exit 0
