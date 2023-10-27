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

# TODO: Move to arg parser
readonly DRY_RUN=$(if [ "$2" = "--dry-run" ]; then echo "true"; else echo "false"; fi)

readonly DIR_TASK=$( cd "$( dirname "$0" )" >/dev/null && pwd )
readonly FILE_BUILD_LOCK="$DIR_TASK/build/.lock"

# Programs
readonly DOCKER=$(which docker)
readonly GIT=$(which git)
readonly XCRUN=$(which xcrun)

# Docker
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
  __build:configure:target:init
  __exec:docker:run
}

function build:android:arm64 { ## Builds Android arm64-v8a
  local os_name="android"
  local os_arch="arm64-v8a"
  local openssl_target="android-arm64"
  __build:configure:target:init
  __exec:docker:run
}

function build:android:x86 { ## Builds Android x86
  local os_name="android"
  local os_arch="x86"
  local openssl_target="android-x86"
  __build:configure:target:init
  __exec:docker:run
}

function build:android:x86_64 { ## Builds Android x86_64
  local os_name="android"
  local os_arch="x86_64"
  local openssl_target="android-x86_64"
  __build:configure:target:init
  __exec:docker:run
}

function build:jvm:freebsd:aarch64 { ## Builds FreeBSD aarch64 for JVM
  local os_name="freebsd"
  local os_arch="aarch64"
  local openssl_target="BSD-aarch64"
  __build:configure:target:init
  # TODO __exec:docker:run
}

function build:jvm:freebsd:x86 { ## Builds FreeBSD x86 for JVM
  local os_name="freebsd"
  local os_arch="x86"
  local openssl_target="BSD-x86"
  __build:configure:target:init
  # TODO __exec:docker:run
}

function build:jvm:freebsd:x86_64 { ## Builds FreeBSD x86_64 for JVM
  local os_name="freebsd"
  local os_arch="x86_64"
  local openssl_target="BSD-x86_64"
  __build:configure:target:init
  # TODO __exec:docker:run
}

function build:jvm:linux-libc:aarch64 { ## Builds Linux Libc aarch64 for JVM
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="aarch64"
  local openssl_target="linux-aarch64"
  __build:configure:target:init
  __conf:CFLAGS '-march=armv8-a'
  __exec:docker:run
}

function build:jvm:linux-libc:armv7 { ## Builds Linux Libc armv7 for JVM
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="armv7"
  local openssl_target="linux-armv4"
  __build:configure:target:init
  __conf:CFLAGS '-march=armv7-a -mfloat-abi=hard -mfpu=vfp'
  __exec:docker:run
}

function build:jvm:linux-libc:x86 { ## Builds Linux Libc x86 for JVM
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="x86"
  local openssl_target="linux-x86"
  __build:configure:target:init
  __conf:CFLAGS '-m32'
  __conf:LDFLAGS '-m32'
  __exec:docker:run
}

function build:jvm:linux-libc:x86_64 { ## Builds Linux Libc x86_64 for JVM
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="x86_64"
  local openssl_target="linux-x86_64"
  __build:configure:target:init
  __exec:docker:run
}

function build:jvm:linux-musl:aarch64 { ## Builds Linux Musl aarch64 for JVM
  local os_name="linux"
  local os_subtype="-musl"
  local os_arch="aarch64"
  local openssl_target="linux-aarch64"
  __build:configure:target:init
  # TODO __exec:docker:run
}

function build:jvm:linux-musl:x86 { ## Builds Linux Musl x86 for JVM
  local os_name="linux"
  local os_subtype="-musl"
  local os_arch="x86"
  local openssl_target="linux-x86"
  __build:configure:target:init
  __conf:CFLAGS '-m32'
  __conf:LDFLAGS '-m32'
  # TODO __exec:docker:run
}

function build:jvm:linux-musl:x86_64 { ## Builds Linux Musl x86_64 for JVM
  local os_name="linux"
  local os_subtype="-musl"
  local os_arch="x86_64"
  local openssl_target="linux-x86_64"
  __build:configure:target:init
  # TODO __exec:docker:run
}

