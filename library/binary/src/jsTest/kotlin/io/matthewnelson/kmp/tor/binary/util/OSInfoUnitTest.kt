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
import kotlin.test.Test
import kotlin.test.assertTrue

class OSInfoUnitTest {

    @Test
    fun givenOSInfo_whenOSName_thenOSHostIsAsExpected() {
        println("OS_HOST: ${OSInfo.INSTANCE.osHost}")
        println("OS_ARCH: ${OSInfo.INSTANCE.osArch}")

        assertTrue(OSInfo.INSTANCE.osHost("win32") is OSHost.Windows)
        assertTrue(OSInfo.INSTANCE.osHost("darwin") is OSHost.MacOS)
        assertTrue(OSInfo.INSTANCE.osHost("freebsd") is OSHost.FreeBSD)
        assertTrue(OSInfo.INSTANCE.osHost("android") is OSHost.Linux.Android)

        OSInfo.get(
            pathMapFiles = MAP_FILES_NOT_MUSL.toString(),
            pathOSRelease = OS_RELEASE_NOT_MUSL.toString()
        ).let { osInfo ->
            assertTrue(osInfo.osHost("linux") is OSHost.Linux.Libc)
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
            assertTrue(osInfo.osHost("linux") is OSHost.Linux.Musl)
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
            // There is no fallback to checking os-release file for alpine
            // on js implementation (maybe someday...). So, it should register
            // as Linux.Libc.
            assertTrue(osInfo.osHost("linux") is OSHost.Linux.Libc)
        }
    }
}
