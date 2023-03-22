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

import io.matthewnelson.differ.internal.ArgTypePath
import io.matthewnelson.differ.internal.Subcommand
import io.matthewnelson.differ.internal.create.Create
import io.matthewnelson.differ.internal.create.DirCreateCmd
import kotlinx.cli.ArgType
import kotlinx.cli.default
import okio.Path

internal class DirApplyCmd: Subcommand(
    name = NAME_CMD,
    description = """
        Applies diff files from previously created
        ${DirCreateCmd.NAME_CMD} to the specified $NAME_DIR.
    """,
    additionalIndent = 1,
) {
    private val dirArg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_DIR,
        description = "The directory to apply the file diffs to (e.g. /path/to/program-unsigned)",
    )

    private val diffDirArg: Path by argument(
        type = ArgTypePath,
        fullName = Create.NAME_DIFF_DIR,
        description = "The directory of diff files to be applied to $NAME_DIR (e.g. /path/to/program-unsigned-diffs)",
    )

    private val outDirArg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_OUT_DIR,
        description = "The new directory for all files of $NAME_DIR with diffs from ${Create.NAME_DIFF_DIR} applied (e.g. /path/to/program-signed)",
    )

    private val diffFileExtNameOpt: String by option(
        type = ArgType.String,
        fullName = Create.NAME_DIFF_FILE_EXT,
        description = "The file extension name used when diff files were created with ${DirCreateCmd.NAME_CMD}"
    ).default(Create.DEFAULT_EXT)

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
        internal const val NAME_CMD = "dir-apply"

        internal const val NAME_DIR = "dir"
        internal const val NAME_OUT_DIR = "out-dir"
    }
}
