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
package io.matthewnelson.differ.internal.apply

import io.matthewnelson.differ.internal.*
import io.matthewnelson.differ.internal.Subcommand
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import kotlin.test.Test

class ApplyUnitTest: DifferUnitTest() {

    private val file = "/home/user/unsigned/file".toPath()
    private val diff = "/home/user/diff/file".toPath()

    @Test
    fun givenApply_whenBothFilesExistAndNotEmpty_thenRuns() {
        file.writeText("files")
        diff.writeText("diffs")

        val apply = applyFrom(runner = EmptyRunner(TestException()))

        // We throw a test exception here from EmptyRunner to signal
        // that execute() passed all the requirements
        assertThrew<TestException> { apply.execute() }
    }

    @Test
    fun givenApply_whenDiffFileDoesNotExist_thenThrowsException() {
        file.writeText("file")

        val apply = applyFrom()

        assertThrew<IllegalArgumentException> { apply.execute() }
    }

    @Test
    fun givenApply_whenFileDoesNotExist_thenThrowsException() {
        diff.writeText("diff")

        val apply = applyFrom()

        assertThrew<IllegalArgumentException> { apply.execute() }
    }

    @Test
    fun givenApply_whenFilesAreTheSame_thenThrowsException() {
        file.writeText("file")

        val apply = applyFrom()

        assertThrew<IllegalArgumentException> { apply.execute() }
    }

    @Test
    fun givenApply_whenFilePathIsADir_thenThrowsException() {
        fs.createDirectories(file)
        diff.writeText("diff")

        val apply = applyFrom()

        assertThrew<IllegalArgumentException> { apply.execute() }
    }

    private fun applyFrom(
        fs: FileSystem = this.fs,
        runner: Apply.Runner = EmptyRunner(),
        file: Path = this.file,
        diff: Path = this.diff,
        settings: Subcommand.Settings = Subcommand.Settings(quiet = false)
    ): Apply {
        return Apply.from(fs, runner, file, diff, settings)
    }
}
