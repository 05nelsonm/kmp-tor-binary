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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(InternalKmpTorBinaryApi::class)
class OSInfoUnitTest {

    @Test
    fun givenOSNameWindows_whenOSHost_thenIsWindows() {
        // Name only based checks
        assertTrue(OSInfo.INSTANCE.osHost("Windows XP") is OSHost.Windows)
        assertTrue(OSInfo.INSTANCE.osHost("Windows 2000") is OSHost.Windows)
        assertTrue(OSInfo.INSTANCE.osHost("Windows Vista") is OSHost.Windows)
        assertTrue(OSInfo.INSTANCE.osHost("Windows 98") is OSHost.Windows)
        assertTrue(OSInfo.INSTANCE.osHost("Windows 95") is OSHost.Windows)
    }

    @Test
    fun givenOSNameMac_whenOSHost_thenIsMacOS() {
        assertTrue(OSInfo.INSTANCE.osHost("Mac OS") is OSHost.MacOS)
        assertTrue(OSInfo.INSTANCE.osHost("Mac OS X") is OSHost.MacOS)
    }

    @Test
    fun givenOSNameFreeBSD_whenOSHost_thenIsFreeBSD() {
        assertTrue(OSInfo.INSTANCE.osHost("FreeBSD") is OSHost.FreeBSD)
    }

    @Test
    fun givenOSNameLinux_whenUnameOAndroid_thenIsLinuxAndroid() {
        // Termux
        var count = 0
        OSInfo.get(
            process = { commands, _ ->
                if (commands == listOf("uname", "-o")) {
                    count++
                    "Linux Android"
                } else {
                    throw AssertionError("")
                }
            },
            osName = { "Linux" }
        ).let { osInfo ->
            assertTrue(osInfo.osHost is OSHost.Linux.Android)
            // Ensure isAndroidTermux executed uname -o
            assertEquals(1, count)
        }
    }

    @Test
    fun givenOSNameLinux_whenOSName_thenIsLinuxLibc() {
        OSInfo.get(
            pathMapFiles = MAP_FILES_NOT_MUSL.toString(),
            pathOSRelease = OS_RELEASE_NOT_MUSL.toString(),
        ).let { osInfo ->
            assertTrue(osInfo.osHost("Linux") is OSHost.Linux.Libc)
            assertTrue(osInfo.osHost("GNU/Linux") is OSHost.Linux.Libc)
        }
    }

    @Test
    fun givenOSInfo_whenMapFilesMusl_thenIsLinuxMusl() {
        // Linux tests cannot be run on windows host machine
        // because symbolic links are not a thing.
        when (OSInfo.INSTANCE.osHost) {
            is OSHost.Unknown,
            is OSHost.Windows -> return
            else -> { /* run */ }
        }

        // Linux-Musl WITH map_files directory
        OSInfo.get(
            pathMapFiles = TEST_SUPPORT_DIR
                .resolve("msl")
                .resolve("map_files")
                .toString(),
            pathOSRelease = OS_RELEASE_NOT_MUSL.toString(),
            osName = { "Linux" }
        ).let { osInfo ->
            assertTrue(osInfo.osHost is OSHost.Linux.Musl)
        }
    }

    @Test
    fun givenOSNameLinux_whenMapFilesNoExistAndOSReleaseAlpine_thenIsLinuxMusl() {
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
            osName = { "Linux" }
        ).let { osInfo ->
            assertTrue(osInfo.osHost is OSHost.Linux.Musl)
        }
    }

    // TODO: architecture tests

}
