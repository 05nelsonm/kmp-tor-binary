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
    Geoip files have been copied to module kmp-tor-binary-geoip
  "
}

binaries() {
  # TODO
  echo "
  $1
  "
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
