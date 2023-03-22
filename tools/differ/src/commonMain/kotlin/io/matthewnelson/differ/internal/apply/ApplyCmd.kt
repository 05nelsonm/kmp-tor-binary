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
import okio.Path

internal class ApplyCmd: Apply() {
    override val fileArg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_FILE,
        description = "The file to apply the diff to (e.g. /path/to/unsigned/file)",
    )

    override val diffFileArg: Path by argument(
        type = ArgTypePath,
        fullName = NAME_DIFF_FILE,
        description = "The previously created diff file to be applied (e.g. /path/to/diff/file.diff)",
    )
}
