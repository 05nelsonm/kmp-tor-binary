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

readonly NUM_CPU=$(if [[ $NUM_CPU =~ ^[0-9]+$ ]]; then echo "$NUM_CPU"; else echo "2"; fi)
readonly DRY_RUN=$(if [ "$2" = "--dry-run" ]; then echo "true"; else echo "false"; fi)

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
  local openssl_target="android-arm"
  local host="armv7a-linux-androideabi"
  __build:configure:target:init

  __build:configure:target:build_script
#  __exec:docker:run "work" "05nelsonm/ubuntu16-linux-x86_64"
}

function build:android:arm64 { ## Builds Android arm64-v8a
  local os_name="android"
  local os_arch="arm64-v8a"
  local openssl_target="android-arm64"
  local host="aarch64-linux-android"
  __build:configure:target:init

  __build:configure:target:build_script
#  __exec:docker:run "work" "05nelsonm/ubuntu16-linux-x86_64"
}

function build:android:x86 { ## Builds Android x86
  local os_name="android"
  local os_arch="x86"
  local openssl_target="android-x86"
  local host="i686-linux-android"
  __build:configure:target:init

  __build:configure:target:build_script
#  __exec:docker:run "work" "05nelsonm/ubuntu16-linux-x86_64"
}

function build:android:x86_64 { ## Builds Android x86_64
  local os_name="android"
  local os_arch="x86_64"
  local openssl_target="android-x86_64"
  local host="x86_64-linux-android"
  __build:configure:target:init

  __build:configure:target:build_script
#  __exec:docker:run "work" "05nelsonm/ubuntu16-linux-x86_64"
}

function build:jvm:freebsd:aarch64 { ## Builds FreeBSD aarch64 for JVM
  local os_name="freebsd"
  local os_arch="aarch64"
  # TODO: Fix
  local openssl_target="freebsd-aarch64"
  # TODO: Fix
  local host="aarch64"
  __build:configure:target:init

  __build:configure:target:build_script
}

function build:jvm:freebsd:x86 { ## Builds FreeBSD x86 for JVM
  local os_name="freebsd"
  local os_arch="x86"
  # TODO: Fix
  local openssl_target="freebsd-x86"
  # TODO: Fix
  local host="i686"
  __build:configure:target:init

  __build:configure:target:build_script
}

function build:jvm:freebsd:x86_64 { ## Builds FreeBSD x86_64 for JVM
  local os_name="freebsd"
  local os_arch="x86_64"
  # TODO: Fix
  local openssl_target="freebsd-x86_64"
  # TODO: Fix
  local host="x86_64"
  __build:configure:target:init

  __build:configure:target:build_script
}

function build:jvm:linux-libc:aarch64 { ## Builds Linux Libc aarch64 for JVM
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="aarch64"
  local openssl_target="linux-aarch64"
  local host="aarch64"
  __build:configure:target:init

  __build:configure:target:build_script
}

function build:jvm:linux-libc:armv5 { ## Builds Linux Libc armv5 for JVM
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="armv5"
  local openssl_target="linux-armv5"
  # TODO: Fix
  local host="armv5-linux-gnueabi"
  __build:configure:target:init

  __conf:CFLAGS '-mfloat-abi=soft'
  __build:configure:target:build_script
}

function build:jvm:linux-libc:armv6 { ## Builds Linux Libc armv6 for JVM
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="armv6"
  local openssl_target="linux-armv6"
  # TODO: Fix
  local host="armv6-linux-gnueabi"
  __build:configure:target:init

  __conf:CFLAGS '-mfloat-abi=hard -mfpu=vfp'
  __build:configure:target:build_script
}

function build:jvm:linux-libc:armv7 { ## Builds Linux Libc armv7 for JVM
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="armv7"
  local openssl_target="linux-armv7"
  # TODO: Fix
  local host="armv7-linux-gnueabi"
  __build:configure:target:init

  __conf:CFLAGS '-mfloat-abi=hard -mfpu=vfp'
  __build:configure:target:build_script
}

function build:jvm:linux-libc:x86 { ## Builds Linux Libc x86 for JVM
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="x86"
  local openssl_target="linux-x86"
  local host="i686"
  __build:configure:target:init

  __conf:CFLAGS '-m32'
  __build:configure:target:build_script
  __exec:docker:run "work" "05nelsonm/kmptor-linux-x86"
}

