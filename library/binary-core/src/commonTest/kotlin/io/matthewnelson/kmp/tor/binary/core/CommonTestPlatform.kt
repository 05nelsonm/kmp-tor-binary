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
package io.matthewnelson.kmp.tor.binary.core

import okio.ByteString.Companion.toByteString
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.random.Random

expect fun filesystem(): FileSystem

val TEST_SUPPORT_DIR by lazy {
    PROJECT_DIR_PATH
        .toPath()
        .resolve("test_support")
}

val TEST_MAP_FILES_NOT_MUSL by lazy {
    TEST_SUPPORT_DIR
        .resolve("not_msl")
        .resolve("map_files")
}

val TEST_OS_RELEASE_NOT_MUSL by lazy {
    TEST_SUPPORT_DIR
        .resolve("not_msl")
        .resolve("os-release")
}

fun randomName(): String = Random
    .Default
    .nextBytes(16)
    .toByteString()
    .hex()
