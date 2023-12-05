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

import io.matthewnelson.kmp.tor.binary.core.internal.fs_readFileBytes
import io.matthewnelson.kmp.tor.binary.core.internal.fs_readFileUtf8
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@OptIn(InternalKmpTorBinaryApi::class)
class ReadFileUnitTest {

    @Test
    fun givenFile_whenReadBytes_thenIsAsExpected() {
        // Using a large-ish file to ensure all bytes are read
        val torPath = PROJECT_DIR_PATH.toPath()
            .resolve("..")
            .resolve("binary")
            .resolve("src")
            .resolve("jvmMain")
            .resolve("resources")
            .resolve("io")
            .resolve("matthewnelson")
            .resolve("kmp")
            .resolve("tor")
            .resolve("binary")
            .resolve("native")
            .resolve("linux-libc")
            .resolve("x86_64")
            .resolve("tor.gz")

        val expected = filesystem().read(torPath) { readByteArray() }
        val actual = fs_readFileBytes(torPath.toString())

        assertContentEquals(expected, actual)
    }

    @Test
    fun givenFile_whenReadUtf8_thenIsAsExpected() {
        val expected = filesystem().read(OS_RELEASE_NOT_MUSL) { readUtf8() }
        val actual = fs_readFileUtf8(OS_RELEASE_NOT_MUSL.toString())
        assertEquals(expected, actual)
    }
}