function build:jvm:linux-libc:x86_64 { ## Builds Linux Libc x86_64 for JVM
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="x86_64"
  local openssl_target="linux-x86_64"
  local host="x86_64"
  __build:configure:target:init

  __conf:CFLAGS '-m64'
  __build:configure:target:build_script
  __exec:docker:run "work" "05nelsonm/kmptor-linux-x86_64"
}

function build:jvm:linux-musl:aarch64 { ## Builds Linux Musl aarch64 for JVM
  local os_name="linux"
  local os_subtype="-musl"
  local os_arch="aarch64"
  local openssl_target="linux-aarch64"
  local host="aarch64"
  __build:configure:target:init

  __build:configure:target:build_script
}

function build:jvm:linux-musl:x86 { ## Builds Linux Musl x86 for JVM
  local os_name="linux"
  local os_subtype="-musl"
  local os_arch="x86"
  local openssl_target="linux-x86"
  # TODO: Fix
  local host="i686"
  __build:configure:target:init

  __conf:CFLAGS '-m32'
  __build:configure:target:build_script
}

function build:jvm:linux-musl:x86_64 { ## Builds Linux Musl x86_64 for JVM
  local os_name="linux"
  local os_subtype="-musl"
  local os_arch="x86_64"
  local openssl_target="linux-x86_64"
  local host="x86_64"
  __build:configure:target:init

  __conf:CFLAGS '-m64'
  __build:configure:target:build_script
}

function build:jvm:macos:aarch64 { ## Builds macOS aarch64 for JVM
  local os_name="macos"
  local os_arch="aarch64"
  local openssl_target="darwin64-arm64-cc"
  # TODO: Fix
  local host="aarch64"
  __build:configure:target:init

  __build:configure:target:build_script
}

function build:jvm:macos:x86_64 { ## Builds macOS x86_64 for JVM
  local os_name="macos"
  local os_arch="x86_64"
  local openssl_target="darwin64-x86_64-cc"
  # TODO: Fix
  local host="x86_64"
  __build:configure:target:init

  __build:configure:target:build_script
}

function build:jvm:mingw:x86 { ## Builds Windows x86 for JVM
  local os_name="mingw"
  local os_arch="x86"
  local openssl_target="mingw"
  local host="i686-w64-mingw32"
  __build:configure:target:init

  __build:configure:target:build_script
}

function build:jvm:mingw:x86_64 { ## Builds Windows x86_64 for JVM
  local os_name="mingw"
  local os_arch="x86_64"
  local openssl_target="mingw64"
  local host="x86_64-w64-mingw32"
  __build:configure:target:init

  __build:configure:target:build_script
}

# TODO: macOS, iOS, tvOS, watchOS frameworks

#function build:framework:ios:x86_64 { ## Builds iOS x86_64 Framework
#  local os_name="ios"
#  local os_arch="x86_64"
#  local is_framework="yes"
#  local openssl_target="ios64-xcrun"
#  local host=""
#  __build:configure:target:init
#
#  __build:configure:target:build_script
#}

