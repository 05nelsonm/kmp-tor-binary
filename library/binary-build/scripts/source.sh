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

readonly FIND=$(which find)
readonly GIT=$(which git)
readonly MAKE=$(which make)
readonly TAR=$(which tar)
readonly RCODESIGN=$(which rcodesign)
readonly OSSLSIGNCODE=$(which osslsigncode)

check_find() {
  if [ "$FIND" = "" ]; then
    echo "
    ERROR: find is required to be installed to run this script
    "
    exit 1
  fi
}

check_git() {
  if [ "$GIT" = "" ]; then
    echo "
    ERROR: git is required to be installed to run this script
    "
    exit 1
  fi
}

check_make() {
  if [ "$MAKE" = "" ]; then
    echo "
    ERROR: make is required to be installed to run this script
    "
    exit 1
  fi
}

check_tar() {
  if [ "$TAR" = "" ]; then
    echo "
    ERROR: tar is required to be installed to run this script
    "
    exit 1
  fi
}

check_rcodesign() {
  if [ "$RCODESIGN" = "" ]; then
    echo "
    ERROR: Apple Codesign is required to be installed to run this script
           See https://gregoryszorc.com/docs/apple-codesign/main/apple_codesign_getting_started.html#installing
    "
    exit 1
  fi
}

check_osslsigncode() {
  if [ "$OSSLSIGNCODE" = "" ]; then
    echo "
    ERROR: osslsigncode is required to be installed to run this script
           See https://github.com/mtrojnar/osslsigncode
    "
    exit 1
  fi
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
