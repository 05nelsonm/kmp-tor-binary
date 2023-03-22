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
package io.matthewnelson.differ.cli.internal

import kotlinx.cli.*

internal interface OptCreateReadable {
    val createReadableOpt: Boolean

    companion object {
        internal const val NAME_OPT = "create-readable"

        internal fun ArgParser.createReadableOption(
            description: String
        ): SingleOption<Boolean, DefaultRequiredType.Default> {
            return option(
                type = ArgType.Boolean,
                fullName = NAME_OPT,
                description = description
            ).default(false)
        }
    }
}

internal interface OptDiffFileExtName {
    val diffFileExtNameOpt: String

    companion object {
        internal const val NAME_OPT = "diff-ext-name"
        internal const val DEFAULT_EXT = ".diff"

        internal fun ArgParser.diffFileExtNameOption(
            description: String
        ): SingleOption<String, DefaultRequiredType.Default> {
            return option(
                type = ArgType.String,
                fullName = NAME_OPT,
                description = description
            ).default(DEFAULT_EXT)
        }

        @Throws(IllegalArgumentException::class)
        internal fun String.requireDiffFileExtensionNameValid() {
            require(!contains(' ')) { "$NAME_OPT cannot contain white space" }
            require(lines().size == 1) { "$NAME_OPT cannot contain line breaks" }
            require(startsWith('.')) { "$NAME_OPT must start with a '.'" }
            require(length > 1) { "$NAME_OPT length must be greater than 1" }
        }
    }
}

internal interface OptQuiet {
    val quietOpt: Boolean

    companion object {
        internal const val NAME_OPT = "quiet"

        internal fun ArgParser.quietOption(): SingleOption<Boolean, DefaultRequiredType.Default> {
            return option(
                type = ArgType.Boolean,
                fullName = NAME_OPT,
                description = "Silences the terminal output"
            ).default(false)
        }
    }
}
