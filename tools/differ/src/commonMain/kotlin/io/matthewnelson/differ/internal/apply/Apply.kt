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

import io.matthewnelson.differ.internal.Subcommand
import io.matthewnelson.differ.internal.requireFileDoesNotExist
import io.matthewnelson.differ.internal.requireFileExistAndNotEmpty
import okio.Path

internal abstract class Apply: Subcommand(
    name = NAME_CMD,
    description = """
        Applies a diff to it's associated file and outputs
        the new file to specified path.
    """,
    additionalIndent = 5,
) {

    protected abstract val fileArg: Path
    protected abstract val diffFileArg: Path
    protected abstract val outFileArg: Path

    final override fun execute() {
        fileArg.requireFileExistAndNotEmpty(NAME_FILE)
        diffFileArg.requireFileExistAndNotEmpty(NAME_DIFF_FILE)
        require(fileArg != diffFileArg) { "$NAME_FILE cannot equal $NAME_DIFF_FILE" }
        outFileArg.requireFileDoesNotExist(NAME_OUT_FILE)

        try {
            run(fileArg, diffFileArg, outFileArg)
        } catch (t: Throwable) {
            // TODO: Clean up
            throw t
        }
    }

    private fun run(file: Path, diff: Path, outFile: Path) {
        // TODO
        println("""
            $NAME_FILE: $file
            $NAME_DIFF_FILE: $diff
            $NAME_OUT_FILE: $outFile
        """.trimIndent())
    }

    internal companion object {
        internal const val NAME_CMD = "apply"

        internal const val NAME_FILE = "file"
        internal const val NAME_DIFF_FILE = "diff-file"
        internal const val NAME_OUT_FILE = "out-file"

        @Throws(IllegalArgumentException::class)
        internal fun from(
            file: Path,
            diff: Path,
            outFile: Path,
        ): Apply {
            return object : Apply() {
                override val fileArg: Path = file
                override val diffFileArg: Path = diff
                override val outFileArg: Path = outFile
            }
        }
    }
}
