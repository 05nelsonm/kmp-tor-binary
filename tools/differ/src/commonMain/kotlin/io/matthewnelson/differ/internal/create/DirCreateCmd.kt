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

import io.matthewnelson.differ.internal.ArgTypePath
import io.matthewnelson.differ.internal.Subcommand
import io.matthewnelson.differ.internal.apply.DirApplyCmd
import kotlinx.cli.ArgType
import kotlinx.cli.default
import okio.Path

internal class DirCreateCmd: Subcommand(
    name = NAME_CMD,
    description = """
        Creates diff files from 2 identically structured
        directories. Walks the entire file tree of both
        directories and outputs diff files to the specified
        ${DirApplyCmd.NAME_DIFF_DIR} when encoutering differences.
        Both directories MUST have an identical file structure.
    """,
) {
    private val dir1Arg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_DIR_1,
        description = "The first directory (e.g. /path/to/program-unsigned)",
    )

    private val dir2Arg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_DIR_2,
        description = "The second directory (identical structure to $NAME_DIR_1) to diff against the first (e.g. /path/to/program-signed)",
    )

    private val createReadableOpt: Boolean by option(
        type = ArgType.Boolean,
        fullName = Create.NAME_CREATE_READABLE,
        description = "Also create human readable text files for each diff"
    ).default(true)

    private val diffFileExtNameOpt: String by option(
        type = ArgType.String,
        fullName = Create.NAME_DIFF_FILE_EXT,
        description = "The file extension name to use when diff files are created"
    ).default(Create.DEFAULT_EXT)

    private val diffDirArg: Path by argument(
        type = ArgTypePath,
        fullName = DirApplyCmd.NAME_DIFF_DIR,
        description = "The directory to output the generated diff files (e.g. /path/to/program-unsigned-diffs)",
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
