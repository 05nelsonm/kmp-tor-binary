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
import io.matthewnelson.differ.internal.CreateReadableOpt.Companion.createReadableOption
import io.matthewnelson.differ.internal.DiffDirArg
import io.matthewnelson.differ.internal.DiffDirArg.Companion.diffDirArgument
import io.matthewnelson.differ.internal.DiffFileExtNameOpt.Companion.diffFileExtNameOption
import okio.Path

internal class CreateCmd: Create() {
    override val file1Arg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_FILE_1,
        description = "The first file (e.g. /path/to/unsigned/file)"
    )

    override val file2Arg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_FILE_2,
        description = "The second file to diff against the first file (e.g. /path/to/signed/file)"
    )

    override val createReadableOpt: Boolean by createReadableOption(
        description = "Also creates a human readable text file of the diff to the specified ${DiffDirArg.NAME_ARG}"
    )

    override val diffFileExtNameOpt: String by diffFileExtNameOption(
        description = "The file extension name to use for the diff file"
    )

    override val diffDirArg: Path by diffDirArgument(
        description = "The directory to output the generated diff file to (e.g. /path/to/diffs)"
    )
}
