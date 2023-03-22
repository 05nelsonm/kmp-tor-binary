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
package io.matthewnelson.diff.cli.internal.create

import io.matthewnelson.diff.cli.internal.*
import io.matthewnelson.diff.cli.internal.ArgDiffDir.Companion.diffDirArgument
import io.matthewnelson.diff.cli.internal.OptDiffFileExtName.Companion.diffFileExtNameOption
import io.matthewnelson.diff.cli.internal.OptQuiet.Companion.quietOption
import io.matthewnelson.diff.cli.internal.Subcommand
import io.matthewnelson.diff.core.Diff
import okio.Path

internal class Create: Subcommand(
    name = NAME_CMD,
    description = """
        Creates a diff from 2 file inputs. The first file is
        compared to the second file whereby any differences
        that the second file has will be recorded.
    """,
//    additionalIndent = 4,
),  ArgDiffDir,
    OptDiffFileExtName
{

    private val file1Arg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_FILE_1,
        description = "The first file (e.g. /path/to/unsigned/file)"
    )

    private val file2Arg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_FILE_2,
        description = "The second file to diff against the first file (e.g. /path/to/signed/file)"
    )

    override val diffDirArg: Path by diffDirArgument(
        description = "The directory to output the generated diff file to (e.g. /path/to/diffs)"
    )

    override val diffFileExtNameOpt: String by diffFileExtNameOption(
        description = "The file extension name to use for the diff file"
    )

    override val quietOpt: Boolean by quietOption()

    override fun execute() {
        try {
            Diff.create(
                file1 = file1Arg,
                file2 = file2Arg,
                diffDir = diffDirArg,
                options = Diff.Options(
                    diffFileExtensionName = diffFileExtNameOpt
                ),
            )
        } catch (t: Throwable) {
            // TODO
            throw t
        }
    }

    internal companion object {
        internal const val NAME_CMD = "create"

        internal const val NAME_FILE_1 = "file1"
        internal const val NAME_FILE_2 = "file2"
    }
}
