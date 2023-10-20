#!/usr/bin/env bash
# Copyright (c) 2023 Matthew Nelson
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
export LC_ALL=C
set -e

readonly DIR_SCRIPT=$( cd "$( dirname "$0" )" >/dev/null && pwd )
readonly FILE_BUILD_LOCK="$DIR_SCRIPT/build/.lock"

# Programs
readonly DOCKER=$(which docker)
readonly GIT=$(which git)
readonly XCRUN=$(which xcrun)

readonly U_ID=$(id -u)
readonly G_ID=$(id -g)

function build:all:android { ## Builds all Android targets
  build:android:arm
  build:android:arm64
  build:android:x86
  build:android:x86_64
}

function build:all:jvm { ## Builds all FreeBSD, Linux, macOS, Windows targets for JVM
  build:all:jvm:freebsd
  build:all:jvm:linux-libc
  build:all:jvm:linux-musl
  build:all:jvm:macos
  build:all:jvm:mingw
}

function build:all:jvm:freebsd { ## Builds all FreeBSD targets for JVM
  build:jvm:freebsd:aarch64
  build:jvm:freebsd:x86
  build:jvm:freebsd:x86_64
}

function build:all:jvm:linux-libc { ## Builds all Linux Libc targets for JVM
  build:jvm:linux-libc:aarch64
  build:jvm:linux-libc:armv5
  build:jvm:linux-libc:armv6
  build:jvm:linux-libc:armv7
  build:jvm:linux-libc:x86
  build:jvm:linux-libc:x86_64
}

function build:all:jvm:linux-musl { ## Builds all Linux Musl targets for JVM
  build:jvm:linux-musl:aarch64
  build:jvm:linux-musl:x86
  build:jvm:linux-musl:x86_64
}

function build:all:jvm:macos { ## Builds all macOS targets for JVM
  build:jvm:macos:aarch64
  build:jvm:macos:x86_64
}

function build:all:jvm:mingw { ## Builds all Windows targets for JVM
  build:jvm:mingw:x86
  build:jvm:mingw:x86_64
}

function build:android:arm { ## Builds Android armeabi-v7a
  local os_name="android"
  local os_arch="armeabi-v7a"
  __build:configure:target
}

function build:android:arm64 { ## Builds Android arm64-v8a
  local os_name="android"
  local os_arch="arm64-v8a"
  __build:configure:target
}

function build:android:x86 { ## Builds Android x86
  local os_name="android"
  local os_arch="x86"
  __build:configure:target
}

function build:android:x86_64 { ## Builds Android x86_64
  local os_name="android"
  local os_arch="x86_64"
  __build:configure:target
}

function build:jvm:freebsd:aarch64 { ## Builds FreeBSD aarch64 for JVM
  local os_name="freebsd"
  local os_arch="aarch64"
  __build:configure:target
}

function build:jvm:freebsd:x86 { ## Builds FreeBSD x86 for JVM
  local os_name="freebsd"
  local os_arch="x86"
  __build:configure:target
}

function build:jvm:freebsd:x86_64 { ## Builds FreeBSD x86_64 for JVM
  local os_name="freebsd"
  local os_arch="x86_64"
  __build:configure:target
}

function build:jvm:linux-libc:aarch64 { ## Builds Linux Libc aarch64 for JVM
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="aarch64"
  __build:configure:target
}

function build:jvm:linux-libc:armv5 { ## Builds Linux Libc armv5 for JVM
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="armv5"
  __build:configure:target
}

function build:jvm:linux-libc:armv6 { ## Builds Linux Libc armv6 for JVM
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="armv6"
  __build:configure:target
}

function build:jvm:linux-libc:armv7 { ## Builds Linux Libc armv7 for JVM
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="armv7"
  __build:configure:target
}

function build:jvm:linux-libc:x86 { ## Builds Linux Libc x86 for JVM
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="x86"
  __build:configure:target
}

function build:jvm:linux-libc:x86_64 { ## Builds Linux Libc x86_64 for JVM
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="x86_64"
  __build:configure:target
}

function build:jvm:linux-musl:aarch64 { ## Builds Linux Musl aarch64 for JVM
  local os_name="linux"
  local os_subtype="-musl"
  local os_arch="aarch64"
  __build:configure:target
}

function build:jvm:linux-musl:x86 { ## Builds Linux Musl x86 for JVM
  local os_name="linux"
  local os_subtype="-musl"
  local os_arch="x86"
  __build:configure:target
}

function build:jvm:linux-musl:x86_64 { ## Builds Linux Musl x86_64 for JVM
  local os_name="linux"
  local os_subtype="-musl"
  local os_arch="x86_64"
  __build:configure:target
}

function build:jvm:macos:aarch64 { ## Builds macOS aarch64 for JVM
  local os_name="macos"
  local os_arch="aarch64"
  __build:configure:target
}

function build:jvm:macos:x86_64 { ## Builds macOS x86_64 for JVM
  local os_name="macos"
  local os_arch="x86_64"
  __build:configure:target
}

function build:jvm:mingw:x86 { ## Builds Windows x86 for JVM
  local os_name="mingw"
  local os_arch="x86"
  __build:configure:target
}

function build:jvm:mingw:x86_64 { ## Builds Windows x86_64 for JVM
  local os_name="mingw"
  local os_arch="x86_64"
  __build:configure:target
}

