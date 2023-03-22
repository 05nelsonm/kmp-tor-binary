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
package io.matthewnelson.differ.internal

import okio.Path

internal class CmdApply: Subcommand(
    name = "apply",
    description = """
        Applies a diff to it's associated file and outputs
        the new file to specified path.
    """,
    additionalIndent = 1,
) {
    private val fileArg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_FILE,
        description = "The file to apply the diff to (e.g. /path/to/file-unsigned)",
    )

    private val diffArg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_DIFF,
        description = "The previously created diff file to be applied (e.g. /path/to/file-unsigned.diff)",
    )

    private val outFileArg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_OUT_FILE,
        description = "The new file after the diff has been applied (e.g. /path/to/file-signed)",
    )

    override fun execute() {
        fileArg.requireFileExistAndNotEmpty(NAME_FILE)
        diffArg.requireFileExistAndNotEmpty(NAME_DIFF)
        require(fileArg != diffArg) { "$NAME_FILE cannot equal $NAME_DIFF" }
        outFileArg.requireFileDoesNotExist(NAME_OUT_FILE)

        try {
            run(fileArg, diffArg, outFileArg)
        } catch (t: Throwable) {
            // TODO: Clean up
            throw t
        }
    }

    @Throws(Throwable::class)
    internal fun run(file: Path, diff: Path, outFile: Path) {
        // TODO
        println("""
            $NAME_FILE: $file
            $NAME_DIFF: $diff
            $NAME_OUT_FILE: $outFile
        """.trimIndent())
    }

    private companion object {
        private const val NAME_FILE = "file"
        private const val NAME_DIFF = "diff"
        private const val NAME_OUT_FILE = "out-file"
    }
}
