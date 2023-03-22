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
import io.matthewnelson.differ.internal.DiffDirArg
import io.matthewnelson.differ.internal.DiffDirArg.Companion.diffDirArgument
import io.matthewnelson.differ.internal.DiffFileExtNameOpt
import io.matthewnelson.differ.internal.DiffFileExtNameOpt.Companion.diffFileExtNameOption
import io.matthewnelson.differ.internal.Subcommand
import io.matthewnelson.differ.internal.create.DirCreateCmd
import okio.Path

internal class DirApplyCmd: Subcommand(
    name = NAME_CMD,
    description = """
        Applies diff files from previously created
        ${DirCreateCmd.NAME_CMD} to the specified $NAME_DIR.
        Files from $NAME_DIR are modified in place.
    """,
    additionalIndent = 1,
),  DiffDirArg,
    DiffFileExtNameOpt
{
    private val dirArg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_DIR,
        description = "The directory to apply the file diffs to (e.g. /path/to/unsigned/program)",
    )

    override val diffDirArg: Path by diffDirArgument(
        description = "The directory of diff files to be applied to $NAME_DIR (e.g. /path/to/diffs/program)",
    )

    override val diffFileExtNameOpt: String by diffFileExtNameOption(
        description = "The file extension name used when diff files were created with ${DirCreateCmd.NAME_CMD}"
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
        internal const val NAME_CMD = "dir-apply"

        internal const val NAME_DIR = "dir"
    }
}