# TODO: macOS, iOS, tvOS, watchOS frameworks

function clean { ## Cleans the build directory
  __require:no_build_lock
  rm -rf "$DIR_SCRIPT/build"
}

function help { ## THIS MENU
  # shellcheck disable=SC2154
  echo "
    $0
    Copyright (C) 2023 Matthew Nelson

    Build tor binaries

    Location: $DIR_SCRIPT
    Syntax: $0 [task]

    Tasks:
$(
    # function names + comments & colorization
    grep -E '^function .* {.*?## .*$$' "$0" |
    grep -v "^function __" |
    sed -e 's/function //' |
    sort |
    awk 'BEGIN {FS = "{.*?## "}; {printf "        \033[93m%-30s\033[92m %s\033[0m\n", $1, $2}'
)

    Example: $0 build:jvm:linux-libc:x86_64
  "
}


function __build:cleanup {
  rm -rf "$FILE_BUILD_LOCK"
  __build:git:stash "libevent"
  __build:git:stash "openssl"
  __build:git:stash "tor"
  __build:git:stash "xz"
  __build:git:stash "zlib"
}

function __build:configure:init {
  if ! echo "$1" | grep -q "^build"; then
    return 0
  fi

  __require:cmd "$GIT" "git"
  __require:no_build_lock

  ${GIT} submodule update --init

  mkdir -p "build"
  trap '__build:cleanup' EXIT
  touch "$FILE_BUILD_LOCK"

  __build:git:apply_patches "libevent"
  __build:git:apply_patches "openssl"
  __build:git:apply_patches "tor"
  __build:git:apply_patches "xz"
  __build:git:apply_patches "zlib"
  cd "$DIR_SCRIPT"
}

function __build:configure:target {
  __require:var_set "$os_name" "os_name"
  __require:var_set "$os_arch" "os_arch"

  local dir_platform=
  if [ -n "$is_framework" ]; then
    dir_platform="framework/"
    __require:cmd "$XCRUN" "xcrun (Xcode CLI tool on macOS machine)"
  else
    if [ "$os_name" != "android" ]; then
      dir_platform="jvm/"
    fi

    __require:cmd "$DOCKER" "docker"
    __require:var_set "$U_ID" "U_ID"
    __require:var_set "$G_ID" "G_ID"
  fi

  local dir_build="build/$dir_platform$os_name$os_subtype/$os_arch"
  DIR_BUILD_LIBEVENT="$dir_build/libevent"
  DIR_BUILD_OPENSSL="$dir_build/openssl"
  DIR_BUILD_TOR="$dir_build/tor"
  DIR_BUILD_XZ="$dir_build/xz"
  DIR_BUILD_ZLIB="$dir_build/zlib"

  rm -rf "$DIR_BUILD_LIBEVENT"
  rm -rf "$DIR_BUILD_OPENSSL"
  rm -rf "$DIR_BUILD_TOR"
  rm -rf "$DIR_BUILD_XZ"
  rm -rf "$DIR_BUILD_ZLIB"

  mkdir -p "$DIR_BUILD_LIBEVENT"
  mkdir -p "$DIR_BUILD_OPENSSL"
  mkdir -p "$DIR_BUILD_TOR"
  mkdir -p "$DIR_BUILD_XZ"
  mkdir -p "$DIR_BUILD_ZLIB"

  echo "
    Building $os_name$os_subtype:$os_arch
  "

  # TODO
}

function __build:git:apply_patches {
  __require:not_empty "$1" "project name must not be empty"
  cd "$DIR_SCRIPT/$1"

  local patch_file=
  for patch_file in "$DIR_SCRIPT/patches/$1/"*.patch; do
    if [ "$patch_file" = "$DIR_SCRIPT/patches/$1/*.patch" ]; then
      # no patch files
      continue
    fi

    echo "Applying git patch to $1 >> $patch_file"
    ${GIT} apply "$patch_file"
    sleep 0.25
  done
}

function __build:git:stash {
  __require:not_empty "$1" "project name must not be empty"
  cd "$DIR_SCRIPT/$1"
  ${GIT} add --all
  if [ "$(${GIT} stash)" = "No local changes to save" ]; then
    return 0
  fi
  ${GIT} stash drop
}

function __require:cmd {
  __require:not_empty "$1" "$2 is required to run this script"
}

function __require:var_set {
  __require:not_empty "$1" "$2 must be set"
}

function __require:not_empty {
  if [ -n "$1" ]; then
    return 0
  fi

  echo 1>&2 "
    ERROR: $2
  "
  exit 3
}

function __require:no_build_lock {
  if [ ! -f "$FILE_BUILD_LOCK" ]; then
    return 0
  fi

  echo 1>&2 "
    ERROR: Another build is in progress

    If this is not the case, delete the following file and re-run the task
    $FILE_BUILD_LOCK
  "
  exit 3
}

# Run
if [ -z "$1" ] || [ "$1" = "help" ] || echo "$1" | grep -q "^__"; then
  help
elif ! grep -qE "^function $1 {" "$0"; then
  echo 1>&2 "
    ERROR: Unknown task '$1'
  "
  help
else
  # Ensure always start in the external directory
  cd "$DIR_SCRIPT"
  __build:configure:init "$1"
  TIMEFORMAT="
    Task '$1' completed in %3lR
  "
  time ${1}
fi
