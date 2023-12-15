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

# See https://github.com/05nelsonm/build-env
readonly TAG_DOCKER_BUILD_ENV="0.1.0"

# Programs
readonly DOCKER=$(which docker)
readonly GIT=$(which git)
readonly OSSLSIGNCODE=$(which osslsigncode)
readonly RCODESIGN=$(which rcodesign)

# Docker
readonly U_ID=$(id -u)
readonly G_ID=$(id -g)

function build:all:android { ## Builds all Android targets
  build:android:aarch64
  build:android:armv7
  build:android:x86
  build:android:x86_64
}

function build:all:desktop { ## Builds all Linux, macOS, Windows targets
#  build:all:freebsd
  build:all:linux-libc
#  build:all:linux-musl
  build:all:macos
  build:all:mingw
}

#function build:all:freebsd { ## Builds all FreeBSD targets
#  build:freebsd:aarch64
#  build:freebsd:x86
#  build:freebsd:x86_64
#}

function build:all:linux-libc { ## Builds all Linux Libc targets
  build:linux-libc:aarch64
  build:linux-libc:armv7
  build:linux-libc:x86
  build:linux-libc:x86_64
}

#function build:all:linux-musl { ## Builds all Linux Musl targets
#  build:linux-musl:aarch64
#  build:linux-musl:x86
#  build:linux-musl:x86_64
#}

function build:all:macos { ## Builds all macOS targets
  build:macos:aarch64
  build:macos:x86_64
}

function build:all:mingw { ## Builds all Windows targets
  build:mingw:x86
  build:mingw:x86_64
}

function build:android:aarch64 { ## Builds Android arm64-v8a
  local os_name="android"
  local os_arch="aarch64"
  local openssl_target="android-arm64"
  local ndk_abi="arm64-v8a"
  local cc_clang="yes"
  __build:configure:target:init
  __exec:docker:run
}

function build:android:armv7 { ## Builds Android armeabi-v7a
  local os_name="android"
  local os_arch="armv7"
  local openssl_target="android-arm"
  local ndk_abi="armeabi-v7a"
  local cc_clang="yes"
  __build:configure:target:init
  __exec:docker:run
}

function build:android:x86 { ## Builds Android x86
  local os_name="android"
  local os_arch="x86"
  local openssl_target="android-x86"
  local ndk_abi="x86"
  local cc_clang="yes"
  __build:configure:target:init
  __exec:docker:run
}

function build:android:x86_64 { ## Builds Android x86_64
  local os_name="android"
  local os_arch="x86_64"
  local openssl_target="android-x86_64"
  local ndk_abi="x86_64"
  local cc_clang="yes"
  __build:configure:target:init
  __exec:docker:run
}

#function build:freebsd:aarch64 { ## Builds FreeBSD aarch64
#  local os_name="freebsd"
#  local os_arch="aarch64"
#  local openssl_target="BSD-aarch64"
#  __build:configure:target:init
#  # TODO __exec:docker:run
#}

#function build:freebsd:x86 { ## Builds FreeBSD x86
#  local os_name="freebsd"
#  local os_arch="x86"
#  local openssl_target="BSD-x86"
#  __build:configure:target:init
#  # TODO __exec:docker:run
#}

#function build:freebsd:x86_64 { ## Builds FreeBSD x86_64
#  local os_name="freebsd"
#  local os_arch="x86_64"
#  local openssl_target="BSD-x86_64"
#  __build:configure:target:init
#  # TODO __exec:docker:run
#}

function build:linux-libc:aarch64 { ## Builds Linux Libc aarch64
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="aarch64"
  local openssl_target="linux-aarch64"
  __build:configure:target:init
  __conf:CFLAGS '-march=armv8-a'
  __exec:docker:run
}

function build:linux-libc:armv7 { ## Builds Linux Libc armv7
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="armv7"
  local openssl_target="linux-armv4"
  __build:configure:target:init
  __conf:CFLAGS '-march=armv7-a'
  __conf:CFLAGS '-mfloat-abi=hard'
  __conf:CFLAGS '-mfpu=vfp'
  __exec:docker:run
}

