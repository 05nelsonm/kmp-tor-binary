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
import okio.Path
import okio.Path.Companion.toPath

internal class CmdCreate: Subcommand(
    name = "create",
    description = """
        Creates a diff from 2 file inputs. The first file is
        compared to the second file whereby any differences
        that the second file has will be recorded.
    """
) {
    private val file1Arg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_FILE_1,
        description = "The first file (e.g. /path/to/file-unsigned)"
    )

    private val file2Arg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_FILE_2,
        description = "The second file to diff against the first file (e.g. /path/to/file-signed)"
    )

    // TODO: Maybe?
    private val createReadableOpt: Boolean by option(
        type = ArgType.Boolean,
        fullName = NAME_CREATE_READABLE,
        description = "Also creates a human readable text file of the diff to the specified out-dir"
    ).default(true)

    private val diffFileNameOpt: String by option(
        type = ArgType.String,
        fullName = NAME_DIFF_FILE_NAME,
        description = "The name of the generated diff file. Default: <file1 name>.diff (e.g. file-unsigned.diff)"
    ).default("")

    private val outDirArg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_OUT_DIR,
        description = "The directory to output the generated diff file to (e.g. /path/to/directory)"
    )

    override fun execute() {
        file1Arg.requireFileExistAndNotEmpty(NAME_FILE_1)
        file2Arg.requireFileExistAndNotEmpty(NAME_FILE_2)
        require(file1Arg != file2Arg) { "$NAME_FILE_1 cannot equal $NAME_FILE_2" }
        outDirArg.requireDirOrNull(NAME_OUT_DIR)
        val diffFile = outDirArg.resolve(diffFileNameOpt.ifBlank { file1Arg.name + ".diff" })
        diffFile.requireFileDoesNotExist(NAME_OUT_DIR)
        val humanReadablefile = if (createReadableOpt) "$diffFile.txt".toPath() else null

        try {
            run(file1Arg, file2Arg, diffFile, humanReadablefile)
        } catch (t: Throwable) {
            // TODO: Clean up
            throw t
        }
    }

    @Throws(Throwable::class)
    internal fun run(file1: Path, file2: Path, diffFile: Path, hrFile: Path?) {
        // TODO
        println("""
            $NAME_FILE_1: $file1
            $NAME_FILE_2: $file2
            diffFile: $diffFile
            humanReadableFile: $hrFile
        """.trimIndent())
    }

    private companion object {
        private const val NAME_FILE_1 = "file1"
        private const val NAME_FILE_2 = "file2"
        private const val NAME_CREATE_READABLE = "create-readable"
        private const val NAME_DIFF_FILE_NAME = "diff-name"
        private const val NAME_OUT_DIR = "out-dir"
    }
}
