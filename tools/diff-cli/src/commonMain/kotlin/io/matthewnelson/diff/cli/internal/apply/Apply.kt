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
package io.matthewnelson.diff.cli.internal.apply

import io.matthewnelson.diff.cli.internal.ArgDiffFile
import io.matthewnelson.diff.cli.internal.ArgDiffFile.Companion.diffFileArgument
import io.matthewnelson.diff.cli.internal.OptQuiet.Companion.quietOption
import io.matthewnelson.diff.cli.internal.Subcommand
import io.matthewnelson.diff.core.Diff
import kotlinx.cli.ArgType

internal class Apply: Subcommand(
    name = NAME_CMD,
    description = """
        Applies a diff to it's associated file.
        $NAME_FILE is modified in place.
    """,
    additionalIndent = 7,
),  ArgDiffFile
{

    override val diffFileArg: String by diffFileArgument(
        description = "The previously created diff file to be applied (e.g. /path/to/diffs/file.diff)",
    )

    private val fileArg: String by argument(
        type = ArgType.String,
        fullName = NAME_FILE,
        description = "The file to apply the diff to (e.g. /path/to/unsigned/file)",
    )

    override val quietOpt: Boolean by quietOption()

    override fun execute() {
        with(settings()) {
            Diff.apply(diffFileArg, fileArg)
            println("Diff applied to [$fileArg]")
        }
    }

    internal companion object {
        internal const val NAME_CMD = "apply"

        internal const val NAME_FILE = "file"
    }
}
