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
package io.matthewnelson.gzip.cli

import io.matthewnelson.cli.core.CLIRuntime
import io.matthewnelson.cli.core.OptQuiet
import io.matthewnelson.cli.core.OptQuiet.Companion.quietOption
import io.matthewnelson.gzip.cli.internal.gzip
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType

public fun main(args: Array<String>) {
    val runtime = GzipCLIRuntime()
    runtime.run(args)
    val gzippedPath = gzip(runtime.pathFile)

    if (runtime.quietOpt) return
    println("gzipped '${runtime.pathFile}' -> '$gzippedPath'")
}

private class GzipCLIRuntime: CLIRuntime(parser = ArgParser(PROGRAM_NAME.lowercase())), OptQuiet {

    private companion object {
        private const val PROGRAM_NAME = "Gzip-CLI"
    }

    val pathFile by parser.argument(
        type = ArgType.String,
        fullName = "path-file",
        description = "The absolute path of the file to gzip"
    )

    override val quietOpt: Boolean by parser.quietOption()

    override fun printHeader() {
        val versionName = "0.1.0"
        val url = "https://github.com/05nelsonm/kmp-tor-binary/tree/master/tools/gzip-cli"

        println("""
            $PROGRAM_NAME v$versionName
            Copyright (C) 2023 Matthew Nelson
            Apache License, Version 2.0

            Utility to gzip files for build reproducibility.
            
            Project: $url
    
        """.trimIndent())
    }
}
