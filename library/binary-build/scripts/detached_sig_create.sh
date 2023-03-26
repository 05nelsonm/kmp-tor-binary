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

# Absolute path to the directory which this script resides in
readonly DIR=$( cd "$( dirname "$0" )" >/dev/null && pwd )
readonly DIR_BUILT="$DIR/../built"
readonly DIR_MACOS="$DIR_BUILT/macos"
readonly DIR_MINGW="$DIR_BUILT/mingw"
readonly DIR_PROJECT="$DIR/../../.."
readonly TOOLING="$DIR_PROJECT/tooling"

# Commands
readonly CMD_MACOS="macos"
readonly CMD_MINGW="mingw"

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
  check_rcodesign

  printf "Path to .p12 key file (e.g. /home/user/dir/key.p12): "
  read -r PATH_P12_KEY

  if [ ! -f "$PATH_P12_KEY" ]; then
    echo "
    File does not exist [$PATH_P12_KEY]
    "
    exit 1
  fi

  printf "Path to App Store Connect api-key json file (e.g. /home/user/dir/api-key.json): "
  read -r PATH_API_KEY

  if [ ! -f "$PATH_API_KEY" ]; then
    echo "
    File does not exist [$PATH_API_KEY]
    "
    exit 1
  fi

  change_dir_or_exit "$DIR_MACOS"

  for ARCHIVE in "$(pwd)"/*-unsigned.tar.gz; do
    DIR_SIGNATURES=$(echo "$ARCHIVE" | sed "s|-unsigned.tar.gz|-signatures|g")
    OUT="$DIR_SIGNATURES.tar.gz"
    BUNDLE=$(echo "$ARCHIVE" | sed "s|-unsigned.tar.gz|.app|g")
    BUNDLE_MACOS="$BUNDLE/Contents/MacOS"
    BUNDLE_TOR="$BUNDLE_MACOS/Tor"
    BUNDLE_UNSIGNED="$BUNDLE_MACOS/Unsigned"

    rm -rf "$BUNDLE"
    mkdir -p "$BUNDLE_TOR"

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

    if ! ${RCODESIGN} sign --p12-file "$PATH_P12_KEY" --code-signature-flags runtime "$BUNDLE"; then
      exit 1
    fi

    if ! ${RCODESIGN} notary-submit --api-key-path "$PATH_API_KEY" --staple "$BUNDLE"; then
      exit 1
    fi

    mkdir -p "$BUNDLE_UNSIGNED"
    ${TAR} -xz -C "$BUNDLE_UNSIGNED" -f "$ARCHIVE"
    change_dir_or_exit "$BUNDLE_UNSIGNED"
    # shellcheck disable=SC2035
    FILES=$(${FIND} * -type f)

    change_dir_or_exit "$DIR_PROJECT"

    for FILE in $FILES; do
      ${TOOLING} diff-cli create --diff-ext-name ".signature" "$BUNDLE_UNSIGNED/$FILE" "$BUNDLE_TOR/$FILE" "$DIR_SIGNATURES"
    done

    change_dir_or_exit "$DIR_SIGNATURES"
    # shellcheck disable=SC2035
    ${TAR} -cz * -f "$OUT"
    echo "
    Created $OUT
    "

    rm -rf "$BUNDLE"
    rm -rf "$DIR_SIGNATURES"

    change_dir_or_exit "$DIR_MACOS"
    sleep 3
  done
}

mingw() {
  initialize
  check_osslsigncode

  change_dir_or_exit "$DIR_MINGW"
  # TODO
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