function build:linux-libc:x86 { ## Builds Linux Libc x86
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="x86"
  local openssl_target="linux-x86"
  __build:configure:target:init
  __conf:CFLAGS '-m32'
  __conf:LDFLAGS '-m32'
  __exec:docker:run
}

function build:linux-libc:x86_64 { ## Builds Linux Libc x86_64
  local os_name="linux"
  local os_subtype="-libc"
  local os_arch="x86_64"
  local openssl_target="linux-x86_64"
  __build:configure:target:init
  __exec:docker:run
}

#function build:linux-musl:aarch64 { ## Builds Linux Musl aarch64
#  local os_name="linux"
#  local os_subtype="-musl"
#  local os_arch="aarch64"
#  local openssl_target="linux-aarch64"
#  __build:configure:target:init
#  # TODO __exec:docker:run
#}

#function build:linux-musl:x86 { ## Builds Linux Musl x86
#  local os_name="linux"
#  local os_subtype="-musl"
#  local os_arch="x86"
#  local openssl_target="linux-x86"
#  __build:configure:target:init
#  __conf:CFLAGS '-m32'
#  __conf:LDFLAGS '-m32'
#  # TODO __exec:docker:run
#}

#function build:linux-musl:x86_64 { ## Builds Linux Musl x86_64
#  local os_name="linux"
#  local os_subtype="-musl"
#  local os_arch="x86_64"
#  local openssl_target="linux-x86_64"
#  __build:configure:target:init
#  # TODO __exec:docker:run
#}

function build:macos:aarch64 { ## Builds macOS aarch64
  local os_name="macos"
  local os_arch="aarch64"
  local openssl_target="darwin64-arm64-cc"
  local cc_clang="yes"
  __build:configure:target:init
  __exec:docker:run
}

function build:macos:x86_64 { ## Builds macOS x86_64
  local os_name="macos"
  local os_arch="x86_64"
  local openssl_target="darwin64-x86_64-cc"
  local cc_clang="yes"
  __build:configure:target:init
  __exec:docker:run
}

function build:mingw:x86 { ## Builds Windows x86
  local os_name="mingw"
  local os_arch="x86"
  local openssl_target="mingw"
  __build:configure:target:init
  __conf:LDFLAGS '-Wl,--no-seh'
  __exec:docker:run
}

function build:mingw:x86_64 { ## Builds Windows x86_64
  local os_name="mingw"
  local os_arch="x86_64"
  local openssl_target="mingw64"
  __build:configure:target:init
  __exec:docker:run
}

function clean { ## Deletes the build dir
  rm -rf "$DIR_TASK/build"
}

function help { ## THIS MENU
  # shellcheck disable=SC2154
  echo "
    $0
    Copyright (C) 2023 Matthew Nelson

    Tasks for building, codesigning, and packaging tor binaries

    Location: $DIR_TASK
    Syntax: $0 [task] [option] [args]

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
        --dry-run                      Debugging output that does not execute.

    Example: $0 build:all:jvm --dry-run
  "
}

function package { ## Packages build dir output
  DIR_STAGING="$(mktemp -d)"
  trap 'rm -rf "$DIR_STAGING"' SIGINT ERR

  local module="binary"
  __package:geoip "geoip"
  __package:geoip "geoip6"

  __package:android "arm64-v8a"
  __package:android "armeabi-v7a"
  __package:android "x86"
  __package:android "x86_64"

  __package:jvm "linux-android/aarch64" "tor"
  __package:jvm "linux-android/armv7" "tor"
  __package:jvm "linux-android/x86" "tor"
  __package:jvm "linux-android/x86_64" "tor"
  __package:jvm "linux-libc/aarch64" "tor"
  __package:jvm "linux-libc/armv7" "tor"
  __package:jvm "linux-libc/x86" "tor"
  __package:jvm "linux-libc/x86_64" "tor"
  __package:codesigned "macos/aarch64" "tor"
  __package:jvm:codesigned "macos/x86_64" "tor"
  __package:jvm:codesigned "mingw/x86" "tor.exe"
  __package:jvm:codesigned "mingw/x86_64" "tor.exe"

  __package:native "linux-libc/aarch64" "tor" "linuxArm64Main"
  __package:native "linux-libc/x86_64" "tor" "linuxX64Main"
  __package:native:codesigned "mingw/x86_64" "tor.exe" "mingwX64Main"

  # TODO: Use lipo tool to combine darwin libs
#  __package:native:codesigned "macos/aarch64" "tor" "macosArm64Main"
#  __package:native:codesigned "macos/x86_64" "tor" "macosX64Main"

  rm -rf "$DIR_STAGING"
  trap - SIGINT ERR
}

