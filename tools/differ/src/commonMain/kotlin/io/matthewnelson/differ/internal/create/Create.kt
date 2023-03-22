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
package io.matthewnelson.differ.internal.create

import io.matthewnelson.differ.internal.*
import io.matthewnelson.differ.internal.Subcommand
import io.matthewnelson.differ.internal.requireDirOrNull
import io.matthewnelson.differ.internal.requireFileDoesNotExist
import io.matthewnelson.differ.internal.requireFileExistAndNotEmpty
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath

internal abstract class Create(
    protected val fs: FileSystem
): Subcommand(
    name = NAME_CMD,
    description = """
        Creates a diff from 2 file inputs. The first file is
        compared to the second file whereby any differences
        that the second file has will be recorded.
    """,
    additionalIndent = 4,
),  DiffDirArg,
    DiffFileExtNameOpt,
    CreateReadableOpt
{

    protected abstract val file1Arg: Path
    protected abstract val file2Arg: Path

    abstract override val createReadableOpt: Boolean
    abstract override val diffFileExtNameOpt: String
    abstract override val diffDirArg: Path

    final override fun execute() {
        file1Arg.requireFileExistAndNotEmpty(NAME_FILE_1)
        file2Arg.requireFileExistAndNotEmpty(NAME_FILE_2)
        require(file1Arg != file2Arg) { "$NAME_FILE_1 cannot equal $NAME_FILE_2" }
        val mustCreate = diffDirArg.requireDirOrNull(DiffDirArg.NAME_ARG)
        diffFileExtNameOpt.requireDiffFileExtensionNameValid(DiffFileExtNameOpt.NAME_OPT)

        fs.createDirectories(diffDirArg, mustCreate = mustCreate)
        val canonicalDiffDir = fs.canonicalize(diffDirArg)

        val diffFile = canonicalDiffDir.resolve(file1Arg.name + diffFileExtNameOpt)
        diffFile.requireFileDoesNotExist(DiffDirArg.NAME_ARG)
        val humanReadablefile = if (createReadableOpt) "$diffFile.txt".toPath() else null
        humanReadablefile?.let { hrf ->
            hrf.requireFileDoesNotExist("Human readable file ${hrf.name}")
        }

        try {
            run(
                file1 = fs.canonicalize(file1Arg),
                file2 = fs.canonicalize(file2Arg),
                diffFile = diffFile,
                hrFile = humanReadablefile,
            )
        } catch (t: Throwable) {
            try {
                if (mustCreate) {
                    fs.deleteRecursively(canonicalDiffDir, mustExist = false)
                } else {
                    fs.delete(diffFile, mustExist = false)
                    if (humanReadablefile != null) {
                        fs.delete(humanReadablefile, mustExist = false)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            throw t
        }
    }

    @Throws(Throwable::class)
    private fun run(file1: Path, file2: Path, diffFile: Path, hrFile: Path?) {
        // TODO
        println("""
            $NAME_FILE_1: $file1
            $NAME_FILE_2: $file2
            diffFile: $diffFile
            humanReadableFile: $hrFile
        """.trimIndent())
    }

    internal companion object {
        internal const val NAME_CMD = "create"

        internal const val NAME_FILE_1 = "file1"
        internal const val NAME_FILE_2 = "file2"

        internal fun from(
            fs: FileSystem,
            file1: Path,
            file2: Path,
            createReadable: Boolean,
            diffFileExtName: String,
            diffDir: Path,
        ): Create {
            return object : Create(fs = fs) {
                override val file1Arg: Path = file1
                override val file2Arg: Path = file2
                override val createReadableOpt: Boolean = createReadable
                override val diffFileExtNameOpt: String = diffFileExtName
                override val diffDirArg: Path = diffDir
            }
        }
    }
}
