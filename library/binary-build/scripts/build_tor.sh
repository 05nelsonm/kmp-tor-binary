#!/usr/bin/env bash
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

readonly TIME_START=$(date +%s)

# Absolute path to the directory which this script resides
readonly DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null && pwd)"
readonly DIR_TOR_BUILD="$DIR/../tor-browser-build"
readonly DIR_TOR_BUILD_OUT="$DIR_TOR_BUILD/out/tor"

# commands
readonly CMD_ALL="all"
readonly CMD_A_ARMV7="android-armv7"
readonly CMD_A_AARCH64="android-aarch64"
readonly CMD_A_X86="android-x86"
readonly CMD_A_X86_64="android-x86_64"
readonly CMD_L_I686="linux-i686"
readonly CMD_L_X86_64="linux-x86_64"
readonly CMD_M_AARCH64="macos-aarch64"
readonly CMD_M_X86_64="macos-x86_64"
readonly CMD_W_I686="windows-i686"
readonly CMD_W_X86_64="windows-x86_64"

# Programs
readonly FIND=$(which find)
readonly GIT=$(which git)
readonly MAKE=$(which make)
readonly TAR=$(which tar)

help() {
  echo "
    build_tor.sh
    Copyright (C) 2023 Matthew Nelson
    License Apache 2.0

    Builds tor via tor-browser-build submodule for specified target(s)
    Binaries are output to directory /binary-build/out/

    Location: $DIR/build_tor.sh
    Syntax: ./build_tor.sh [target]

    Targets:
                $CMD_ALL               Builds all targets listed below

                $CMD_A_AARCH64
                $CMD_A_ARMV7
                $CMD_A_X86
                $CMD_A_X86_64

                $CMD_L_I686
                $CMD_L_X86_64
                $CMD_M_AARCH64
                $CMD_M_X86_64
                $CMD_W_I686
                $CMD_W_X86_64
  "
}

apply_git_patches() {
  return 0
}

remove_git_patches() {
  return 0
}

change_dir_or_exit() {
  if cd "$1"; then
    return 0
  fi

  echo "
    ERROR: Failed to change dirs to $1
  "
  exit 1
}

initialize() {
    if [ "$FIND" == "" ]; then
      echo "
      ERROR: find is required to be installed to run this script
      "
      exit 1
    fi

    if [ "$GIT" == "" ]; then
      echo "
      ERROR: git is required to be installed to run this script
      "
      exit 1
    fi

    if [ "$MAKE" == "" ]; then
      echo "
      ERROR: make is required to be installed to run this script
      "
      exit 1
    fi

    if [ "$TAR" == "" ]; then
      echo "
      ERROR: tar is a required to be installed to run this script
      "
      exit 1
    fi

    change_dir_or_exit "$DIR"

    if ! ${GIT} submodule update --init; then
      echo "
      ERROR: Failed to checkout tor-browser-build submodule
      "
      exit 1
    fi

    apply_git_patches
    trap remove_git_patches EXIT
}

pre_build_setup() {
  change_dir_or_exit "$DIR_TOR_BUILD"

  if [ -d "$DIR_TOR_BUILD_OUT" ]; then
    rm -rf "$DIR_TOR_BUILD_OUT"
  fi

  echo "
    Building $1...
  "

  if ! ${MAKE} submodule-update; then
    exit 1
  fi
}

build_and_unpack_tor() {
  if ! ./rbm/rbm build tor --target release --target "torbrowser-$1"; then
    exit 1
  fi

  change_dir_or_exit "$DIR_TOR_BUILD_OUT"

  local ARCHIVE=
  for ARCHIVE in "$(pwd)"/*.tar.gz; do
    break
  done

  if [[ "$ARCHIVE" == "" || ! -f "$ARCHIVE" ]]; then
    echo "
    ERROR: Failed to get *.tar.gz file handle for $1 from tor-browser-build/out/tor/
    "
    exit 1
  fi

  if ! ${TAR} -xzf "$ARCHIVE"; then
    echo "
    ERROR: Failed to extract contents of $ARCHIVE
    "
    exit 1
  fi

  sleep 1
}

build_android() {
  local ARCH=
  if [ "$1" == "$CMD_A_AARCH64" ]; then
    ARCH="arm64-v8a"
  elif [ "$1" == "$CMD_A_ARMV7" ]; then
    ARCH="armeabi-v7a"
  elif [ "$1" == "$CMD_A_X86" ]; then
    ARCH="x86"
  elif [ "$1" == "$CMD_A_X86_64" ]; then
    ARCH="x86_64"
  else
    echo "
    ERROR: Failed to determine architecture for $1
    "
  fi

  pre_build_setup "$1"
  build_and_unpack_tor "$1"

  # Android binaries are copied directly to jniLibs as there is no
  # intermediate step necessary for code signing or gzipping
  local ANDROID_MAIN_DIR=
  ANDROID_MAIN_DIR="$DIR/../../kmp-tor-binary-android/src/androidMain"
  mkdir -p "$ANDROID_MAIN_DIR/jniLibs/$ARCH"
  cp -ar "$DIR_TOR_BUILD_OUT/tor/libTor.so" "$ANDROID_MAIN_DIR/jniLibs/$ARCH/libKmpTor.so"

  echo "
    Tor binaries for $1 have been
    copied to kmp-tor-binary-android/src/androidMain/jniLibs
  "
}

build_desktop() {
  local OS=
  local ARCH=
  if [ "$1" == "$CMD_L_I686" ]; then
    OS="linux"
    ARCH="x86"
  elif [ "$1" == "$CMD_L_X86_64" ]; then
    OS="linux"
    ARCH="x64"
  elif [ "$1" == "$CMD_M_AARCH64" ]; then
    OS="macos"
    ARCH="arm64"
  elif [ "$1" == "$CMD_M_X86_64" ]; then
    OS="macos"
    ARCH="x64"
  elif [ "$1" == "$CMD_W_I686" ]; then
    OS="mingw"
    ARCH="x86"
  elif [ "$1" == "$CMD_W_X86_64" ]; then
    OS="mingw"
    ARCH="x64"
  else
    echo "
    ERROR: Failed to determine os and architecture for $1
    "
  fi

  pre_build_setup "$1"
  build_and_unpack_tor "$1"

  # TODO
}

case $1 in
  "$CMD_ALL")
    initialize
    build_android "$CMD_A_AARCH64"; sleep 1
    build_android "$CMD_A_ARMV7"; sleep 1
    build_android "$CMD_A_X86"; sleep 1
    build_android "$CMD_A_X86_64"; sleep 1

    build_desktop "$CMD_L_I686"; sleep 1
    build_desktop "$CMD_L_X86_64"; sleep 1
    build_desktop "$CMD_M_AARCH64"; sleep 1
    build_desktop "$CMD_M_X86_64"; sleep 1
    build_desktop "$CMD_W_I686"; sleep 1
    build_desktop "$CMD_W_X86_64"; sleep 1
    ;;
  "$CMD_A_ARMV7"|"$CMD_A_AARCH64"|"$CMD_A_X86"|"$CMD_A_X86_64")
    initialize
    build_android "$1"
    ;;
  "$CMD_L_I686"|"$CMD_L_X86_64"|"$CMD_M_AARCH64"|"$CMD_M_X86_64"|"$CMD_W_I686"|"$CMD_W_X86_64")
    initialize
    build_desktop "$1"
    ;;
  *)
    help
    exit 0
    ;;
esac

TIME_RUN=$(($(date +%s)-TIME_START))
echo "
    Script runtime: ${TIME_RUN}s
"
exit 0