function sign:apple { ## 2 ARGS - [1]: /path/to/key.p12  [2]: /path/to/app/store/connect/api_key.json
  # shellcheck disable=SC2128
  if [ $# -ne 2 ]; then
    __error "Usage: $0 $FUNCNAME /path/to/key.p12 /path/to/app/store/connect/api_key.json"
  fi

  __require:cmd "$RCODESIGN" "rcodesign"

  local os_name="macos"
  __signature:generate:apple "$1" "$2" "aarch64"
  __signature:generate:apple "$1" "$2" "x86_64"
}

function sign:mingw { ## 2 ARGS - [1]: /path/to/file.key [2]: /path/to/cert.cer
  # shellcheck disable=SC2128
  if [ $# -ne 2 ]; then
    __error "Usage: $0 $FUNCNAME /path/to/file.key /path/to/cert.cer"
  fi

  __require:cmd "$OSSLSIGNCODE" "osslsigncode"

  __signature:generate:mingw "$1" "$2" "x86"
  __signature:generate:mingw "$1" "$2" "x86_64"
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
  __require:cmd "$DOCKER" "docker"
  __require:var_set "$U_ID" "U_ID"
  __require:var_set "$G_ID" "G_ID"

  if [ "$os_name" = "android" ]; then
    __require:var_set "$ndk_abi" "ndk_abi"
    __require:var_set "$cc_clang"

    DIR_BUILD="build/stage/$os_name/$ndk_abi"
    DIR_OUT="build/out/$os_name/$ndk_abi"
  else
    DIR_BUILD="build/stage/$os_name$os_subtype/$os_arch"
    DIR_OUT="build/out/$os_name$os_subtype/$os_arch"
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
'

  __conf:SCRIPT "readonly TASK_TARGET=\"$os_name$os_subtype:$os_arch\""
  __conf:SCRIPT '
readonly DIR_SCRIPT=$( cd "$( dirname "$0" )" >/dev/null && pwd )
# Docker container WORKDIR
readonly DIR_EXTERNAL=/work

if [ ! -f "$DIR_EXTERNAL/task.sh" ]; then
  echo 1>&2 "
    DIR_EXTERNAL does not exist
    Are you not using task.sh?
  "
  exit 3
fi

if [ -z "$CROSS_TRIPLE" ]; then
  echo 1>&2 "
    CROSS_TRIPLE environment variable must be set.
    Are you not using task.sh?
  "
  exit 3
fi
'

  __conf:SCRIPT "readonly DIR_OUT=\"\$DIR_EXTERNAL/$DIR_OUT\""
  __conf:SCRIPT 'readonly DIR_TMP="$(mktemp -d)"'
  __conf:SCRIPT "trap 'rm -rf \$DIR_TMP' EXIT"
  __conf:SCRIPT '
readonly NUM_JOBS="$(nproc)"
'

  if [ "$os_name" = "android" ]; then
    __conf:SCRIPT "readonly DIR_OUT_ALT=\"\$DIR_EXTERNAL/build/out/linux-$os_name/$os_arch\""
    __conf:SCRIPT '
rm -rf "$DIR_OUT_ALT"'
  fi

  __conf:SCRIPT 'rm -rf "$DIR_OUT"
rm -rf "$DIR_SCRIPT/libevent"
rm -rf "$DIR_SCRIPT/openssl"
rm -rf "$DIR_SCRIPT/tor"
rm -rf "$DIR_SCRIPT/xz"
rm -rf "$DIR_SCRIPT/zlib"

mkdir -p "$DIR_SCRIPT/zlib/include"
mkdir -p "$DIR_SCRIPT/zlib/lib"
mkdir -p "$DIR_SCRIPT/zlib/logs"

mkdir -p "$DIR_SCRIPT/xz/include"
mkdir -p "$DIR_SCRIPT/xz/lib"
mkdir -p "$DIR_SCRIPT/xz/logs"

mkdir -p "$DIR_SCRIPT/openssl/include"
mkdir -p "$DIR_SCRIPT/openssl/lib"
mkdir -p "$DIR_SCRIPT/openssl/logs"

mkdir -p "$DIR_SCRIPT/libevent/include"
mkdir -p "$DIR_SCRIPT/libevent/lib"
mkdir -p "$DIR_SCRIPT/libevent/logs"

mkdir -p "$DIR_SCRIPT/tor/logs"

export LD_LIBRARY_PATH="$DIR_SCRIPT/libevent/lib:$DIR_SCRIPT/openssl/lib:$DIR_SCRIPT/xz/lib:$DIR_SCRIPT/zlib/lib:$LD_LIBRARY_PATH"
export LIBS="-L$DIR_SCRIPT/libevent/lib -L$DIR_SCRIPT/openssl/lib -L$DIR_SCRIPT/xz/lib -L$DIR_SCRIPT/zlib/lib"
export PKG_CONFIG_PATH="$DIR_SCRIPT/libevent/lib/pkgconfig:$DIR_SCRIPT/openssl/lib/pkgconfig:$DIR_SCRIPT/xz/lib/pkgconfig:$DIR_SCRIPT/zlib/lib/pkgconfig"
'

  # CFLAGS
  __conf:CFLAGS '-I$DIR_SCRIPT/libevent/include'
  __conf:CFLAGS '-I$DIR_SCRIPT/openssl/include'
  __conf:CFLAGS '-I$DIR_SCRIPT/xz/include'
  __conf:CFLAGS '-I$DIR_SCRIPT/zlib/include'
  __conf:CFLAGS '-O3'
  __conf:CFLAGS '-frandom-seed=0'
  __conf:CFLAGS '-fstack-protector-strong'

  if [ -z "$cc_clang" ]; then
    # gcc only
    __conf:CFLAGS '-fno-guess-branch-probability'
  fi
  if [ "$os_name" = "mingw" ]; then
    # In order to utilize the -fstack-protector-strong flag,
    # we also must comiple with -static to ensure libssp-0.dll
    # will not be included in the final product.
    #
    # $ objdump -p build/jvm-out/mingw/<arch>/tor.exe | grep "DLL Name"
    __conf:CFLAGS '-static'
    __conf:CFLAGS '-fno-strict-overflow'
  else
    __conf:CFLAGS '-fPIC'
    __conf:CFLAGS '-fvisibility=hidden'
  fi
  # TODO: openssl configurations for darwin use -bundle which is wrong
  #  need to modify openssl/Configurations/shared-info.pl
  #  for its darwin-shared target the field module_ldflags
#  if [ "$os_name" = "macos" ]; then
#    __conf:CFLAGS '-fembed-bitcode'
#  fi

  # LDFLAGS
  __conf:LDFLAGS '-L$DIR_SCRIPT/libevent/lib'
  __conf:LDFLAGS '-L$DIR_SCRIPT/openssl/lib'
  __conf:LDFLAGS '-L$DIR_SCRIPT/xz/lib'
  __conf:LDFLAGS '-L$DIR_SCRIPT/zlib/lib'

  if [ "$os_name" = "mingw" ]; then
    __conf:LDFLAGS '-Wl,--no-insert-timestamp'
    __conf:LDFLAGS '-static-libgcc'
  fi
#  if [ "$os_name" = "macos" ]; then
#    __conf:LDFLAGS '-fembed-bitcode'
#  fi

  # ZLIB
  CONF_ZLIB='./configure --static \
  --prefix="$DIR_SCRIPT/zlib"'

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
  --prefix="$DIR_SCRIPT/xz"'

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
  --libdir=lib \
  --with-zlib-lib="$DIR_SCRIPT/zlib/lib" \
  --with-zlib-include="$DIR_SCRIPT/zlib/include" \
  --prefix="$DIR_SCRIPT/openssl"'

  if [ "${os_arch: -2}" = "64" ]; then
    __conf:OPENSSL 'enable-ec_nistp_64_gcc_128'
  fi
  if [ "$os_name" = "android" ]; then
    __conf:OPENSSL '-D__ANDROID_API__=21'
  fi
  if [ "$os_name" = "mingw" ]; then
    # Even though -static is declared in CFLAGS, it is declared here
    # because openssl's Configure file is jank.
    __conf:OPENSSL '-static'
  fi
  __conf:OPENSSL "$openssl_target"

  # LIBEVENT
  CONF_LIBEVENT='./configure --enable-static \
  --enable-gcc-hardening \
  --disable-debug-mode \
  --disable-doxygen-html \
  --disable-libevent-regress \
  --disable-samples \
  --disable-shared \
  --host="$CROSS_TRIPLE" \
  --prefix="$DIR_SCRIPT/libevent"'

  # TOR
  CONF_TOR='./configure --disable-asciidoc \
  --disable-html-manual \
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
  --prefix="$DIR_SCRIPT/tor"'

  if [ "$os_name" = "android" ]; then
    __conf:TOR '--enable-android'
  fi
  if [ "$os_name" = "mingw" ]; then
    __conf:TOR '--enable-static-tor'
    # So if tor.exe is clicked on, it opens in console.
    # This is the same behavior as the tor.exe output by
    # tor-browser-build.
    __conf:TOR 'LDFLAGS="$LDFLAGS -Wl,--subsystem,console"'
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
  fi
  if [ -n "$CONF_STRIP" ]; then
    __conf:SCRIPT "export STRIP=\"$CONF_STRIP\""
  fi

  __conf:SCRIPT "export CFLAGS=\"$CONF_CFLAGS\""
  __conf:SCRIPT "export LDFLAGS=\"$CONF_LDFLAGS\""

  if [ "$os_name" = "mingw" ]; then
    __conf:SCRIPT 'export CHOST="$CROSS_TRIPLE"'
  fi

  # ZLIB
  __conf:SCRIPT "
echo \"
    Building zlib for \$TASK_TARGET
    LOGS >> $DIR_BUILD/zlib/logs
\""
  __conf:SCRIPT 'cp -R "$DIR_EXTERNAL/zlib" "$DIR_TMP"'
  __conf:SCRIPT "cd \"\$DIR_TMP/zlib\"
$CONF_ZLIB > \"\$DIR_SCRIPT/zlib/logs/configure.log\" 2> \"\$DIR_SCRIPT/zlib/logs/configure.err\"
cat configure.log >> \"\$DIR_SCRIPT/zlib/logs/configure.log\"
make clean > /dev/null
make -j\"\$NUM_JOBS\" > \"\$DIR_SCRIPT/zlib/logs/make.log\" 2> \"\$DIR_SCRIPT/zlib/logs/make.err\"
make install >> \"\$DIR_SCRIPT/zlib/logs/make.log\" 2>> \"\$DIR_SCRIPT/zlib/logs/make.err\""

  # LZMA
  __conf:SCRIPT "
echo \"
    Building lzma for \$TASK_TARGET
    LOGS >> $DIR_BUILD/xz/logs
\""
  __conf:SCRIPT 'cp -R "$DIR_EXTERNAL/xz" "$DIR_TMP"'
  __conf:SCRIPT "cd \"\$DIR_TMP/xz\"
./autogen.sh --no-po4a \\
  --no-doxygen > \"\$DIR_SCRIPT/xz/logs/autogen.log\" 2> \"\$DIR_SCRIPT/xz/logs/autogen.err\"
$CONF_XZ > \"\$DIR_SCRIPT/xz/logs/configure.log\" 2> \"\$DIR_SCRIPT/xz/logs/configure.err\"
make clean > /dev/null
make -j\"\$NUM_JOBS\" > \"\$DIR_SCRIPT/xz/logs/make.log\" 2> \"\$DIR_SCRIPT/xz/logs/make.err\"
make install >> \"\$DIR_SCRIPT/xz/logs/make.log\" 2>> \"\$DIR_SCRIPT/xz/logs/make.err\""

  # OPENSSL
  __conf:SCRIPT "
echo \"
    Building openssl for \$TASK_TARGET
    LOGS >> $DIR_BUILD/openssl/logs
\""
  __conf:SCRIPT 'cp -R "$DIR_EXTERNAL/openssl" "$DIR_TMP"
cd "$DIR_TMP/openssl"'

  if [ "$os_name" = "mingw" ]; then
    # TODO: Move to patch file
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
make install_sw >> \"\$DIR_SCRIPT/openssl/logs/make.log\" 2>> \"\$DIR_SCRIPT/openssl/logs/make.err\""

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
make install >> \"\$DIR_SCRIPT/libevent/logs/make.log\" 2>> \"\$DIR_SCRIPT/libevent/logs/make.err\""

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
make install >> \"\$DIR_SCRIPT/tor/logs/make.log\" 2>> \"\$DIR_SCRIPT/tor/logs/make.err\"
"

  # out
  __conf:SCRIPT 'mkdir -p "$DIR_OUT"'

  local bin_name=
  local bin_name_out=

  case "$os_name" in
    "android")
      bin_name="tor"
      bin_name_out="libtor.so"
      ;;
    "linux"|"freebsd"|"macos")
      bin_name="tor"
      bin_name_out="tor"
      ;;
    "mingw")
      bin_name="tor.exe"
      # Do not modify the name for Windows. Otherwise it
      # may be flaged by Windows Defender as a virus.
      bin_name_out="tor.exe"
      ;;
    *)
      __error "Unknown os_name >> $os_name"
      ;;
  esac

  __conf:SCRIPT "cp \"\$DIR_SCRIPT/tor/bin/$bin_name\" \"\$DIR_OUT/$bin_name_out\""
  __conf:SCRIPT "\${STRIP} -D \"\$DIR_OUT/$bin_name_out\""

  if [ "$os_name" = "android" ]; then
    __conf:SCRIPT "
