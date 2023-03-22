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

import io.matthewnelson.differ.internal.Subcommand
import io.matthewnelson.differ.internal.requireDirOrNull
import io.matthewnelson.differ.internal.requireFileDoesNotExist
import io.matthewnelson.differ.internal.requireFileExistAndNotEmpty
import okio.Path
import okio.Path.Companion.toPath

internal abstract class Create: Subcommand(
    name = "create",
    description = """
        Creates a diff from 2 file inputs. The first file is
        compared to the second file whereby any differences
        that the second file has will be recorded.
    """
) {

    protected abstract val file1Arg: Path
    protected abstract val file2Arg: Path
    // TODO: Maybe?
    protected abstract val createReadableOpt: Boolean
    protected abstract val diffFileNameOpt: String
    protected abstract val outDirArg: Path

    final override fun execute() {
        file1Arg.requireFileExistAndNotEmpty(NAME_FILE_1)
        file2Arg.requireFileExistAndNotEmpty(NAME_FILE_2)
        require(file1Arg != file2Arg) { "$NAME_FILE_1 cannot equal $NAME_FILE_2" }
        outDirArg.requireDirOrNull(NAME_OUT_DIR)
        val diffFile = outDirArg.resolve(diffFileNameOpt.ifBlank { file1Arg.name + ".diff" })
        diffFile.requireFileDoesNotExist(NAME_OUT_DIR)
        val humanReadablefile = if (createReadableOpt) "$diffFile.txt".toPath() else null

        try {
            run(file1Arg, file2Arg, diffFile, humanReadablefile)
        } catch (t: Throwable) {
            // TODO: Clean up
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
        internal const val NAME_FILE_1 = "file1"
        internal const val NAME_FILE_2 = "file2"
        internal const val NAME_CREATE_READABLE = "create-readable"
        internal const val NAME_DIFF_FILE_NAME = "diff-name"
        internal const val NAME_OUT_DIR = "out-dir"

        @Throws(IllegalArgumentException::class)
        internal fun from(
            file1: Path,
            file2: Path,
            createReadable: Boolean,
            diffFileName: String,
            outDir: Path
        ): Create {
            return object : Create() {
                override val file1Arg: Path = file1
                override val file2Arg: Path = file2
                override val createReadableOpt: Boolean = createReadable
                override val diffFileNameOpt: String = diffFileName
                override val outDirArg: Path = outDir
            }
        }
    }
}
