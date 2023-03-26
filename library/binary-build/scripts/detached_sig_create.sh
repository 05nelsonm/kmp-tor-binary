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
readonly CMD_MACOS="macos"
readonly CMD_MINGW="mingw"

# Absolute path to the directory which this script resides in
readonly DIR=$( cd "$( dirname "$0" )" >/dev/null && pwd )
readonly DIR_BUILT="$DIR/../built"
readonly DIR_MACOS="$DIR_BUILT/$CMD_MACOS"
readonly DIR_MINGW="$DIR_BUILT/$CMD_MINGW"
readonly DIR_PROJECT="$DIR/../../.."

# Programs
readonly TOOLING="$DIR_PROJECT/tooling"
readonly RCODESIGN=$(which rcodesign)
readonly OSSLSIGNCODE=$(which osslsigncode)

help() {
  echo "
    $0
    Copyright (C) 2023 Matthew Nelson
    License Apache 2.0

    Interactive script for creating detached signatures for macos
    and windows binaries

    Location: $DIR
    Syntax: $0 [command]

    Commands:
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
}

macos() {
  initialize

  if [ "$RCODESIGN" = "" ]; then
    echo "
    ERROR: Apple Codesign is required to be installed to run this script
           See https://gregoryszorc.com/docs/apple-codesign/main/apple_codesign_getting_started.html#installing
    "
    exit 1
  fi

  # Read in .p12 key file path
  printf "Path to .p12 key file (e.g. /home/user/dir/key.p12): "
  read -r PATH_P12_KEY

  if [ ! -f "$PATH_P12_KEY" ]; then
    echo "
    ERROR: File does not exist [$PATH_P12_KEY]
    "
    exit 1
  fi

  # Read in App Store Connect apikey.json file path
  printf "Path to App Store Connect api-key json file (e.g. /home/user/dir/api_key.json): "
  read -r PATH_API_KEY

  if [ ! -f "$PATH_API_KEY" ]; then
    echo "
    ERROR: File does not exist [$PATH_API_KEY]
    "
    exit 1
  fi

  change_dir_or_exit "$DIR_MACOS"

  # For all archives in built/macos ending in -unsigned.tar.gz
  for ARCHIVE in "$(pwd)"/*-unsigned.tar.gz; do
    DIR_SIGNATURES=$(echo "$ARCHIVE" | sed "s|-unsigned.tar.gz|-signatures|g")
    OUT="$DIR_SIGNATURES.tar.gz"
    BUNDLE=$(echo "$ARCHIVE" | sed "s|-unsigned.tar.gz|.app|g")
    BUNDLE_MACOS="$BUNDLE/Contents/MacOS"
    BUNDLE_TOR="$BUNDLE_MACOS/Tor"
    BUNDLE_UNSIGNED="$BUNDLE_MACOS/Unsigned"

    rm -rf "$BUNDLE"

    # Create .app bundle dirs
    mkdir -p "$BUNDLE_TOR"

    # Generate our Info.plist manifest
    touch "$BUNDLE/Contents/Info.plist"
    echo '<?xml version="1.0" encoding="UTF-8"?>
<plist version="1.0">
<dict>
    <key>CFBundleExecutable</key>
    <string>tor.program</string>
    <key>CFBundleIdentifier</key>
    <string>io.matthewnelson</string>
    <key>LSUIElement</key>
    <true/>
</dict>
</plist>' > "$BUNDLE/Contents/Info.plist"

    # Extract unsigned archive contents to the Tor directory within the bundle
    ${TAR} -xz -C "$BUNDLE_TOR" -f "$ARCHIVE"

    # By copying and setting tor.program as the bundle executable, we ensure
    # the same architecture is being used; otherwise notary rejects it.
    #
    # Cannot use the tor binary directly as the bundle executable because it
    # will get linked to the bundle resources when stapled such that if we
    # extract it to use in kmp-tor-binary, it will fail signature checks.
    if ! cp -a "$BUNDLE_TOR/tor" "$BUNDLE_MACOS/tor.program"; then
      exit 1
    fi

    # Sign everything in the app bundle
    if ! ${RCODESIGN} sign --p12-file "$PATH_P12_KEY" --code-signature-flags runtime "$BUNDLE"; then
      exit 1
    fi

    # Submit app bundle for notarization and stapling
    if ! ${RCODESIGN} notary-submit --api-key-path "$PATH_API_KEY" --staple "$BUNDLE"; then
      exit 1
    fi

    # Extract unsigned archive contents again to diff agains
    mkdir -p "$BUNDLE_UNSIGNED"
    ${TAR} -xz -C "$BUNDLE_UNSIGNED" -f "$ARCHIVE"
    change_dir_or_exit "$BUNDLE_UNSIGNED"
    # shellcheck disable=SC2035
    FILES=$(${FIND} * -type f)

    change_dir_or_exit "$DIR_PROJECT"

    # Create diffs between unsigned, and signed
    # binaries (.signature files) to be applied later
    for FILE in $FILES; do
      if ! ${TOOLING} diff-cli create \
           --diff-ext-name ".signature" \
           "$BUNDLE_UNSIGNED/$FILE" \
           "$BUNDLE_TOR/$FILE" \
           "$DIR_SIGNATURES"; then
        exit 1
      fi
    done

    change_dir_or_exit "$DIR_SIGNATURES"
    # shellcheck disable=SC2035
    ${TAR} -cz * -f "$OUT"
    echo "
    Created $OUT
    "

    # Clean up
    rm -rf "$BUNDLE"
    rm -rf "$DIR_SIGNATURES"

    change_dir_or_exit "$DIR_MACOS"
    sleep 3
  done
}

mingw() {
  initialize

  if [ "$OSSLSIGNCODE" = "" ]; then
    echo "
    ERROR: osslsigncode is required to be installed to run this script
           See https://github.com/mtrojnar/osslsigncode
    "
    exit 1
  fi

  # Read in .key file path
  printf "Path to .key file (e.g. /home/user/dir/my_key.key): "
  read -r PATH_KEY

  if [ ! -f "$PATH_KEY" ]; then
    echo "
    ERROR: File does not exist [$PATH_KEY]
    "
    exit 1
  fi

  # Read in cert file path
  printf "Path to cert file (e.g. /home/user/dir/cert.cer): "
  read -r PATH_CERT

  if [ ! -f "$PATH_CERT" ]; then
    echo "
    ERROR: File does not exist [$PATH_CERT]
    "
    exit 1
  fi

  change_dir_or_exit "$DIR_MINGW"

  # For all archives in built/mingw ending in -unsigned.tar.gz
  for ARCHIVE in "$(pwd)"/*-unsigned.tar.gz; do
    DIR_SIGNATURES=$(echo "$ARCHIVE" | sed "s|-unsigned.tar.gz|-signatures|g")
    OUT="$DIR_SIGNATURES.tar.gz"
    DIR_SIGNED=$(echo "$ARCHIVE" | sed "s|-unsigned.tar.gz|-signed|g")
    DIR_UNSIGNED=$(echo "$ARCHIVE" | sed "s|-unsigned.tar.gz|-unsigned|g" )

    rm -rf "$DIR_SIGNED"
    rm -rf "$DIR_UNSIGNED"
    rm -rf "$DIR_SIGNATURES"
    mkdir -p "$DIR_SIGNED"
    mkdir -p "$DIR_UNSIGNED"

    # Extract unsigned archive contents
    ${TAR} -xz -C "$DIR_UNSIGNED" -f "$ARCHIVE"
    change_dir_or_exit "$DIR_UNSIGNED"
    # shellcheck disable=SC2035
    FILES=$(${FIND} * -type f)

    change_dir_or_exit "$DIR_PROJECT"

    for FILE in $FILES; do
      # Sign each file
      if ! ${OSSLSIGNCODE} sign -certs "$PATH_CERT" \
           -key "$PATH_KEY" \
           -t "http://timestamp.comodoca.com" \
           -in "$DIR_UNSIGNED/$FILE" \
           -out "$DIR_SIGNED/$FILE"; then
        exit 1
      fi

      # Create diffs between unsigned, and signed
      # binaries (.signature files) to be applied later
      if ! ${TOOLING} diff-cli create \
           --diff-ext-name ".signature" \
           "$DIR_UNSIGNED/$FILE" \
           "$DIR_SIGNED/$FILE" \
           "$DIR_SIGNATURES"; then
         exit 1
       fi
    done

    change_dir_or_exit "$DIR_SIGNATURES"
    # shellcheck disable=SC2035
    ${TAR} -cz * -f "$OUT"
    echo "
    Created $OUT
    "

    # Clean up
    rm -rf "$DIR_SIGNATURES"
    rm -rf "$DIR_SIGNED"
    rm -rf "$DIR_UNSIGNED"

    change_dir_or_exit "$DIR_MINGW"
    sleep 1
  done
}

case $1 in
  "$CMD_MACOS")
    macos
    ;;
  "$CMD_MINGW")
    mingw
    ;;
  *)
    help
    ;;
esac

exit 0
