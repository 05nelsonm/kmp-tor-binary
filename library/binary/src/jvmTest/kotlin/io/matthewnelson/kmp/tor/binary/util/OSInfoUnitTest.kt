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
package io.matthewnelson.kmp.tor.binary.util

import io.matthewnelson.kmp.tor.binary.internal.ProcessRunner
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class OSInfoUnitTest {

    private val testSupportDir = File("test_support").absoluteFile
    private val mapFilesNotMusl = testSupportDir
        .resolve("not_msl")
        .resolve("map_files")
    private val osReleaseNotMusl = testSupportDir
        .resolve("not_msl")
        .resolve("os-release")

    @Test
    fun givenOSInfo_whenOSName_thenOSHostIsAsExpected() {
        // Name only based checks
        assertTrue(OSInfo.INSTANCE.osHost("Windows XP") is OSHost.Windows)
        assertTrue(OSInfo.INSTANCE.osHost("Windows 2000") is OSHost.Windows)
        assertTrue(OSInfo.INSTANCE.osHost("Windows Vista") is OSHost.Windows)
        assertTrue(OSInfo.INSTANCE.osHost("Windows 98") is OSHost.Windows)
        assertTrue(OSInfo.INSTANCE.osHost("Windows 95") is OSHost.Windows)

        assertTrue(OSInfo.INSTANCE.osHost("Mac OS") is OSHost.MacOS)
        assertTrue(OSInfo.INSTANCE.osHost("Mac OS X") is OSHost.MacOS)

        assertTrue(OSInfo.INSTANCE.osHost("FreeBSD") is OSHost.Unsupported)

        // Termux
        var count = 0
        OSInfo.get(process = object : ProcessRunner {
            override fun runAndWait(commands: List<String>): String = runAndWait(commands, 1.seconds)
            override fun runAndWait(commands: List<String>, timeout: Duration): String {
                return if (commands == listOf("uname", "-o")) {
                    count++
                    "Linux Android"
                } else {
                    throw AssertionError("")
                }
            }
        }).let { osInfo ->
            assertTrue(osInfo.osHost("asdfasdf") is OSHost.Linux.Android)
            assertEquals(1, count)
        }

        OSInfo.get(
            pathMapFile = mapFilesNotMusl.path,
            pathOSRelease = osReleaseNotMusl.path
        ).let { osInfo ->
            assertTrue(osInfo.osHost("Linux") is OSHost.Linux.Libc)
            assertTrue(osInfo.osHost("GNU/Linux") is OSHost.Linux.Libc)
        }

        // Linux-Musl WITH map_files directory
        OSInfo.get(
            pathMapFile = testSupportDir
                .resolve("msl")
                .resolve("map_files")
                .path,
            pathOSRelease = testSupportDir
                .resolve("not_msl")
                .resolve("os-release")
                .path,
        ).let { osInfo ->
            // Currently unsupported, but should NOT be OSHost.Linux.Libc
            assertTrue(osInfo.osHost("Linux") is OSHost.Unsupported)
        }

        // Linux-Musl w/o map_files directory
        // Will check os-release to see if it's alpine
        OSInfo.get(
            pathMapFile = testSupportDir
                .resolve("msl")
                .resolve("does_not_exist")
                .path,
            pathOSRelease = testSupportDir
                .resolve("msl")
                .resolve("os-release") // alpine linux
                .path,
        ).let { osInfo ->
            // Currently unsupported, but should NOT be OSHost.Linux.Libc
            assertTrue(osInfo.osHost("Linux") is OSHost.Unsupported)
        }
    }

    // TODO: architecture tests
}
