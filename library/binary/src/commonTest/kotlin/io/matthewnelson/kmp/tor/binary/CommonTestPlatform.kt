/*
 * Copyright (c) 2023 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package io.matthewnelson.kmp.tor.binary

import okio.FileSystem
import okio.Path.Companion.toPath

expect fun filesystem(): FileSystem

val TEST_SUPPORT_DIR by lazy {
    val dir = filesystem()
        .canonicalize(".".toPath())
        .toString()

    if (
        dir.contains("js")
        && dir.contains("packages")
        && dir.contains("kmp-tor-binary-library-binary-test")
    ) {
        dir.toPath()
            .parent  // remove: kmp-tor-binary-library-binary-test
            ?.parent // remove: packages
            ?.parent // remove: js
            ?.parent // remove: build
            ?.resolve("library")
            ?.resolve("binary")!!
    } else {
        dir.toPath()
    }.resolve("test_support")
}

val MAP_FILES_NOT_MUSL by lazy {
    TEST_SUPPORT_DIR
        .resolve("not_msl")
        .resolve("map_files")
}

val OS_RELEASE_NOT_MUSL by lazy {
    TEST_SUPPORT_DIR
        .resolve("not_msl")
        .resolve("os-release")
}
