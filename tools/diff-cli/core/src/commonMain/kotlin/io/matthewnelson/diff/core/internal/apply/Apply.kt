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
package io.matthewnelson.diff.core.internal.apply

import io.matthewnelson.diff.core.Diff
import io.matthewnelson.diff.core.Header
import io.matthewnelson.diff.core.Header.Companion.readDiffHeader
import io.matthewnelson.diff.core.internal.BASE16
import io.matthewnelson.diff.core.internal.checkExistsAndIsFile
import io.matthewnelson.diff.core.internal.create.Create.Companion.EOF_HASH
import io.matthewnelson.diff.core.internal.hashLengthOf
import io.matthewnelson.encoding.core.Encoder.Companion.encodeToString
import okio.BufferedSource
import okio.FileSystem
import okio.IOException
import okio.Path
import org.kotlincrypto.hash.sha2.SHA256

internal sealed class Apply private constructor() {

    @Throws(IllegalStateException::class, IOException::class)
    internal abstract operator fun invoke(fs: FileSystem, header: Header, diffFile: BufferedSource, applyTo: Path)

    internal companion object {

        internal fun diff(fs: FileSystem, diffFile: Path, applyTo: Path) {
            check(diffFile != applyTo) { "Cannot apply a diff to itself" }
            applyTo.checkExistsAndIsFile(fs)
            diffFile.checkExistsAndIsFile(fs)

            // The very last line is the sha256 hash of all that was written
            // to the file after taking a diff. This is a validation check
            // of the diff file contents such that if the file was modified
            // the contents will not match the recorded sha256 value. Of course
            // there are ways around this, but it will work 99% of the time if
            // someone modifies the diff file.
            val (hashContent, hashEOF) = fs.read(diffFile) {
                val digest = SHA256()
                var hashEOF = ""
                val newLineByte = '\n'.code.toByte()

                while (true) {
                    val line = readUtf8Line()
                    when {
                        line == null -> break
                        line.length == 71 && line.startsWith(EOF_HASH) -> {
                            hashEOF = line.substringAfter(EOF_HASH)
                            break
                        }
                        else -> {
                            digest.update(line.encodeToByteArray())
                            digest.update(newLineByte)
                        }
                    }
                }

                val hashContent = digest.digest().encodeToString(BASE16)
                Pair(hashContent, hashEOF)
            }

            if (hashContent != hashEOF) {
                throw IllegalStateException(
                    "Validation check failed. Diff file contentHash[$hashContent] didn't " +
                    "match recordedHash[$hashEOF]. Was the Diff file modified?"
                )
            }

            fs.read(diffFile) {
                val header = readDiffHeader()
                val (hash, _) = fs.hashLengthOf(applyTo)
                if (header.forFileHash != hash) {
                    throw IllegalStateException(
                        "Cannot apply the diff. File's sha256[$hash] value does not " +
                        "match what the diff file has for it of sha256[${header.forFileHash}]."
                    )
                }

                when (header.schema) {
                    is Diff.Schema.v1 -> V1(fs, header, this, applyTo)
                }
            }
        }
    }

    private object V1: Apply() {

        @Throws(IllegalStateException::class, IOException::class)
        override operator fun invoke(fs: FileSystem, header: Header, diffFile: BufferedSource, applyTo: Path) {
            // TODO
            //  HashingSource
            //  apply diff to file.bak
            //  Check hash against header.file2Hash
            //  If good, atomic move
            println(header)
        }
    }
}
