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

package io.matthewnelson.differ.internal.create

import io.matthewnelson.differ.internal.ArgTypePath
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.default
import okio.Path

internal class CreateCmd: Create() {
    override val file1Arg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_FILE_1,
        description = "The first file (e.g. /path/to/file-unsigned)"
    )

    override val file2Arg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_FILE_2,
        description = "The second file to diff against the first file (e.g. /path/to/file-signed)"
    )

    override val createReadableOpt: Boolean by option(
        type = ArgType.Boolean,
        fullName = NAME_CREATE_READABLE,
        description = "Also creates a human readable text file of the diff to the specified out-dir"
    ).default(true)

    override val diffFileNameOpt: String by option(
        type = ArgType.String,
        fullName = NAME_DIFF_FILE_NAME,
        description = "The name of the generated diff file. Default: <file1 name>.diff (e.g. file-unsigned.diff)"
    ).default("")

    override val outDirArg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_OUT_DIR,
        description = "The directory to output the generated diff file to (e.g. /path/to/directory)"
    )
}