function clean { ## Deletes the build directory
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
    Syntax: $0 [task] [option]

    Tasks:
$(
    # function names + comments & colorization
    grep -E '^function .* {.*?## .*$$' "$0" |
    grep -v "^function __" |
    sed -e 's/function //' |
    sort |
    awk 'BEGIN {FS = "{.*?## "}; {printf "        \033[93m%-30s\033[92m %s\033[0m\n", $1, $2}'
)

    Options:
        --dry-run                      Will generate build scripts, but not execute anything

    Environment:
        NUM_CPU=<number>               Number of CPUs to use when executing make. Defaults to 2
                                       Example: $ export NUM_CPU=4; $0 build:all:android

    Example: $0 build:jvm:linux-libc:x86_64 --dry-run
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

# shellcheck disable=SC2016
# shellcheck disable=SC1004
function __build:configure:target:init {
  __require:os_name_arch

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

  unset DIR_BUILD
  unset CONF_CC
  unset CONF_LD
  unset CONF_AR
  unset CONF_AS
  unset CONF_RANLIB
  unset CONF_STRIP
  unset CONF_LDFLAGS
  unset CONF_CFLAGS
  unset CONF_SCRIPT
  unset CONF_LIBEVENT
  unset CONF_OPENSSL
  unset CONF_TOR
  unset CONF_XZ
  unset CONF_ZLIB
  unset CMD_SH

  DIR_BUILD="build/$dir_platform$os_name$os_subtype/$os_arch"
  unset dir_platform

  CONF_SCRIPT='#!/bin/sh
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

# DO NOT MODIFY. THIS IS AN AUTOMATICALLY GENERATED FILE.

export TZ=UTC
export LC_ALL=C
export SOURCE_DATE_EPOCH="1234567890"
set -e
'
  __conf:SCRIPT "readonly TASK=\"$os_name$os_subtype:$os_arch\""
  __conf:SCRIPT '
readonly DIR_SCRIPT=$( cd "$( dirname "$0" )" >/dev/null && pwd )
readonly DIR_EXTERNAL="$(pwd)"

rm -rf "$DIR_SCRIPT/out"
rm -rf "$DIR_SCRIPT/libevent"
rm -rf "$DIR_SCRIPT/openssl"
rm -rf "$DIR_SCRIPT/tor"
rm -rf "$DIR_SCRIPT/xz"
rm -rf "$DIR_SCRIPT/zlib"

mkdir -p "$DIR_SCRIPT/out"
mkdir -p "$DIR_SCRIPT/libevent/logs"
mkdir -p "$DIR_SCRIPT/openssl/logs"
mkdir -p "$DIR_SCRIPT/tor/logs"
mkdir -p "$DIR_SCRIPT/xz/logs"
mkdir -p "$DIR_SCRIPT/zlib/logs"
'

  CONF_CFLAGS='-fno-guess-branch-probability -frandom-seed=0'
  __conf:CFLAGS '-I$DIR_SCRIPT/libevent/include'
  __conf:CFLAGS '-I$DIR_SCRIPT/openssl/include'
  __conf:CFLAGS '-I$DIR_SCRIPT/xz/include'
  __conf:CFLAGS '-I$DIR_SCRIPT/zlib/include'

  CONF_LDFLAGS='-L$DIR_SCRIPT/libevent/lib'
  __conf:LDFLAGS '-L$DIR_SCRIPT/openssl/lib'
  __conf:LDFLAGS '-L$DIR_SCRIPT/xz/lib'
  __conf:LDFLAGS '-L$DIR_SCRIPT/zlib/lib'

  CONF_XZ='./configure --enable-static \
  --disable-doc \
  --disable-lzma-links \
  --disable-lzmadec \
  --disable-lzmainfo \
  --disable-scripts \
  --disable-shared \
  --disable-xz \
  --disable-xzdec'

  CONF_ZLIB='./configure --static'

  CONF_OPENSSL='./Configure no-shared \
  no-comp \
  no-dtls \
  no-err \
  no-psk \
  no-srp \
  no-weak-ssl-ciphers \
  no-camellia \
  no-idea \
  no-md2 \
  no-md4 \
  no-rc2 \
  no-rc4 \
  no-rc5 \
  no-rmd160 \
  no-whirlpool \
  no-ui-console'

  if [ "${os_arch: -2}" = "64" ]; then
    __conf:OPENSSL 'enable-ec_nistp_64_gcc_128'
  fi

  # TODO: maybe?
  # --disable-openssl
  # --disable-doxygen-html
  CONF_LIBEVENT='./configure --enable-static \
  --enable-gcc-hardening \
  --disable-debug-mode \
  --disable-libevent-regress \
  --disable-samples \
  --disable-shared'

  CONF_TOR='./configure --disable-asciidoc \
  --disable-dependency-tracking \
  --disable-html-manual \
  --disable-linker-hardening \
  --disable-manpage \
  --disable-system-torrc \
  --disable-systemd \
  --disable-tool-name-check \
  --enable-gpl \
  --enable-static-libevent \
  --with-libevent-dir="$DIR_SCRIPT/libevent" \
  --enable-lzma \
  --enable-static-openssl \
  --with-openssl-dir="$DIR_SCRIPT/openssl" \
  --enable-static-zlib \
  --with-zlib-dir="$DIR_SCRIPT/zlib" '

  __build:configure:android
  __build:configure:jvm
  __build:configure:framework
  __build:configure:non_framework
}

function __build:configure:android {
  __require:os_name_arch
  if [ "$os_name" != "android" ]; then
      return 0
  fi

  __conf:OPENSSL 'no-asm'
  __conf:OPENSSL '-D__ANDROID_API__=21'
  __conf:TOR '--enable-android'
  # TODO
}

function __build:configure:jvm {
  __require:os_name_arch
  if [ "$os_name" = "android" ] || [ -n "$is_framework" ]; then
      return 0
  fi

  __conf:OPENSSL 'no-asm'
  # TODO
}

function __build:configure:framework {
  __require:os_name_arch
  if [ -z "$is_framework" ]; then
    return 0
  fi

  __conf:CFLAGS '-fembed-bitcode'
  __conf:LDFLAGS '-fembed-bitcode'
  # TODO: https://github.com/iCepa/Tor.framework/tree/pure_pod/Tor
}

function __build:configure:non_framework {
  __require:os_name_arch
  if [ -n "$is_framework" ]; then
    return 0
  fi

  __conf:CFLAGS '-fPIC'
  # TODO: JNI
}

# shellcheck disable=SC2016
# shellcheck disable=SC1004
function __build:configure:target:build_script {
  __require:var_set "$os_name" "os_name"
  __require:var_set "$DIR_BUILD" "DIR_BUILD"
  __require:var_set "$openssl_target" "openssl_target"
  __require:var_set "$host" "host"

  if [ -n "$CONF_CC" ]; then
    __conf:SCRIPT "export CC=\"$CONF_CC\""
  fi
  if [ -n "$CONF_LD" ]; then
    __conf:SCRIPT "export LD=\"$CONF_LD\""
  fi
  if [ -n "$CONF_AR" ]; then
    __conf:SCRIPT "export AR=\"$CONF_AR\""
  fi
  if [ -n "$CONF_AS" ]; then
    __conf:SCRIPT "export AS=\"$CONF_AS\""
  fi
  if [ -n "$CONF_RANLIB" ]; then
    __conf:SCRIPT "export RANLIB=\"$CONF_RANLIB\""
  fi
  if [ -n "$CONF_STRIP" ]; then
    __conf:SCRIPT "export STRIP=\"$CONF_STRIP\""
  fi

  __conf:SCRIPT "export CFLAGS=\"$CONF_CFLAGS\""
  __conf:SCRIPT "export LDFLAGS=\"$CONF_LDFLAGS\""

  __conf:SCRIPT 'export LD_LIBRARY_PATH="$DIR_SCRIPT/libevent/lib:$DIR_SCRIPT/openssl/lib:$DIR_SCRIPT/xz/lib:$DIR_SCRIPT/zlib/lib"'
  __conf:SCRIPT 'export LIBS="-ldl -L$DIR_SCRIPT/libevent/lib -L$DIR_SCRIPT/openssl/lib -L$DIR_SCRIPT/xz/lib -L$DIR_SCRIPT/zlib/lib"'

  __conf:XZ "--host=$host"
  __conf:XZ '--prefix="$DIR_SCRIPT/xz"'
  __conf:XZ 'CFLAGS="$CFLAGS -O3"'

  __conf:ZLIB '--prefix="$DIR_SCRIPT/zlib"'

  __conf:OPENSSL '--release'
  __conf:OPENSSL '--libdir=lib'
  __conf:OPENSSL '--prefix="$DIR_SCRIPT/openssl"'
  __conf:OPENSSL "$openssl_target"
  # TODO: Need to check ./Configure output to see if this is necessary
  __conf:OPENSSL 'PKG_CONFIG_PATH="$DIR_SCRIPT/zlib/lib/pkgconfig"'

  __conf:LIBEVENT '--prefix="$DIR_SCRIPT/libevent"'
  __conf:LIBEVENT 'CFLAGS="$CFLAGS -O3"'
  __conf:LIBEVENT 'PKG_CONFIG_PATH="$DIR_SCRIPT/openssl/lib/pkgconfig"'

  __conf:TOR "--host=$host"
  __conf:TOR '--prefix="$DIR_SCRIPT/tor"'
  __conf:TOR 'CFLAGS="$CFLAGS -O3"'

  # xz
  __conf:SCRIPT '
echo "
    Building lzma for $TASK'
  __conf:SCRIPT "    LOGS >> $DIR_BUILD/xz/logs"
  __conf:SCRIPT '"
cd "$DIR_EXTERNAL/xz"
./autogen.sh > "$DIR_SCRIPT/xz/logs/autogen.log" 2> "$DIR_SCRIPT/xz/logs/autogen.err"'
  __conf:SCRIPT "$CONF_XZ > \"\$DIR_SCRIPT/xz/logs/configure.log\" 2> \"\$DIR_SCRIPT/xz/logs/configure.err\"
make clean > /dev/null
make -j$NUM_CPU > \"\$DIR_SCRIPT/xz/logs/make.log\" 2> \"\$DIR_SCRIPT/xz/logs/make.err\"
make install > /dev/null"

  # zlib
  __conf:SCRIPT '
echo "
    Building zlib for $TASK'
  __conf:SCRIPT "    LOGS >> $DIR_BUILD/zlib/logs"
  __conf:SCRIPT '"
cd "$DIR_EXTERNAL/zlib"'
  __conf:SCRIPT "$CONF_ZLIB > \"\$DIR_SCRIPT/zlib/logs/configure.log\" 2> \"\$DIR_SCRIPT/zlib/logs/configure.err\"
make clean > /dev/null
make -j$NUM_CPU > \"\$DIR_SCRIPT/zlib/logs/make.log\" 2> \"\$DIR_SCRIPT/zlib/logs/make.err\"
make install > /dev/null"

  # openssl
  __conf:SCRIPT '
echo "
    Building openssl for $TASK'
  __conf:SCRIPT "    LOGS >> $DIR_BUILD/openssl/logs"
  __conf:SCRIPT '"
cd "$DIR_EXTERNAL/openssl"'
  __conf:SCRIPT "$CONF_OPENSSL > \"\$DIR_SCRIPT/openssl/logs/configure.log\" 2> \"\$DIR_SCRIPT/openssl/logs/configure.err\"
make clean > /dev/null
make -j$NUM_CPU > \"\$DIR_SCRIPT/openssl/logs/make.log\" 2> \"\$DIR_SCRIPT/openssl/logs/make.err\"
make install_sw > /dev/null"

  # libevent
  __conf:SCRIPT '
echo "
    Building libevent for $TASK'
  __conf:SCRIPT "    LOGS >> $DIR_BUILD/libevent/logs"
  __conf:SCRIPT '"
cd "$DIR_EXTERNAL/libevent"
./autogen.sh > "$DIR_SCRIPT/libevent/logs/autogen.log" 2> "$DIR_SCRIPT/libevent/logs/autogen.err"'
  __conf:SCRIPT "$CONF_LIBEVENT > \"\$DIR_SCRIPT/libevent/logs/configure.log\" 2> \"\$DIR_SCRIPT/libevent/logs/configure.err\"
make clean > /dev/null
make -j$NUM_CPU > \"\$DIR_SCRIPT/libevent/logs/make.log\" 2> \"\$DIR_SCRIPT/libevent/logs/make.err\"
make install > /dev/null"

  # tor
  __conf:SCRIPT '
echo "
    Building tor for $TASK'
  __conf:SCRIPT "    LOGS >> $DIR_BUILD/tor/logs"
  __conf:SCRIPT '"
export LZMA_CFLAGS="-I$DIR_SCRIPT/xz/include"
export LZMA_LIBS="$DIR_SCRIPT/xz/lib/liblzma.a"
cd "$DIR_EXTERNAL/tor"
./autogen.sh > "$DIR_SCRIPT/tor/logs/autogen.log" 2> "$DIR_SCRIPT/tor/logs/autogen.err"'
  __conf:SCRIPT "$CONF_TOR > \"\$DIR_SCRIPT/tor/logs/configure.log\" 2> \"\$DIR_SCRIPT/tor/logs/configure.err\"
make clean > /dev/null 2>&1
make -j$NUM_CPU > \"\$DIR_SCRIPT/tor/logs/make.log\" 2> \"\$DIR_SCRIPT/tor/logs/make.err\"
make install > /dev/null 2>&1
"

  # out
  if [ -z "$is_framework" ]; then
    case "$os_name" in
      "android"|"linux"|"freebsd")
        __conf:SCRIPT 'install -s "$DIR_SCRIPT/tor/bin/tor" "$DIR_SCRIPT/out/libkmptor.so"'
        ;;
      "macos")
        # TODO
        ;;
      "mingw")
        __conf:SCRIPT 'install -s "$DIR_SCRIPT/tor/bin/tor.exe" "$DIR_SCRIPT/out/kmptor.dll"'
        ;;
      *)
        __error "Unknown os_name >> $os_name"
        ;;
    esac
  # else
    # TODO
  fi

  mkdir -p "$DIR_BUILD"
  echo "$CONF_SCRIPT" > "$DIR_BUILD/build.sh"
  chmod +x "$DIR_BUILD/build.sh"
  CMD_SH="./$DIR_BUILD/build.sh"
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

