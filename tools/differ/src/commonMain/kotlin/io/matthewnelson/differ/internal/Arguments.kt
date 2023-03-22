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
package io.matthewnelson.differ.internal

import kotlinx.cli.ArgParser
import kotlinx.cli.DefaultRequiredType
import kotlinx.cli.SingleArgument
import okio.Path

internal interface ArgDiffDir {
    val diffDirArg: Path

    companion object {
        internal const val NAME_ARG = "diff-dir"

        internal fun ArgParser.diffDirArgument(
            description: String
        ): SingleArgument<Path, DefaultRequiredType.Required> {
            return argument(
                type = ArgTypePath,
                fullName = NAME_ARG,
                description = description
            )
        }
    }
}
