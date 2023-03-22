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

import io.matthewnelson.diff.cli.internal.ArgTypePath
import io.matthewnelson.diff.cli.internal.OptQuiet.Companion.quietOption
import io.matthewnelson.diff.cli.internal.Subcommand
import io.matthewnelson.diff.core.Diff
import io.matthewnelson.diff.core.internal.InternalDiffApi
import okio.FileSystem
import okio.Path

internal class Apply(
    private val fs: FileSystem,
): Subcommand(
    name = NAME_CMD,
    description = """
        Applies a diff to it's associated file.
        $NAME_FILE is modified in place.
    """,
    additionalIndent = 1,
) {

    private val diffFileArg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_DIFF_FILE,
        description = "The previously created diff file to be applied (e.g. /path/to/diff/file.diff)",
    )

    private val fileArg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_FILE,
        description = "The file to apply the diff to (e.g. /path/to/unsigned/file)",
    )

    override val quietOpt: Boolean by quietOption()

    override fun execute() {
        @OptIn(InternalDiffApi::class)
        Diff.apply(fs, diffFileArg, fileArg)
    }

    internal companion object {
        internal const val NAME_CMD = "apply"

        internal const val NAME_FILE = "file"
        internal const val NAME_DIFF_FILE = "diff-file"
    }
}
