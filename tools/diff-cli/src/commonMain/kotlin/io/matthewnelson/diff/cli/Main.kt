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
package io.matthewnelson.diff.cli

import io.matthewnelson.diff.cli.internal.apply.Apply
import io.matthewnelson.diff.cli.internal.create.Create
import io.matthewnelson.diff.cli.internal.header.PrintHeader
import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli

private const val PROGRAM_NAME = "Diff-CLI"

public fun main(args: Array<String>) {
    val parser = ArgParser(programName = PROGRAM_NAME.lowercase().replace(' ', '-'))

    // TODO: Change Create/Apply indents when enabling DirCreate and DirApply
    val create = Create()
//    val dirCreate = DirCreate()
    val apply = Apply()
//    val dirApply = DirApply()
    val printHeader = PrintHeader()

    @OptIn(ExperimentalCli::class)
    parser.subcommands(create, /*dirCreate,*/ apply, /*dirApply,*/ printHeader)

    val helpOrArgs = when {
        args.isEmpty() -> {
            printHeader()
            arrayOf("-h")
        }
        else -> {
            when (args.first()) {
                "-h", "--help" -> printHeader()
            }
            args
        }
    }

    parser.parse(helpOrArgs)
}

private fun printHeader() {
    // TODO: BuildConfig https://github.com/gmazzo/gradle-buildconfig-plugin
    val versionName = "0.1.0"
    val url = "https://github.com/05nelsonm/kmp-tor-binary/tree/master/tools/diff-cli"

    println("""
        $PROGRAM_NAME v$versionName
        Copyright (C) 2023 Matthew Nelson
        Apache License, Version 2.0
        
        Compares files byte for byte and creates diffs
        which can be applied at a later date and time.
        Was created primarily for applying code signatures
        to reproducibly built software.
        
        Project: $url

    """.trimIndent())
}
