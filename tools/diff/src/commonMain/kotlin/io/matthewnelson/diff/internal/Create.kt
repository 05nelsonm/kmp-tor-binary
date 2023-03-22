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
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import okio.Path

internal class Create: DiffCommand(help = "Creates a diff from 2 file inputs. The first file is compared to the second file whereby any differences that the second file has will be recorded.") {
    private val file1: Path by argument(
        name = "<file1>",
        help = "The first file (e.g. /path/to/file-unsigned)"
    )
        .convert(conversion = PathConverter)
        .validate(RequireFileExistsAndNotEmpty)

    private val file2: Path by argument(
        name = "<file2>",
        help = "The second file to diff against the first file (e.g. /path/to/file-signed)"
    )
        .convert(conversion = PathConverter)
        .validate {
            RequireFileExistsAndNotEmpty(this, it)
            require(file1 != it) { "file1 cannot equal file2" }
        }

    // TODO: Maybe?
    private val createReadable: Boolean by option(
        names = arrayOf("-r", "--create-readable"),
        help = "Also creates a human readable text file of the diff to the specified <out dir>"
    ).flag()

    private val diffFileName: String by option(
        names = arrayOf("-n", "--name"),
        help = "The name of the generated diff file. Default: <file1 name>.diff (e.g. file-unsigned.diff)"
    )
        .defaultLazy { file1.name + ".diff" }

    private val outDir: Path by argument(
        name = "<out dir>",
        help = "The directory to output the generated diff file to (e.g. /path/to/directory)"
    )
        .convert(conversion = PathConverter)
        .validate {
            RequireFileDoesNotExist(this, it.resolve(diffFileName))
            RequireDirOrNull(this, it)
        }

    private val diffFile: Path by lazy { outDir.resolve(diffFileName) }

    override fun run() {
        // TODO
        echo("""
            file1: $file1
            file2: $file2
            --name: $diffFileName
            out dir: $outDir
        """.trimIndent())
    }
}