function build:jvm:macos:aarch64 { ## Builds macOS aarch64 for JVM
  local os_name="macos"
  local os_arch="aarch64"
  local openssl_target="darwin64-arm64-cc"
  __build:configure:target:init
  # TODO __exec:docker:run
}

function build:jvm:macos:x86_64 { ## Builds macOS x86_64 for JVM
  local os_name="macos"
  local os_arch="x86_64"
  local openssl_target="darwin64-x86_64-cc"
  __build:configure:target:init
  # TODO __exec:docker:run
}

function build:jvm:mingw:x86 { ## Builds Windows x86 for JVM
  local os_name="mingw"
  local os_arch="x86"
  local openssl_target="mingw"
  __build:configure:target:init
  __conf:CFLAGS '-m32'
  __conf:LDFLAGS '-m32 -Wl,--no-seh'
  __exec:docker:run
}

function build:jvm:mingw:x86_64 { ## Builds Windows x86_64 for JVM
  local os_name="mingw"
  local os_arch="x86_64"
  local openssl_target="mingw64"
  __build:configure:target:init
  __exec:docker:run
}

# TODO: macOS, iOS, tvOS, watchOS frameworks

#function build:framework:ios:x86_64 { ## Builds iOS x86_64 Framework
#  local os_name="ios"
#  local os_arch="x86_64"
#  local is_framework="yes"
#  local openssl_target="ios64-xcrun"
#  __build:configure:target:init
#}

function clean { ## Deletes the build directory
  __require:no_build_lock
  rm -rf "$DIR_TASK/build"
}

