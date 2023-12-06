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
package io.matthewnelson.kmp.tor.binary.core.internal

import io.matthewnelson.kmp.tor.binary.core.*
import io.matthewnelson.kmp.tor.binary.core.PROJECT_DIR_PATH
import io.matthewnelson.kmp.tor.binary.core.api.FileNotFoundException
import okio.ByteString.Companion.toByteString
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.random.Random
import kotlin.test.*

@OptIn(InternalKmpTorBinaryApi::class)
class FileSystemUnitTest {

    @Test
    fun givenFile_whenReadBytes_thenIsAsExpected() {
        // Using a large-ish file to ensure all bytes are read
        val torPath = PROJECT_DIR_PATH.toPath()
            .parent!!
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

        // assertContentEquals cannot handle larger files, so.
        assertEquals(expected.size, actual.size)
        for (i in expected.indices) {
            assertEquals(expected[i], actual[i], "Byte mismatch at index[$i] of [${expected.lastIndex}]")
        }
    }

    @Test
    fun givenFile_whenReadUtf8_thenIsAsExpected() {
        val expected = filesystem().read(TEST_OS_RELEASE_NOT_MUSL) { readUtf8() }
        val actual = fs_readFileUtf8(TEST_OS_RELEASE_NOT_MUSL.toString())
        assertEquals(expected, actual)
    }

    @Test
    fun givenNonExistentFile_whenRead_thenThrowsFileNotFoundException() {
        val doesNotExist = PROJECT_DIR_PATH.toPath().resolve("does_not_exist.txt").toString()

        try {
            fs_readFileBytes(doesNotExist)
            fail()
        } catch (_: FileNotFoundException) {
            // pass
        }

        try {
            fs_readFileUtf8(doesNotExist)
            fail()
        } catch (_: FileNotFoundException) {
            // pass
        }
    }

    @Test
    fun givenFile_whenExists_thenExistsReturnsTrue() {
        assertTrue(fs_exists(PROJECT_DIR_PATH))
    }

    @Test
    fun givenFile_whenDoesNotExist_thenExistsReturnsFalse() {
        assertFalse(fs_exists(PROJECT_DIR_PATH.toPath().resolve("does_not_exist.txt").toString()))
    }

    @Test
    fun givenMkdir_whenAlreadyExists_thenReturnsFalse() {
        assertFalse(fs_mkdir(PROJECT_DIR_PATH))
    }

    @Test
    fun givenMkdir_whenDoesNotExist_thenReturnsTrue() {
        val name = Random.Default.nextBytes(16).toByteString().hex()
        val dir = FileSystem.SYSTEM_TEMPORARY_DIRECTORY.resolve(name)
        assertFalse(filesystem().exists(dir))
        assertTrue(fs_mkdir(dir.toString()))
        filesystem().delete(dir, mustExist = true)
    }
}