function __conf:SCRIPT {
  if [ -z "$1" ]; then
    return 0
  fi

  CONF_SCRIPT+="
$1"
}

function __conf:CC {
  if [ -z "$1" ]; then
    return 0
  fi

  CONF_CC="$1"
}

function __conf:LD {
  if [ -z "$1" ]; then
    return 0
  fi

  CONF_LD="$1"
}

function __conf:AR {
  if [ -z "$1" ]; then
    return 0
  fi

  CONF_AR="$1"
}

function __conf:AS {
  if [ -z "$1" ]; then
    return 0
  fi

  CONF_AS="$1"
}

function __conf:RANLIB {
  if [ -z "$1" ]; then
    return 0
  fi

  CONF_RANLIB="$1"
}

function __conf:STRIP {
  if [ -z "$1" ]; then
    return 0
  fi

  CONF_STRIP="$1"
}

function __conf:CFLAGS {
  if [ -z "$1" ]; then
    return 0
  fi

  CONF_CFLAGS+=" $1"
}

function __conf:LDFLAGS {
  if [ -z "$1" ]; then
    return 0
  fi

  CONF_LDFLAGS+=" $1"
}

function __conf:LIBEVENT {
  if [ -z "$1" ]; then
    return 0
  fi

  CONF_LIBEVENT+=" \\
  $1"
}

