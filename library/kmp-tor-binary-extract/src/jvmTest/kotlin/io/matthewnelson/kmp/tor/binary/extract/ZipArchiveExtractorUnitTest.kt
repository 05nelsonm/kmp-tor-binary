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

import org.junit.*
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class ZipArchiveExtractorUnitTest {

    companion object {
        const val A_TEXT = "Hello World from a!"
        const val B_TEXT = "Hello World from b!"

        @get:JvmStatic
        @get:ClassRule
        internal val tmpDir: TemporaryFolder = TemporaryFolder()
        private var testCount = 0

        @JvmStatic
        @AfterClass
        fun afterClass() {
            testCount = 0
        }
    }

    private lateinit var testDir: File

    @Before
    fun before() {
        testDir = tmpDir.newFolder()
    }

    @Test
    fun givenZipArchive_whenExtractAll_thenWholeArchiveIsExtracted() {
        val aFile = File(testDir, "a.txt")
        val bFile = File(testDir, "b/b.txt")

        ZipArchiveExtractor.all(
            destinationDir = testDir,
            postExtraction = {
                assertEquals(2, size)
                assertTrue(contains(aFile))
                assertTrue(contains(bFile))
            },
            zipFileStreamProvider = {
                javaClass.getResourceAsStream("/testing/testing.zip")
                    ?: throw AssertionError("Failed to get testing.zip resource stream")
            }
        ).extract()

        assertTrue(aFile.exists())
        assertTrue(bFile.exists())

        val aText = aFile.readText()
        assertEquals(A_TEXT, aText)

        val bText = bFile.readText()
        assertEquals(B_TEXT, bText)
    }

    @Test
    fun givenZipArchive_whenSelectiveExtraction_onlySpecifiedFilesAreExtracted() {
        val expectedFile = File(testDir, "b.txt")

        ZipArchiveExtractor.selective(
            zipFileStreamProvider = {
                javaClass.getResourceAsStream("/testing/testing.zip")
                    ?: throw AssertionError("Failed to get testing.zip resource stream")
            },
            postExtraction = null,
            extractToFile = {
                if (name == "b/b.txt") {
                    expectedFile
                } else {
                    null
                }
            }
        ).extract()

        assertFalse(File(testDir, "a.txt").exists())
        assertTrue(expectedFile.exists())

        val bText = expectedFile.readText()
        assertEquals(B_TEXT, bText)
    }
}