mkdir -p \"\$DIR_OUT_ALT\"
cp -a \"\$DIR_OUT/$bin_name_out\" \"\$DIR_OUT_ALT/tor\"
"
  fi

  __conf:SCRIPT "echo \"Unstripped: \$(sha256sum \"\$DIR_SCRIPT/tor/bin/$bin_name\")\""
  __conf:SCRIPT "echo \"Stripped:   \$(sha256sum \"\$DIR_OUT/$bin_name_out\")\""

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
  if [ -z "$1" ]; then return 0; fi
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

  if $DRY_RUN; then
    echo "Build Script >> $DIR_BUILD/build.sh"
    return 0
  fi

  trap 'echo "
    SIGINT intercepted... exiting...
"; exit 1' SIGINT

  # map os_arch to what docker container expects
  local docker_arch=
  case "$os_arch" in
    "armv7") docker_arch="armv7a"  ;;
    *)       docker_arch="$os_arch";;
  esac

  ${DOCKER} run \
    --rm \
    -u "$U_ID:$G_ID" \
    -v "$DIR_TASK:/work" \
    "05nelsonm/build-env.$os_name$os_subtype.$docker_arch:$TAG_DOCKER_BUILD_ENV" \
    "./$DIR_BUILD/build.sh"

  trap - SIGINT
}

