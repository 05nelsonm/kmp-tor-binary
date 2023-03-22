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
package io.matthewnelson.diff.internal

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import okio.Path

internal class Apply : DiffCommand(help = "Applys a diff to it's associated file and outputs a new file") {
    private val file: Path by argument(
        name = "<file>",
        help = "The file to apply the diff to (e.g. /path/to/file-unsigned)"
    )
        .convert(conversion = PathConverter)
        .validate(RequireFileExistsAndNotEmpty)

    private val diff: Path by argument(
        name = "<diff file>",
        help = "The generated diff to be applied to file (e.g. /path/to/file-unsigned.diff)",
    )
        .convert(conversion = PathConverter)
        .validate(RequireFileExistsAndNotEmpty)

    private val outFile: Path by argument(
        name = "<out file>",
        help = "The new file after the diff has been applied (e.g. /path/to/file-signed)"
    )
        .convert(conversion = PathConverter)
        .validate(RequireFileDoesNotExist)

    override fun run() {
        // TODO
        echo("""
            file: $file
            diff: $diff
            out: $outFile
        """.trimIndent())
    }
}
