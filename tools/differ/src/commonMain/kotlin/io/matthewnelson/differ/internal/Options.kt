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

import kotlinx.cli.*

internal interface DiffFileExtNameOpt {
    val diffFileExtNameOpt: String

    companion object {
        internal const val NAME_OPT = "diff-extension-name"
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
    }
}

internal interface CreateReadableOpt {
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
            ).default(true)
        }
    }
}