function __package:geoip {
  local permissions="664"
  local gzip="yes"

  __package "tor/src/config" "jvmAndroidMain/resources/io/matthewnelson/kmp/tor/binary" "$1"

  local native_resource="io.matthewnelson.kmp.tor.binary.internal"
  __package "tor/src/config" "nativeMain" "$1"
}

function __package:android {
  local permissions="755"
  # no gzip
  __package "build/out/android/$1" "androidMain/jniLibs/$1" "libtor.so"
}

function __package:jvm {
  local permissions="755"
  local gzip="yes"
  __package "build/out/$1" "jvmMain/resources/io/matthewnelson/kmp/tor/binary/native/$1" "$2"
}

function __package:jvm:codesigned {
  local detached_sig="$1"
  __package:jvm "$@"
}

function __package:native {
  local permissions="755"
  local gzip="yes"
  local native_resource="io.matthewnelson.kmp.tor.binary.internal"
  __package "build/out/$1" "$3" "$2"
}

function __package:native:codesigned {
  local detached_sig="$1"
  __package:native "$@"
}

function __package {
  __require:var_set "$1" "Packaging target dir (relative to dir kmp-tor-binary/external)"
  __require:var_set "$2" "Binary module src path (relative to dir kmp-tor-binary/library/binary/src)"
  __require:var_set "$3" "File name"
  __require:var_set "$module" "module"

  __require:var_set "$permissions" "permissions"
  __require:var_set "$DIR_STAGING" "DIR_STAGING"

  if $DRY_RUN; then
    echo "
    Packaging Target:     $1/$3
    Detached Signature:   $detached_sig
    gzip:                 $gzip
    permissions:          $permissions
    NativeResource:       $native_resource
    Module Src Dir:       build/package/$module/src/$2
    "
    return 0
  fi

  if [ ! -f "$DIR_TASK/$1/$3" ]; then
    echo "FileNotFound. SKIPPING >> ARGS - 1[$1] - 2[$2] - 3[$3]"
    return 0
  fi

  cp -a "$DIR_TASK/$1/$3" "$DIR_STAGING"

  if [ -n "$detached_sig" ]; then
    ../tooling diff-cli apply \
      "$DIR_TASK/codesign/$detached_sig/$3.signature" \
      "$DIR_STAGING/$3"
  fi

  local file_ext=""
  if [ -n "$gzip" ]; then
    ../tooling gzip-cli "$DIR_STAGING/$3"
    file_ext=".gz"
  fi

  # Need to apply permissions after tooling
  # because it strips that as the file is atomically
  # moved instead of being modified in place (see Issue #77).
  chmod "$permissions" "$DIR_STAGING/$3$file_ext"

  local dir_module="$DIR_TASK/build/package/$module/src/$2"

  if [ -z "$native_resource" ]; then
    mkdir -p "$dir_module"
    mv -v "$DIR_STAGING/$3$file_ext" "$dir_module"
  else
    ../tooling resource-cli \
      "$native_resource" \
      "$dir_module" \
      "$DIR_STAGING/$3$file_ext"

    # shellcheck disable=SC2115
    rm -rf "$DIR_STAGING/$3$file_ext"
  fi

}

