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

import okio.IOException
import okio.Path

internal class CmdApply: Subcommand(
    name = "apply",
    description = """
        Applies a diff to it's associated file and outputs
        the new file to specified path.
    """,
    additionalIndent = 1,
) {
    private val file: Path by argument(
        type = ArgTypePath,
        description = "The file to apply the diff to (e.g. /path/to/file-unsigned)",
    )

    private val diff: Path by argument(
        type = ArgTypePath,
        description = "The generated diff to be applied to file (e.g. /path/to/file-unsigned.diff)",
    )

    private val outFile: Path by argument(
        type = ArgTypePath,
        description = "The new file after the diff has been applied (e.g. /path/to/file-signed)",
    )

    override fun execute() {
        file.requireFileExistAndNotEmpty("file")
        diff.requireFileExistAndNotEmpty("diff")
        require(file != diff) { "file cannot equal diff" }
        outFile.requireFileDoesNotExist("outFile")

        try {
            run()
        } catch (e: IOException) {
            // TODO: Clean up
            throw e
        }
    }

    @Throws(IOException::class)
    private fun run() {
        println("""
            file: $file
            diff: $diff
            out: $outFile
        """.trimIndent())
    }
}
