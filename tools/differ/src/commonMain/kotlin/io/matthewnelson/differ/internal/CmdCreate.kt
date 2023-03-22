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
@file:OptIn(ExperimentalCli::class)

package io.matthewnelson.differ.internal

import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.default
import okio.IOException
import okio.Path

internal class CmdCreate: Subcommand(
    name = "create",
    description = """
        Creates a diff from 2 file inputs. The first file is
        compared to the second file whereby any differences
        that the second file has will be recorded.
    """
) {
    private val file1: Path by argument(
        type = ArgTypePath,
        description = "The first file (e.g. /path/to/file-unsigned)"
    )

    private val file2: Path by argument(
        type = ArgTypePath,
        description = "The second file to diff against the first file (e.g. /path/to/file-signed)"
    )

    // TODO: Maybe?
    private val createReadable: Boolean by option(
        type = ArgType.Boolean,
        fullName = "create-readable",
        shortName = "r",
        description = "Also creates a human readable text file of the diff to the specified out-dir"
    ).default(true)

    private val diffFileName: String by option(
        type = ArgType.String,
        fullName = "name",
        shortName = "n",
        description = "The name of the generated diff file. Default: <file1 name>.diff (e.g. file-unsigned.diff)"
    ).default("")

    private val outDir: Path by argument(
        type = ArgTypePath,
        fullName = "out-dir",
        description = "The directory to output the generated diff file to (e.g. /path/to/directory)"
    )

    private val diffFile: Path by lazy { outDir.resolve(diffFileName.ifBlank { file1.name + ".diff" }) }

    override fun execute() {
        file1.requireFileExistAndNotEmpty("file1")
        file2.requireFileExistAndNotEmpty("file2")
        require(file1 != file2) { "file1 cannot equal file2" }
        outDir.requireDirOrNull("out-dir")
        diffFile.requireFileDoesNotExist("out-dir")

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
            file1: $file1
            file2: $file2
            createReadable: $createReadable
            diffFileName: ${diffFile.name}
            outDir: $outDir
        """.trimIndent())
    }
}