function __signature:generate:apple {
  __require:var_set "$os_name"
  __require:file_exists "$1" "p12 file does not exist"
  __require:file_exists "$2" "App Store Connect api key file does not exist"
  __require:var_set "$3" "arch"

  if [ ! -f "$DIR_TASK/build/out/$os_name/$3/tor" ]; then
    echo "
    tor not found for $os_name:$3. Skipping...
    "
    return 0
  fi

  echo "
    Creating detached signature for build/out/$os_name/$3/tor
  "

  DIR_TMP="$(mktemp -d)"
  trap 'rm -rf "$DIR_TMP"' SIGINT ERR

  # TODO: handle non-macos
  local dir_bundle="$DIR_TMP/KmpTor.app"
  local dir_bundle_macos="$dir_bundle/Contents/MacOS"
  local dir_bundle_libs="$dir_bundle_macos/Tor"
  mkdir -p "$dir_bundle_libs"
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
</plist>' > "$dir_bundle/Contents/Info.plist"

  cp "$DIR_TASK/build/out/$os_name/$3/tor" "$dir_bundle_macos/tor.program"
  cp "$DIR_TASK/build/out/$os_name/$3/tor" "$dir_bundle_libs/tor"

  ${RCODESIGN} sign \
    --p12-file "$1" \
    --code-signature-flags runtime \
    "$dir_bundle"

  echo ""
  sleep 1

  ${RCODESIGN} notary-submit \
    --api-key-path "$2" \
    --staple \
    "$dir_bundle"

  mkdir -p "$DIR_TASK/codesign/$os_name/$3"
  rm -rf "$DIR_TASK/codesign/$os_name/$3/tor.signature"

  echo ""

  ../tooling diff-cli create \
    --diff-ext-name ".signature" \
    "$DIR_TASK/build/out/$os_name/$3/tor" \
    "$dir_bundle_libs/tor" \
    "$DIR_TASK/codesign/$os_name/$3"

  echo ""

  local dir_tmp="$DIR_TMP"
  unset DIR_TMP
  trap - SIGINT ERR
  rm -rf "$dir_tmp"
}

