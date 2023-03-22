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
import io.matthewnelson.differ.internal.ArgTypePath
import io.matthewnelson.differ.internal.CreateReadableOpt.Companion.createReadableOption
import io.matthewnelson.differ.internal.DiffDirArg
import io.matthewnelson.differ.internal.DiffDirArg.Companion.diffDirArgument
import io.matthewnelson.differ.internal.DiffFileExtNameOpt
import io.matthewnelson.differ.internal.DiffFileExtNameOpt.Companion.diffFileExtNameOption
import io.matthewnelson.differ.internal.Subcommand
import okio.FileSystem
import okio.Path

internal class DirCreateCmd(
    private val fs: FileSystem
): Subcommand(
    name = NAME_CMD,
    description = """
        Creates diff files from 2 identically structured
        directories. Walks the entire file tree of both
        directories and outputs diff files to the specified
        ${DiffDirArg.NAME_ARG} when encoutering differences.
        Both directories MUST have an identical file structure.
    """,
),  DiffDirArg,
    DiffFileExtNameOpt,
    CreateReadableOpt
{
    private val dir1Arg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_DIR_1,
        description = "The first directory (e.g. /path/to/unsigned/program)",
    )

    private val dir2Arg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_DIR_2,
        description = "The second directory (identical structure to $NAME_DIR_1) to diff against the first (e.g. /path/to/signed/program)",
    )

    override val createReadableOpt: Boolean by createReadableOption(
        description = "Also create human readable text files for each diff"
    )

    override val diffFileExtNameOpt: String by diffFileExtNameOption(
        description = "The file extension name to use when diff files are created"
    )

    override val diffDirArg: Path by diffDirArgument(
        description = "The directory to output the generated diff files (e.g. /path/to/diffs/program)",
    )

    override fun execute() {
        // TODO: Validate

        try {
            run()
        } catch (t: Throwable) {
            // TODO: Cleanup
            throw t
        }
    }

    @Throws(Throwable::class)
    private fun run() {
        // TODO
    }

    internal companion object {
        internal const val NAME_CMD = "dir-create"

        private const val NAME_DIR_1 = "dir1"
        private const val NAME_DIR_2 = "dir2"
    }
}
