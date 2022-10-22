/*
 * Copyright (c) 2022 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package io.matthewnelson.kmp.tor.binary.extract

import io.matthewnelson.kmp.tor.binary.extract.internal.FILE_NAME_SHA256_TOR
import io.matthewnelson.kmp.tor.binary.extract.internal.mapManifestToDestination
import kotlin.test.AfterTest
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

abstract class BaseExtractorUnitTest {

    protected abstract val extractor: Extractor
    protected abstract val fsSeparator: Char
    protected abstract val tmpDir: String
    protected val tmpBinaryDir: String get() = "$tmpDir${fsSeparator}.kmptor"

    protected abstract fun fileExists(path: String): Boolean
    protected abstract fun fileSize(path: String): Long
    protected abstract fun fileLastModified(path: String): Long
    protected abstract fun fileSha256Sum(path: String): String
    protected abstract fun sha256Sum(bytes: ByteArray): String

    @AfterTest
    open fun deleteTestDir() {
        error("deleteTestDir must be overridden")
    }

    /* Helper for testing cleanExtraction by checking lastModified */
    protected fun threadSleep(loopCount: Int = 100_000) {
        var count = 0
        while (count < loopCount) { count++ }
    }

    @Throws(AssertionError::class)
    protected fun assertBinaryResourceExtractionIsSuccessful(
        resource: TorResource.Binaries,
        testDir: String = tmpBinaryDir,
    ): List<String> {
        val torFilePath = extractor.extract(
            resource = resource,
            destinationDir = testDir,
            cleanExtraction = true,
        )

        println("Tor: $torFilePath")

        val paths = resource.resourceManifest.mapManifestToDestination(testDir) { _, _ -> }

        val sha256SumActual = StringBuilder()

        paths.forEach { path ->
            assertTrue(fileExists(path))
            assertTrue(fileSize(path) > 0)

            sha256SumActual.append(fileSha256Sum(path))
            sha256SumActual.appendLine()
        }

        assertEquals(resource.sha256sum, sha256Sum(sha256SumActual.toString().encodeToByteArray()))

        val sha256Path = testDir + fsSeparator + FILE_NAME_SHA256_TOR
        assertTrue(fileExists(sha256Path))
        assertTrue(fileSize(sha256Path) > 0)

        return paths
    }

    @Throws(AssertionError::class)
    protected fun assertBinaryResourceCleanExtractionFalseNotExtracted(
        resource: TorResource.Binaries,
        extractionDir: String = tmpBinaryDir,
    ) {
        val paths = assertBinaryResourceExtractionIsSuccessful(resource, extractionDir)

        fun lastModified(paths: List<String>): List<Long> {
            return buildList {
                paths.forEach { path ->
                    add(fileLastModified(path))
                }
            }
        }

        val lastModified = lastModified(paths)

        threadSleep()

        extractor.extract(
            resource = resource,
            destinationDir = extractionDir,
            cleanExtraction = false,
        )

        val checkNotModified = lastModified(paths)

        lastModified.forEachIndexed { index, time ->
            assertEquals(time, checkNotModified[index])
        }

        extractor.extract(
            resource = resource,
            destinationDir = extractionDir,
            cleanExtraction = true,
        )

        val checkModified = lastModified(paths)

        lastModified.forEachIndexed { index, time ->
            assertNotEquals(time, checkModified[index])
        }
    }
}