function __signature:generate:mingw {
  __require:file_exists "$1" "key file does not exist"
  __require:file_exists "$2" "cert file does not exist"
  __require:var_set "$3" "arch"

  if [ ! -f "$DIR_TASK/build/out/mingw/$3/tor.exe" ]; then
    echo "
    build/$3/tor.exe not found. Skipping...
    "
    return 0
  fi

  echo "
    Creating detached signature for build/out/mingw/$3/tor.exe
  "

  DIR_TMP="$(mktemp -d)"
  trap 'rm -rf "$DIR_TMP"' SIGINT ERR

  ${OSSLSIGNCODE} sign \
    -key "$1" \
    -certs "$2" \
    -t "http://timestamp.comodoca.com" \
    -in "$DIR_TASK/build/out/mingw/$3/tor.exe" \
    -out "$DIR_TMP/tor.exe"

  mkdir -p "$DIR_TASK/codesign/mingw/$3"
  rm -rf "$DIR_TASK/codesign/mingw/$3/tor.exe.signature"

  echo ""

  ../tooling diff-cli create \
    --diff-ext-name ".signature" \
    "$DIR_TASK/build/out/mingw/$3/tor.exe" \
    "$DIR_TMP/tor.exe" \
    "$DIR_TASK/codesign/mingw/$3"

  echo ""

  local dir_tmp="$DIR_TMP"
  unset DIR_TMP
  trap - SIGINT ERR
  rm -rf "$dir_tmp"
}