function help { ## THIS MENU
  # shellcheck disable=SC2154
  echo "
    $0
    Copyright (C) 2023 Matthew Nelson

    Build tor binaries

    Location: $DIR_TASK
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
        --dry-run                      Will generate build scripts, but not execute anything.

    Example: $0 build:all:jvm --dry-run
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
  __require:var_set "$os_name" "os_name"
  __require:var_set "$os_arch" "os_arch"
  __require:var_set "$openssl_target" "openssl_target"

  if [ -n "$is_framework" ]; then
    # TODO: require cross_triple to be set as we're not running in docker
    __require:cmd "$XCRUN" "xcrun (Xcode CLI tool on macOS machine)"

    DIR_BUILD="build/framework/$os_name$os_subtype/$os_arch"
    DIR_OUT="build/framework-out/$os_name$os_subtype/$os_arch"
  else
    __require:cmd "$DOCKER" "docker"
    __require:var_set "$U_ID" "U_ID"
    __require:var_set "$G_ID" "G_ID"

    if [ "$os_name" = "android" ]; then
      DIR_BUILD="build/$os_name/$os_arch"
      DIR_OUT="build/$os_name-out/$os_arch"
    else
      DIR_BUILD="build/jvm/$os_name$os_subtype/$os_arch"
      DIR_OUT="build/jvm-out/$os_name$os_subtype/$os_arch"
    fi
  fi

  unset CONF_CC
  unset CONF_LD
  unset CONF_AR
  unset CONF_AS
  unset CONF_RANLIB
  unset CONF_STRIP
  unset CONF_CFLAGS
  unset CONF_LDFLAGS
  unset CONF_SCRIPT
  unset CONF_LIBEVENT
  unset CONF_OPENSSL
  unset CONF_TOR
  unset CONF_XZ
  unset CONF_ZLIB

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

export LANG=C.UTF-8
export LC_ALL=C
export SOURCE_DATE_EPOCH="1234567890"
export TZ=UTC
set -e

if [ -z "$CROSS_TRIPLE" ]; then
  echo 1>&2 "
    CROSS_TRIPLE environment variable must be set.
    Are you not using task.sh?
  "
  exit 3
fi
'
  __conf:SCRIPT "readonly TASK_TARGET=\"$os_name$os_subtype:$os_arch\""
  __conf:SCRIPT '
readonly DIR_SCRIPT=$( cd "$( dirname "$0" )" >/dev/null && pwd )
readonly DIR_EXTERNAL="$(pwd)"'
  __conf:SCRIPT "readonly DIR_OUT=\"\$DIR_EXTERNAL/$DIR_OUT\""
  __conf:SCRIPT 'readonly DIR_TMP="$(mktemp -d)"'
  __conf:SCRIPT "trap 'rm -rf \$DIR_TMP' EXIT"
  __conf:SCRIPT '
readonly NUM_JOBS="$(nproc)"

rm -rf "$DIR_OUT"
rm -rf "$DIR_SCRIPT/libevent"
rm -rf "$DIR_SCRIPT/openssl"
rm -rf "$DIR_SCRIPT/tor"
rm -rf "$DIR_SCRIPT/xz"
rm -rf "$DIR_SCRIPT/zlib"

mkdir -p "$DIR_SCRIPT/libevent/logs"
mkdir -p "$DIR_SCRIPT/openssl/logs"
mkdir -p "$DIR_SCRIPT/tor/logs"
mkdir -p "$DIR_SCRIPT/xz/logs"
mkdir -p "$DIR_SCRIPT/zlib/logs"

export PKG_CONFIG_PATH="$DIR_SCRIPT/libevent/lib/pkgconfig:$DIR_SCRIPT/openssl/lib/pkgconfig:$DIR_SCRIPT/xz/lib/pkgconfig:$DIR_SCRIPT/zlib/lib/pkgconfig"
'

  # CFLAGS
  __conf:CFLAGS '-I$DIR_SCRIPT/libevent/include'
  __conf:CFLAGS '-I$DIR_SCRIPT/openssl/include'
  __conf:CFLAGS '-I$DIR_SCRIPT/xz/include'
  __conf:CFLAGS '-I$DIR_SCRIPT/zlib/include'
  __conf:CFLAGS '-fno-guess-branch-probability'
  __conf:CFLAGS '-frandom-seed=0'
  __conf:CFLAGS '-fvisibility=hidden'
  if [ "$os_name" = "mingw" ]; then
    __conf:CFLAGS '-fno-strict-overflow'
    __conf:CFLAGS '-fstack-protector-strong'
  else
    __conf:CFLAGS '-fPIC'
  fi
  if [ -n "$is_framework" ]; then
    __conf:CFLAGS '-fembed-bitcode'
  fi

  # LDFLAGS
  __conf:LDFLAGS '-L$DIR_SCRIPT/libevent/lib'
  __conf:LDFLAGS '-L$DIR_SCRIPT/openssl/lib'
  __conf:LDFLAGS '-L$DIR_SCRIPT/xz/lib'
  __conf:LDFLAGS '-L$DIR_SCRIPT/zlib/lib'
  if [ "$os_name" = "mingw" ]; then
    __conf:LDFLAGS '-Wl,--no-insert-timestamp'
    __conf:LDFLAGS '-Wl,--subsystem,windows'
    __conf:LDFLAGS '-static-libgcc'
  fi
  if [ -n "$is_framework" ]; then
    __conf:LDFLAGS '-fembed-bitcode'
  fi

  # LZMA
  CONF_XZ='./configure --enable-static \
  --disable-doc \
  --disable-lzma-links \
  --disable-lzmadec \
  --disable-lzmainfo \
  --disable-scripts \
  --disable-shared \
  --disable-xz \
  --disable-xzdec \
  --host="$CROSS_TRIPLE" \
  --prefix="$DIR_SCRIPT/xz" \
  CFLAGS="$CFLAGS -O3"'

  # ZLIB
  CONF_ZLIB='./configure --static \
  --prefix="$DIR_SCRIPT/zlib"'

  # OPENSSL
  CONF_OPENSSL='./Configure no-shared \
  no-asm \
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
  no-ui-console \
  --release \
  --libdir=lib \
  --with-zlib-lib="$DIR_SCRIPT/zlib/lib/libz.a" \
  --with-zlib-include="$DIR_SCRIPT/zlib/include" \
  --prefix="$DIR_SCRIPT/openssl"'

  if [ "${os_arch: -2}" = "64" ]; then
    __conf:OPENSSL 'enable-ec_nistp_64_gcc_128'
  fi
  if [ "$os_name" = "android" ]; then
    __conf:OPENSSL '-D__ANDROID_API__=21'
  fi
  if [ "$os_name" = "mingw" ]; then
    __conf:OPENSSL '-static'
  fi
  __conf:OPENSSL "$openssl_target"

  # LIBEVENT
  CONF_LIBEVENT='./configure --enable-static \
  --enable-gcc-hardening \
  --disable-debug-mode \
  --disable-libevent-regress \
  --disable-samples \
  --disable-shared \
  --host="$CROSS_TRIPLE" \
  --prefix="$DIR_SCRIPT/libevent" \
  CFLAGS="$CFLAGS -O3"'

  # TOR
  CONF_TOR='./configure --disable-asciidoc \
  --disable-dependency-tracking \
  --disable-html-manual \
  --disable-linker-hardening \
  --disable-manpage \
  --disable-system-torrc \
  --disable-systemd \
  --disable-tool-name-check \
  --enable-gpl \
  --enable-zstd=no \
  --enable-static-libevent \
  --with-libevent-dir="$DIR_SCRIPT/libevent" \
  --enable-lzma \
  --enable-static-openssl \
  --with-openssl-dir="$DIR_SCRIPT/openssl" \
  --enable-static-zlib \
  --with-zlib-dir="$DIR_SCRIPT/zlib" \
  --host="$CROSS_TRIPLE" \
  --prefix="$DIR_SCRIPT/tor" \
  CFLAGS="$CFLAGS -O3"'

  if [ "$os_name" = "android" ]; then
    __conf:TOR '--enable-android'
  fi
}

