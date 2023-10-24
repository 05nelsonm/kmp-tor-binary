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

# Default to 23 if ANDROID_API environment variable is not expressed
readonly ANDROID_API=$(
  if [[ $ANDROID_API =~ ^[0-9]+$ ]]; then
    echo "$ANDROID_API"
  else
    echo "23"
  fi
)

# Default to 25b if ANDROID_NDK_REVISION environment variable is not expressed
readonly ANDROID_NDK_REVISION=$(
  if [ -n "$ANDROID_NDK_REVISION" ]; then
    echo "$ANDROID_NDK_REVISION"
  else
    echo "25b"
  fi
)

function __install_packages {
  apt-get update
  apt-get install --yes \
    "$@"
}

case "$CROSS_TRIPLE" in
  "arm-linux-androideabi")
    __install_packages "qemu-user" "qemu-user-static"
    ARCH="arm"
    ARCH_ABI="armeabi-v7a"
    SYS_PROCESSOR="armv7-a"
    ;;
  "aarch64-linux-android")
    sed -i '/debian-security/d' /etc/apt/sources.list
    dpkg --add-architecture arm64
    __install_packages "qemu-user" "qemu-user-static"
    ARCH="arm64"
    ARCH_ABI="arm64-v8a"
    SYS_PROCESSOR="aarch64"
    ;;
  "i686-linux-android")
    ARCH="x86"
    ARCH_ABI="x86"
    SYS_PROCESSOR="i686"
    ;;
  "x86_64-linux-android")
    ARCH="x86_64"
    ARCH_ABI="x86_64"
    SYS_PROCESSOR="x86_64"
    ;;
  *)
    echo 1>&2 "
    ERROR: Unknown CROSS_TRIPLE environment variable of '$CROSS_TRIPLE'
           options are:
             arm-linux-androideabi
             aarch64-linux-android
             i686-linux-android
             x86_64-linux-android
    "
    exit 1
    ;;
esac

CROSS_ROOT="/usr/$CROSS_TRIPLE"

mkdir -p /build
cd /build

curl -O "https://dl.google.com/android/repository/android-ndk-r$ANDROID_NDK_REVISION-linux.zip"
unzip "./android-ndk-r$ANDROID_NDK_REVISION-linux.zip"
cd "android-ndk-r$ANDROID_NDK_REVISION"

./build/tools/make_standalone_toolchain.py \
  --arch "$ARCH" \
  --api "$ANDROID_API" \
  --stl=libc++ \
  --install-dir="$CROSS_ROOT"
cd /

rm -rf /build

find "$CROSS_ROOT" -exec chmod a+r '{}' \;
find "$CROSS_ROOT" -executable -exec chmod a+x '{}' \;

echo "set(CMAKE_SYSTEM_NAME Android)
set(CMAKE_SYSTEM_VERSION 1)
set(CMAKE_SYSTEM_PROCESSOR $SYS_PROCESSOR)
set(CMAKE_ANDROID_ARCH_ABI $ARCH_ABI)

set(cross_triple \$ENV{CROSS_TRIPLE})
set(CMAKE_ANDROID_STANDALONE_TOOLCHAIN \$ENV{CROSS_ROOT})
set(CMAKE_ANDROID_ARM_MODE ON)
set(CMAKE_ANDROID_ARM_NEON ON)

set(CMAKE_C_COMPILER \$ENV{CC})
set(CMAKE_CXX_COMPILER \$ENV{CXX})
set(CMAKE_Fortran_COMPILER \$ENV{FC})
set(CMAKE_EXE_LINKER_FLAGS \"-llog\")
set(CMAKE_SHARED_LINKER_FLAGS \"-llog\")

set(CMAKE_FIND_ROOT_PATH \$ENV{CROSS_ROOT})
set(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM NEVER)
set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY BOTH)
set(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE BOTH)
set(CMAKE_SYSROOT \$ENV{CROSS_ROOT}/sysroot)
" > "$CROSS_ROOT/Toolchain.cmake"

case "$ARCH" in
  "arm"|"arm64")
    echo "set(CMAKE_CROSSCOMPILING_EMULATOR /usr/bin/qemu-arm)" >> "$CROSS_ROOT/Toolchain.cmake"
    ;;
esac

echo "
export CROSS_TRIPLE=\"$CROSS_TRIPLE\"
export CROSS_ROOT=\"$CROSS_ROOT\"
export ANDROID_NDK=\"$CROSS_ROOT\"
export AS=\"$CROSS_ROOT/bin/llvm-as\"
export AR=\"$CROSS_ROOT/bin/llvm-ar\"
export CC=\"$CROSS_ROOT/bin/clang\"
export CXX=\"$CROSS_ROOT/bin/clang++\"
export LD=\"$CROSS_ROOT/bin/ld\"
export ANDROID_NDK_REVISION=\"$ANDROID_NDK_REVISION\"
export ANDROID_API=\"$ANDROID_API\"
export CMAKE_TOOLCHAIN_FILE=\"$CROSS_ROOT/Toolchain.cmake\"
" > "/dockcross/android_base.env"

cd /
rm -rf "/var/opt/install_android.sh"
