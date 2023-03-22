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
package io.matthewnelson.differ

import io.matthewnelson.differ.internal.CmdApply
import io.matthewnelson.differ.internal.CmdCreate
import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli

private const val PROGRAM_NAME = "differ"

public fun main(args: Array<String>) {
    val parser = ArgParser(programName = PROGRAM_NAME)

    val create = CmdCreate()
    val apply = CmdApply()

    @OptIn(ExperimentalCli::class)
    parser.subcommands(create, apply)

    val helpOrArgs = when {
        args.isEmpty() -> {
            printHeader()
            arrayOf("-h")
        }
        else -> {
            when (args.first()) {
                "-h", "--help" -> {
                    printHeader()
                }
            }
            args
        }
    }

    try {
        parser.parse(helpOrArgs)
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

private fun printHeader() {
    // TODO: BuildConfig https://github.com/gmazzo/gradle-buildconfig-plugin
    val version = "0.1.0"
    val url = "https://github.com/05nelsonm/kmp-tor-binary/tools/differ"

    val programName = PROGRAM_NAME.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

    println("""
        $programName v$version
        Copyright (C) 2023 Matthew Nelson
        Apache 2.0 License
        
        Creates and applies diffs to files.
        
        Project: $url

    """.trimIndent())
}
