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

import io.matthewnelson.encoding.base16.Base16
import io.matthewnelson.encoding.core.Encoder.Companion.encodeToString
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.random.Random
import kotlin.test.*

abstract class KmpTorBinaryBaseUnitTest {

    open val tempDir = FileSystem.SYSTEM_TEMPORARY_DIRECTORY
    open val isWindows: Boolean = false
    private val testDir by lazy {
        tempDir.resolve("kmp_tor_test")
    }

    @Test
    open fun givenKmpTorBinaryResources_whenInstalled_thenIsSuccessful() {
        val random = Random.Default.nextBytes(8).encodeToString(Base16)

        // Will check extraction uses mkdirs instead of mkdir (which would fail)
        val workDir = testDir.resolve(random)

        try {
            val paths = KmpTorBinary(workDir.toString()).install()
            println(paths)

            val geoip = paths.geoip.toPath()
            val geoip6 = paths.geoip6.toPath()
            val tor = paths.tor.toPath()

            assertTrue((filesystem().metadata(geoip).size ?: 0) > 0)
            assertTrue((filesystem().metadata(geoip6).size ?: 0) > 0)
            assertTrue((filesystem().metadata(tor).size ?: 0) > 0)

            // Resource files were gzipped. Check to see if the .gz
            // extension was removed.
            assertFalse(geoip.name.endsWith(".gz"))
            assertFalse(geoip6.name.endsWith(".gz"))
            assertFalse(tor.name.endsWith(".gz"))

            if (!isWindows) {
                assertFalse(geoip.canExecute())
                assertFalse(geoip6.canExecute())
                assertTrue(tor.canExecute())
            }
        } finally {
            try {
                filesystem().deleteRecursively(testDir)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }
}
