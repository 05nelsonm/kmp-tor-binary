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
}

geoips() {
  # TODO
  echo "
  $CMD_GEOIPS
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
