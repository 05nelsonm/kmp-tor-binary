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
package io.matthewnelson.diff.core

import io.matthewnelson.diff.core.internal.InternalDiffApi
import kotlinx.datetime.Clock
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@OptIn(InternalDiffApi::class)
class OptionsUnitTest: DiffCoreTestHelper() {

    @Test
    fun givenDiffOptions_whenStaticTime_thenWritesStaticTime() {
        file1.writeText("asdfasdf")
        file2.writeText("aaaa")
        val diffFile = Diff.create(fs, file1, file2, diffDir, Diff.Options {
            useStaticTime = true
        })
        val header = Diff.readHeader(fs, diffFile)
        assertEquals(Diff.Options.STATIC_TIME.toInstant(), header.createdAtInstant)
    }

    @Test
    fun givenDiffOptions_whenNotStaticTime_thenWritesCurrentTime() {
        file1.writeText("asdfasdf")
        file2.writeText("aaaa")
        val now = Clock.System.now()
        val diffFile = Diff.create(fs, file1, file2, diffDir, Diff.Options {
            useStaticTime = false
        })
        val header = Diff.readHeader(fs, diffFile)
        assertNotEquals(Diff.Options.STATIC_TIME.toInstant(), header.createdAtInstant)
        assertTrue(header.createdAtInstant > now)
    }

    @Test
    fun givenDiffOptions_whenDifferentExtension_thenUses() {
        file1.writeText("asdfasdf")
        file2.writeText("aaaa")
        val expected = ".signature"
        val diffFile = Diff.create(fs, file1, file2, diffDir, Diff.Options {
            diffFileExtensionName(expected)
        })
        assertTrue(diffFile.name.endsWith(expected))
    }
}
