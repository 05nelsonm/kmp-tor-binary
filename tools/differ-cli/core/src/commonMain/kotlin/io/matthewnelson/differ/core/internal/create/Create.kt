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
package io.matthewnelson.differ.core.internal.create

import io.matthewnelson.differ.core.Differ
import io.matthewnelson.differ.core.Header
import io.matthewnelson.differ.core.NoDiffException
import io.matthewnelson.differ.core.internal.*
import io.matthewnelson.differ.core.internal.hashLengthOf
import io.matthewnelson.differ.core.internal.checkIsDirOrNull
import io.matthewnelson.differ.core.internal.checkExistsAndIsFile
import kotlinx.datetime.Clock
import okio.*

internal sealed class Create private constructor() {

    @Throws(IllegalStateException::class, IOException::class)
    internal abstract operator fun invoke(
        fs: FileSystem,
        file1: BufferedSource,
        file1Length: Long,
        file2: BufferedSource,
        file2Length: Long,
        diffFile: BufferedSink,
    )

    internal companion object {

        internal const val EOF_HASH: String = "$LINE_BREAK END: " /* +sha256 */

        @Throws(IllegalArgumentException::class, IllegalStateException::class, IOException::class)
        internal fun diff(
            fs: FileSystem,
            file1: Path,
            file2: Path,
            diffDir: Path,
            options: Differ.Options,
        ): Path {
            require(file1 != file2) { "files cannot be the same" }

            file1.checkExistsAndIsFile(fs)

            // Maybe write a diff file to delete file1 instead of failing???
            // Maybe as an option to enable this, and throw by default???
            file2.checkExistsAndIsFile(fs)

            val mustCreate = diffDir.checkIsDirOrNull(fs)
            options.validateExtensionName()

            val (f1Hash, f1Length) = fs.hashLengthOf(file1)
            val (f2Hash, f2Length) = fs.hashLengthOf(file2)

            if (f1Hash == f2Hash) {
                throw NoDiffException("No differences found between [$file1] and [$file2]")
            }

            fs.createDirectories(diffDir, mustCreate = mustCreate)
            val canonicalDiffDir = fs.canonicalize(diffDir)
            val diffFile = canonicalDiffDir.resolve(file1.name + options.diffFileExtensionName)

            try {
                val hash = HashingSink.sha256(fs.sink(diffFile, mustCreate = true)).use { hs ->
                    hs.buffer().use { bs ->

                        val header = Header(options.schema, Clock.System.now(), file1.name, f1Hash, f2Hash)
                        header.writeTo(bs)

                        fs.read(file1) file1@{
                            fs.read(file2) file2@{

                                when (options.schema) {
                                    is Differ.Schema.v1 -> {
                                        V1(fs, this@file1, f1Length, this@file2, f2Length, bs)
                                    }
                                }

                            }
                        }

                        bs.writeNewLine()
                    }

                    hs.hash.hex()
                }

                // Write the diff file content's hash to the very last line
                // of the file. This is used as a validation check when going
                // to apply it to file1 later such that any modifications to
                // the diff will throw an exception.
                fs.appendingSink(diffFile, mustExist = true).buffer().use {
                    it.writeUtf8(EOF_HASH)
                    it.writeUtf8(hash)
                    it.writeNewLine()
                }
            } catch (t: Throwable) {
                // TODO: Clean up

                throw t
            }

            return diffFile
        }
    }

    private object V1: Create() {
        override operator fun invoke(
            fs: FileSystem,
            file1: BufferedSource,
            file1Length: Long,
            file2: BufferedSource,
            file2Length: Long,
            diffFile: BufferedSink,
        ) {
            // TODO
        }
    }
}