function __conf:OPENSSL {
  if [ -z "$1" ]; then
    return 0
  fi

  CONF_OPENSSL+=" \\
  $1"
}

function __conf:TOR {
  if [ -z "$1" ]; then
    return 0
  fi

  CONF_TOR+=" \\
  $1"
}

function __conf:XZ {
  if [ -z "$1" ]; then
    return 0
  fi

  CONF_XZ+=" \\
  $1"
}

function __conf:ZLIB   {
  if [ -z "$1" ]; then
    return 0
  fi

  CONF_ZLIB+=" \\
  $1"
}

function __exec:sh {
  if $DRY_RUN; then
    return 0
  fi

  sh -c "$CMD_SH"
}

function __exec:docker:run {
  if $DRY_RUN; then
    return 0
  fi

  __require:var_set "$1" "image work directory name"
  __require:var_set "$2" "image"

  case "$2" in
    "05nelsonm/kmptor-linux-x86")
      ${DOCKER} build -f "$DIR_SCRIPT/docker/Dockerfile.linux-x86" -t "$2" .
      ;;
    "05nelsonm/kmptor-linux-x86_64")
      ${DOCKER} build -f "$DIR_SCRIPT/docker/Dockerfile.linux-x86_64" -t "$2" .
      ;;
  esac

  ${DOCKER} run --rm -u "$U_ID:$G_ID" -v "$DIR_SCRIPT:/$1" "$2" sh -c "$CMD_SH"
}