function __require:cmd {
  __require:file_exists "$1" "$2 is required to run this script"
}

function __require:file_exists {
  if [ -f "$1" ]; then return 0; fi
  __error "$2"
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
    ERROR: A build is in progress

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

# Run
if [ -z "$1" ] || [ "$1" = "help" ] || echo "$1" | grep -q "^__"; then
  help
elif ! grep -qE "^function $1 {" "$0"; then
  help
  echo 1>&2 "
    ERROR: Unknown task '$1'
  "
else
  __require:no_build_lock

  # Ensure always starting in the external directory
  cd "$DIR_TASK"
  mkdir -p "build"

  if echo "$1" | grep -q "^build"; then
    __require:cmd "$GIT" "git"

    ${GIT} submodule update --init

    __require:no_build_lock
    trap '__build:cleanup' EXIT
    echo "$1" > "$FILE_BUILD_LOCK"

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
  elif echo "$1" | grep -q "^package"; then
    __require:cmd "$GIT" "git"

    ${GIT} submodule update --init "$DIR_TASK/tor"
    __build:git:clean tor

    __require:no_build_lock
    trap 'rm -rf "$FILE_BUILD_LOCK"' EXIT
    echo "$1" > "$FILE_BUILD_LOCK"
  elif echo "$1" | grep -q "^sign"; then
    trap 'rm -rf "$FILE_BUILD_LOCK"' EXIT
    echo "$1" > "$FILE_BUILD_LOCK"
  fi

  TIMEFORMAT="
    Task '$1' completed in %3lR
  "
  time "$@"
fi