# shellcheck disable=SC2016
# shellcheck disable=SC1004
function __build:configure:target:build_script {
  __require:var_set "$os_name" "os_name"
  __require:var_set "$DIR_BUILD" "DIR_BUILD"
  __require:var_set "$DIR_OUT" "DIR_OUT"

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
  else
    __conf:SCRIPT 'export RANLIB="$CROSS_TRIPLE-ranlib"'
  fi
  if [ -n "$CONF_STRIP" ]; then
    __conf:SCRIPT "export STRIP=\"$CONF_STRIP\""
  else
    __conf:SCRIPT 'export STRIP="$CROSS_TRIPLE-strip"'
  fi

  __conf:SCRIPT "export CFLAGS=\"$CONF_CFLAGS\""
  __conf:SCRIPT "export LDFLAGS=\"$CONF_LDFLAGS\""

  if [ "$os_name" = "linux" ]; then
    __conf:SCRIPT 'export LD_LIBRARY_PATH="$DIR_SCRIPT/libevent/lib:$DIR_SCRIPT/openssl/lib:$DIR_SCRIPT/xz/lib:$DIR_SCRIPT/zlib/lib:$LD_LIBRARY_PATH"'
    __conf:SCRIPT 'export LIBS="-L$DIR_SCRIPT/libevent/lib -L$DIR_SCRIPT/openssl/lib -L$DIR_SCRIPT/xz/lib -L$DIR_SCRIPT/zlib/lib"'
  fi

  if [ "$os_name" = "mingw" ]; then
    __conf:SCRIPT 'export CHOST="$CROSS_TRIPLE"'
  fi

  # LZMA
  __conf:SCRIPT "
echo \"
    Building lzma for \$TASK_TARGET
    LOGS >> $DIR_BUILD/xz/logs
\""
  __conf:SCRIPT 'cp -R "$DIR_EXTERNAL/xz" "$DIR_TMP"'
  __conf:SCRIPT "cd \"\$DIR_TMP/xz\"
./autogen.sh > \"\$DIR_SCRIPT/xz/logs/autogen.log\" 2> \"\$DIR_SCRIPT/xz/logs/autogen.err\"
$CONF_XZ > \"\$DIR_SCRIPT/xz/logs/configure.log\" 2> \"\$DIR_SCRIPT/xz/logs/configure.err\"
make clean > /dev/null
make -j\"\$NUM_JOBS\" > \"\$DIR_SCRIPT/xz/logs/make.log\" 2> \"\$DIR_SCRIPT/xz/logs/make.err\"
make install > /dev/null"

  # ZLIB
  __conf:SCRIPT "
echo \"
    Building zlib for \$TASK_TARGET
    LOGS >> $DIR_BUILD/zlib/logs
\""
  __conf:SCRIPT 'cp -R "$DIR_EXTERNAL/zlib" "$DIR_TMP"'
  __conf:SCRIPT "cd \"\$DIR_TMP/zlib\"
$CONF_ZLIB > \"\$DIR_SCRIPT/zlib/logs/configure.log\" 2> \"\$DIR_SCRIPT/zlib/logs/configure.err\"
make clean > /dev/null
make -j\"\$NUM_JOBS\" > \"\$DIR_SCRIPT/zlib/logs/make.log\" 2> \"\$DIR_SCRIPT/zlib/logs/make.err\"
make install > /dev/null"

  # OPENSSL
  __conf:SCRIPT "
echo \"
    Building openssl for \$TASK_TARGET
    LOGS >> $DIR_BUILD/openssl/logs
\""
  __conf:SCRIPT 'cp -R "$DIR_EXTERNAL/openssl" "$DIR_TMP"
cd "$DIR_TMP/openssl"'

  if [ "$os_name" = "mingw" ]; then
    __conf:SCRIPT "
# https://github.com/openssl/openssl/issues/14574
# https://github.com/netdata/netdata/pull/15842
sed -i \"s/disable('static', 'pic', 'threads');/disable('static', 'pic');/\" \"Configure\"
"
  fi

  __conf:SCRIPT "$CONF_OPENSSL > \"\$DIR_SCRIPT/openssl/logs/configure.log\" 2> \"\$DIR_SCRIPT/openssl/logs/configure.err\"
perl configdata.pm --dump >> \"\$DIR_SCRIPT/openssl/logs/configure.log\"
make clean > /dev/null
make -j\"\$NUM_JOBS\" > \"\$DIR_SCRIPT/openssl/logs/make.log\" 2> \"\$DIR_SCRIPT/openssl/logs/make.err\"
make install_sw > /dev/null"

  # LIBEVENT
  __conf:SCRIPT "
echo \"
    Building libevent for \$TASK_TARGET
    LOGS >> $DIR_BUILD/libevent/logs
\""
  __conf:SCRIPT 'cp -R "$DIR_EXTERNAL/libevent" "$DIR_TMP"'
  __conf:SCRIPT "cd \"\$DIR_TMP/libevent\"
./autogen.sh > \"\$DIR_SCRIPT/libevent/logs/autogen.log\" 2> \"\$DIR_SCRIPT/libevent/logs/autogen.err\"
$CONF_LIBEVENT > \"\$DIR_SCRIPT/libevent/logs/configure.log\" 2> \"\$DIR_SCRIPT/libevent/logs/configure.err\"
make clean > /dev/null
make -j\"\$NUM_JOBS\" > \"\$DIR_SCRIPT/libevent/logs/make.log\" 2> \"\$DIR_SCRIPT/libevent/logs/make.err\"
make install > /dev/null"

  # TOR
  __conf:SCRIPT "
echo \"
    Building tor for \$TASK_TARGET
    LOGS >> $DIR_BUILD/tor/logs
\""
  __conf:SCRIPT '
# Includes are not enough when using --enable-lzma flag.
# Must specify it here so configure picks it up.
export LZMA_CFLAGS="-I$DIR_SCRIPT/xz/include"
export LZMA_LIBS="$DIR_SCRIPT/xz/lib/liblzma.a"

cp -R "$DIR_EXTERNAL/tor" "$DIR_TMP"
cd "$DIR_TMP/tor"
./autogen.sh > "$DIR_SCRIPT/tor/logs/autogen.log" 2> "$DIR_SCRIPT/tor/logs/autogen.err"'
  __conf:SCRIPT "$CONF_TOR > \"\$DIR_SCRIPT/tor/logs/configure.log\" 2> \"\$DIR_SCRIPT/tor/logs/configure.err\"
make clean > /dev/null 2>&1
make -j\"\$NUM_JOBS\" > \"\$DIR_SCRIPT/tor/logs/make.log\" 2> \"\$DIR_SCRIPT/tor/logs/make.err\"
make install > /dev/null 2>&1
"

  # out
  __conf:SCRIPT 'mkdir -p "$DIR_OUT"'

  if [ -z "$is_framework" ]; then
    local lib_name_pre_strip=
    local lib_name_post_strip=

    case "$os_name" in
      "android"|"linux"|"freebsd")
        lib_name_pre_strip="tor"
        lib_name_post_strip="libkmptor.so"
        ;;
      "macos")
        lib_name_pre_strip="tor"
        lib_name_post_strip="libkmptor.dylib"
        ;;
      "mingw")
        lib_name_pre_strip="tor.exe"
        lib_name_post_strip="kmptor.dll"
        ;;
      *)
        __error "Unknown os_name >> $os_name"
        ;;
    esac

    __conf:SCRIPT "cp \"\$DIR_SCRIPT/tor/bin/$lib_name_pre_strip\" \"\$DIR_OUT/$lib_name_post_strip\""
    __conf:SCRIPT "\${STRIP} -D \"\$DIR_OUT/$lib_name_post_strip\""
    __conf:SCRIPT "echo \"Unstripped: \$(sha256sum \"\$DIR_SCRIPT/tor/bin/$lib_name_pre_strip\")\""
    __conf:SCRIPT "echo \"Stripped:   \$(sha256sum \"\$DIR_OUT/$lib_name_post_strip\")\""
  # else
    # TODO: framework
  fi

  mkdir -p "$DIR_BUILD"
  echo "$CONF_SCRIPT" > "$DIR_BUILD/build.sh"
  chmod +x "$DIR_BUILD/build.sh"
}

