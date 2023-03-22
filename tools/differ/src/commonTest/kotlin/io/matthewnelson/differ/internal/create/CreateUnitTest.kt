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
package io.matthewnelson.differ.internal.create

import io.matthewnelson.differ.internal.*
import io.matthewnelson.differ.internal.OptDiffFileExtName
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertNull

class CreateUnitTest: DifferUnitTest() {

    private val file1 = "/home/user/unsigned/file".toPath()
    private val file2 = "/home/user/signed/file".toPath()
    private val diffDir = "/home/user/diff".toPath()

    @Test
    fun givenCreate_whenFile1And2Exist_thenRuns() {
        writeTestFiles()

        val create = createFrom(runner = EmptyRunner(TestException()))

        // We throw a test exception here from EmptyRunner to signal
        // that execute() passed all the requirements
        assertThrew<TestException> { create.execute() }
    }

    @Test
    fun givenCreate_whenFilesAreTheSame_thenThrowsException() {
        writeTestFiles(file2 = false)

        val create = createFrom(file2 = file1)

        assertThrew<IllegalArgumentException> { create.execute() }
    }

    @Test
    fun givenCreate_whenDiffFileExists_thenThrowsException() {
        writeTestFiles()
        diffDir.resolve(file1.name + OptDiffFileExtName.DEFAULT_EXT).writeText("")

        val create = createFrom()

        assertThrew<IllegalArgumentException> { create.execute() }
    }

    @Test
    fun givenCreate_whenHumanReadableDiffFileExists_thenThrowsException() {
        writeTestFiles()
        diffDir.resolve(file1.name + OptDiffFileExtName.DEFAULT_EXT + ".txt").writeText("")

        val create = createFrom(createReadable = true)

        assertThrew<IllegalArgumentException> { create.execute() }
    }

    @Test
    fun givenCreate_whenCreateReadableFalse_thenIsNull() {
        writeTestFiles()

        val create = createFrom(runner = object : Create.Runner {
            override fun run(
                settings: Subcommand.Settings,
                fs: FileSystem,
                file1: Path,
                file2: Path,
                diffFile: Path,
                hrFile: Path?
            ) {
                assertNull(hrFile)
            }
        })

        create.execute()
    }

    @Test
    fun givenCreate_whenDiffFileExtDoesNotStartWithDot_thenThrowsException() {
        writeTestFiles()

        val create = createFrom(diffFileExtName = "no_dot")

        assertThrew<IllegalArgumentException> { create.execute() }
    }

    @Test
    fun givenCreate_whenDiffFileExtIsOnlyDot_thenThrowsException() {
        writeTestFiles()

        val create = createFrom(diffFileExtName = ".")

        assertThrew<IllegalArgumentException> { create.execute() }
    }

    @Test
    fun givenCreate_whenDiffFileExtHasWitespace_thenThrowsException() {
        writeTestFiles()

        val create = createFrom(diffFileExtName = ". ext")

        assertThrew<IllegalArgumentException> { create.execute() }
    }

    @Test
    fun givenCreate_whenDiffFileExtHasMultipleLines_thenThrowsException() {
        writeTestFiles()

        val create = createFrom(diffFileExtName = """
            .
            ext
        """.trimIndent())

        assertThrew<IllegalArgumentException> { create.execute() }
    }

    private fun writeTestFiles(file1: Boolean = true, file2: Boolean = true) {
        if (file1) this.file1.writeText("file1")
        if (file2) this.file2.writeText("file2")
    }

    private fun createFrom(
        fs: FileSystem = this.fs,
        runner: Create.Runner = EmptyRunner(),
        file1: Path = this.file1,
        file2: Path = this.file2,
        createReadable: Boolean = false,
        diffFileExtName: String = OptDiffFileExtName.DEFAULT_EXT,
        diffDir: Path = this.diffDir,
        settings: Subcommand.Settings = Subcommand.Settings(quiet = true),
    ): Create {
        return Create.from(fs, runner, file1, file2, createReadable, diffFileExtName, diffDir, settings)
    }
}
