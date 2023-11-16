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

import io.matthewnelson.kmp.tor.binary.MAP_FILES_NOT_MUSL
import io.matthewnelson.kmp.tor.binary.OS_RELEASE_NOT_MUSL
import io.matthewnelson.kmp.tor.binary.TEST_SUPPORT_DIR
import io.matthewnelson.kmp.tor.binary.internal.ProcessRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class OSInfoUnitTest {

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

        assertTrue(OSInfo.INSTANCE.osHost("FreeBSD") is OSHost.FreeBSD)

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
            pathMapFiles = MAP_FILES_NOT_MUSL.toString(),
            pathOSRelease = OS_RELEASE_NOT_MUSL.toString(),
        ).let { osInfo ->
            assertTrue(osInfo.osHost("Linux") is OSHost.Linux.Libc)
            assertTrue(osInfo.osHost("GNU/Linux") is OSHost.Linux.Libc)
        }

        // Remaining linux tests cannot be run on windows host machine
        // because symbolic links are not a thing.
        if (OSInfo.INSTANCE.osHost is OSHost.Windows) return

        // Linux-Musl WITH map_files directory
        OSInfo.get(
            pathMapFiles = TEST_SUPPORT_DIR
                .resolve("msl")
                .resolve("map_files")
                .toString(),
            pathOSRelease = OS_RELEASE_NOT_MUSL.toString(),
        ).let { osInfo ->
            assertTrue(osInfo.osHost("Linux") is OSHost.Linux.Musl)
        }

        // Linux-Musl w/o map_files directory
        // Will check os-release to see if it's alpine
        OSInfo.get(
            pathMapFiles = TEST_SUPPORT_DIR
                .resolve("msl")
                .resolve("does_not_exist")
                .toString(),
            pathOSRelease = TEST_SUPPORT_DIR
                .resolve("msl")
                .resolve("os-release") // alpine linux
                .toString(),
        ).let { osInfo ->
            assertTrue(osInfo.osHost("Linux") is OSHost.Linux.Musl)
        }
    }

    // TODO: architecture tests
}
