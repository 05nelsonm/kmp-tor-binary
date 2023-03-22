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

import io.matthewnelson.differ.internal.CreateReadableOpt
import io.matthewnelson.differ.internal.DiffDirArg
import io.matthewnelson.differ.internal.DiffFileExtNameOpt
import io.matthewnelson.differ.internal.Subcommand
import okio.FileSystem
import okio.Path

internal abstract class DirCreate(
    protected val fs: FileSystem,
    protected val runner: Runner,
): Subcommand(
    name = NAME_CMD,
    description = """
        Creates diff files from 2 identically structured
        directories. Walks the entire file tree of both
        directories and outputs diff files to the specified
        ${DiffDirArg.NAME_ARG} when encoutering differences.
        Both directories MUST have an identical file structure.
    """,
),  DiffDirArg,
    DiffFileExtNameOpt,
    CreateReadableOpt
{

    protected abstract val dir1Arg: Path
    protected abstract val dir2Arg: Path
    abstract override val createReadableOpt: Boolean
    abstract override val diffFileExtNameOpt: String
    abstract override val diffDirArg: Path

    final override fun execute() {
        // TODO: Validate

        try {
            runner.run(
                fs = fs,
            )
        } catch (t: Throwable) {
            // TODO: Cleanup
            throw t
        }
    }

    internal interface Runner {

        @Throws(Throwable::class)
        fun run(fs: FileSystem, )

        companion object: Runner {

            @Throws(Throwable::class)
            override fun run(fs: FileSystem, ) {
                // TODO
            }
        }
    }

    internal companion object {
        internal const val NAME_CMD = "dir-create"

        internal const val NAME_DIR_1 = "dir1"
        internal const val NAME_DIR_2 = "dir2"

        internal fun from(
            fs: FileSystem,
            runner: Runner,
            dir1: Path,
            dir2: Path,
            createReadable: Boolean,
            diffFileExtName: String,
            diffDir: Path,
        ): DirCreate {
            return object : DirCreate(fs = fs, runner = runner) {
                override val dir1Arg: Path = dir1
                override val dir2Arg: Path = dir2
                override val createReadableOpt: Boolean = createReadable
                override val diffFileExtNameOpt: String = diffFileExtName
                override val diffDirArg: Path = diffDir
            }
        }
    }
}