function __build:git:apply_patches {
  __require:not_empty "$1" "project name must not be empty"
  local dir_current=
  dir_current="$(pwd)"
  cd "$DIR_TASK/$1"

  local patch_file=
  for patch_file in "$DIR_TASK/patches/$1/"*.patch; do
    if [ "$patch_file" = "$DIR_TASK/patches/$1/*.patch" ]; then
      # no patch files
      continue
    fi

    echo "Applying git patch to $1 >> $patch_file"
    ${GIT} apply "$patch_file"
    sleep 0.25
  done

  cd "$dir_current"
}

function __build:git:clean {
  __require:not_empty "$1" "project name must not be empty"
  local dir_current=
  dir_current="$(pwd)"
  cd "$DIR_TASK/$1"

  ${GIT} clean -X --force --quiet
  cd "$dir_current"
}

function __build:git:stash {
  __require:not_empty "$1" "project name must not be empty"
  local dir_current=
  dir_current="$(pwd)"
  cd "$DIR_TASK/$1"

  ${GIT} add --all

  if [ "$(${GIT} stash)" = "No local changes to save" ]; then
    cd "$dir_current"
    return 0
  fi

  ${GIT} stash drop
  cd "$dir_current"
}

function __conf:SCRIPT {
  if [ -z "$1" ]; then return 0; fi
  CONF_SCRIPT+="
$1"
}

