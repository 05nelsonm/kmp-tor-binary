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

import io.matthewnelson.differ.internal.DiffDirArg
import io.matthewnelson.differ.internal.DiffFileExtNameOpt
import io.matthewnelson.differ.internal.Subcommand
import io.matthewnelson.differ.internal.create.DirCreate
import okio.FileSystem
import okio.Path

internal abstract class DirApply(
    private val fs: FileSystem,
    private val runner: Runner,
): Subcommand(
    name = NAME_CMD,
    description = """
        Applies diff files from previously created
        ${DirCreate.NAME_CMD} to the specified $NAME_DIR.
        Files from $NAME_DIR are modified in place.
    """,
    additionalIndent = 1,
),  DiffDirArg,
    DiffFileExtNameOpt
{
    protected abstract val dirArg: Path
    abstract override val diffDirArg: Path
    abstract override val diffFileExtNameOpt: String

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
        internal const val NAME_CMD = "dir-apply"

        internal const val NAME_DIR = "dir"

        internal fun from(
            fs: FileSystem,
            runner: Runner,
            dir: Path,
            diffDir: Path,
            diffFileExtName: String
        ): DirApply {
            return object : DirApply(fs = fs, runner = runner) {
                override val dirArg: Path = dir
                override val diffDirArg: Path = diffDir
                override val diffFileExtNameOpt: String = diffFileExtName
            }
        }
    }
}