#function __exec:docker:cross {
#
#}

function __require:cmd {
  if [ -f "$1" ]; then
    return 0
  fi

  __error "$2 is required to run this script"
}

function __require:os_name_arch {
  __require:var_set "$os_name" "os_name"
  __require:var_set "$os_arch" "os_arch"
}

function __require:var_set {
  __require:not_empty "$1" "$2 must be set"
}

function __require:not_empty {
  if [ -n "$1" ]; then
    return 0
  fi

  __error "$2"
}

function __require:no_build_lock {
  if [ ! -f "$FILE_BUILD_LOCK" ]; then
    return 0
  fi

  # Don't use __error here because it checks DRY_RUN
  echo 1>&2 "
    ERROR: Another build is in progress

    If this is not the case, delete the following file and re-run the task
    $FILE_BUILD_LOCK
  "
  exit 3
}

function __error {
  echo 1>&2 "
    ERROR: $1
  "
  if $DRY_RUN; then return 0; fi
  exit 3
}

function __init {
  # Ensure always start in the external directory
  cd "$DIR_SCRIPT"

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

# Run
if [ -z "$1" ] || [ "$1" = "help" ] || echo "$1" | grep -q "^__"; then
  help
elif ! grep -qE "^function $1 {" "$0"; then
  echo 1>&2 "
    ERROR: Unknown task '$1'
  "
  help
else
  __init "$1"
  TIMEFORMAT="
    Task '$1' completed in %3lR
  "
  time ${1}
fi