function __conf:CC {
  CONF_CC="$1"
}

function __conf:LD {
  CONF_LD="$1"
}

function __conf:AR {
  CONF_AR="$1"
}

function __conf:AS {
  CONF_AS="$1"
}

function __conf:RANLIB {
  CONF_RANLIB="$1"
}

function __conf:STRIP {
  CONF_STRIP="$1"
}

function __conf:CFLAGS {
  if [ -z "$CONF_CFLAGS" ]; then
    CONF_CFLAGS="$1"
  else
    CONF_CFLAGS+=" $1"
  fi
}

function __conf:LDFLAGS {
  if [ -z "$CONF_LDFLAGS" ]; then
    CONF_LDFLAGS="$1"
  else
    CONF_LDFLAGS+=" $1"
  fi
}

function __conf:LIBEVENT {
  if [ -z "$1" ]; then return 0;fi
  CONF_LIBEVENT+=" \\
  $1"
}

function __conf:OPENSSL {
  if [ -z "$1" ]; then return 0; fi
  CONF_OPENSSL+=" \\
  $1"
}

function __conf:TOR {
  if [ -z "$1" ]; then return 0; fi
  CONF_TOR+=" \\
  $1"
}

function __conf:XZ {
  if [ -z "$1" ]; then return 0; fi
  CONF_XZ+=" \\
  $1"
}

function __conf:ZLIB   {
  if [ -z "$1" ]; then return 0; fi
  CONF_ZLIB+=" \\
  $1"
}

function __exec:docker:run {
  __build:configure:target:build_script
  if $DRY_RUN; then return 0; fi

  # Build linux libc/musl base image if needed
  if [ -n "$os_subtype" ]; then
    __exec:docker:build "linux$os_subtype.base"
  fi

  # Build android base image if needed
  if [ "$os_name" = "android" ]; then
    __exec:docker:build "android.base"
  fi

  # Build linux-libc base images if needed
  if [ "$os_name" = "mingw" ]; then
    __exec:docker:build "linux-libc.base"
  fi

  # Build final container
  local docker_name="$os_name$os_subtype.$os_arch"
  __exec:docker:build "$docker_name"

  trap 'echo "
    SIGINT intercepted... exiting...
"; exit 1' SIGINT

  ${DOCKER} run \
    --rm \
    -u "$U_ID:$G_ID" \
    -v "$DIR_TASK:/work" \
    "05nelsonm/build-env.$docker_name" \
    "./$DIR_BUILD/build.sh"

  local rc=$?
  if [ $rc -eq 0 ]; then
    trap - SIGINT
    return 0
  fi

  __error "
    Something went wrong with the build... Check logs...
  "
}

function __exec:docker:build {
  ${DOCKER} build \
    -f "$DIR_TASK/docker/Dockerfile.$1" \
    -t "05nelsonm/build-env.$1" \
    "$DIR_TASK/docker"
}

function __require:cmd {
  if [ -f "$1" ]; then return 0; fi
  __error "$2 is required to run this script"
}

function __require:var_set {
  __require:not_empty "$1" "$2 must be set"
}

function __require:not_empty {
  if [ -n "$1" ]; then return 0; fi
  __error "$2"
}

function __require:no_build_lock {
  if [ ! -f "$FILE_BUILD_LOCK" ]; then return 0; fi

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
  # Ensure always starting in the external directory
  cd "$DIR_TASK"

  if ! echo "$1" | grep -q "^build"; then return 0; fi

  __require:cmd "$GIT" "git"
  __require:no_build_lock

  ${GIT} submodule update --init

  mkdir -p "build"
  trap '__build:cleanup' EXIT
  echo "$1" > "$FILE_BUILD_LOCK"
  trap 'echo "    There was a build error. Check logs..."' ERR

  __build:git:clean "libevent"
  __build:git:apply_patches "libevent"
  __build:git:clean "openssl"
  __build:git:apply_patches "openssl"
  __build:git:clean "tor"
  __build:git:apply_patches "tor"
  __build:git:clean "xz"
  __build:git:apply_patches "xz"
  __build:git:clean "zlib"
  __build:git:apply_patches "zlib"
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
