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
package io.matthewnelson.diff.core

import io.matthewnelson.component.value.clazz.ValueClazz
import io.matthewnelson.diff.core.internal.LINE_BREAK
import io.matthewnelson.diff.core.internal.writeNewLine
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import okio.BufferedSink
import okio.BufferedSource
import kotlin.jvm.JvmField
import kotlin.jvm.JvmSynthetic

public class Header
@Throws(IllegalStateException::class)
internal constructor(
    @JvmField public val schema: Diff.Schema,
    private val createdAtInstant: Instant,
    @JvmField public val forFileNamed: String,
    @JvmField public val forFileHash: String,
    @JvmField public val postApplyHash: String,
): ValueClazz(schema.toString() + createdAtInstant + forFileNamed + forFileHash + postApplyHash) {

    init {
        check(forFileHash.matches(REGEX)) { "forFileHash invalid sha256[$forFileHash]" }
        check(postApplyHash.matches(REGEX)) { "postApplyHash invalid sha256[$postApplyHash]" }
    }

    public fun createdAt(): Long = createdAtInstant.toEpochMilliseconds()

    internal fun writeTo(sink: BufferedSink) {
        with(sink) {
            writeUtf8(PREFIX_SCHEMA_VERSION)
            writeUtf8(schema.toString())
            writeNewLine()

            writeUtf8(PREFIX_CREATED_AT)
            writeUtf8(createdAtInstant.toString())
            writeNewLine()

            writeUtf8(PREFIX_FOR_FILE_NAME)
            writeUtf8(forFileNamed)
            writeNewLine()

            writeUtf8(PREFIX_FOR_FILE_HASH)
            writeUtf8(forFileHash)
            writeNewLine()

            writeUtf8(PREFIX_POST_APPLY_HASH)
            writeUtf8(postApplyHash)
            writeNewLine()
        }
    }

    override fun toString(): String {
        return """
            Header [
                schema: $schema
                createdAt: $createdAtInstant
                forFileNamed: $forFileNamed
                forFileHash: $forFileHash
                postApplyHash: $postApplyHash
            ]
        """.trimIndent()
    }

    internal companion object {
        private const val PREFIX_SCHEMA_VERSION: String = "$LINE_BREAK Diff Schema: " /* +Version */
        private const val PREFIX_CREATED_AT: String = "$LINE_BREAK Created At: "
        private const val PREFIX_FOR_FILE_NAME: String = "$LINE_BREAK For File Named: "
        private const val PREFIX_FOR_FILE_HASH: String = "$LINE_BREAK For File SHA256: "
        private const val PREFIX_POST_APPLY_HASH: String = "$LINE_BREAK Post Apply SHA256: "

        private val REGEX = "[a-f0-9]{64}".toRegex()

        @JvmSynthetic
        @Throws(IllegalStateException::class)
        internal fun BufferedSource.readDiffFileHeader(): Header {
            val versionString = readUtf8Line()
                ?.substringAfter(PREFIX_SCHEMA_VERSION)
                ?: throw IllegalStateException("Failed to read Diff schema version")

            val schema = Diff.Schema.from(versionString)

            val createdAtString = readUtf8Line()
                ?.substringAfter(PREFIX_CREATED_AT)
                ?: throw IllegalStateException("Failed to read Diff createdAt")

            val createdAt = try {
                createdAtString.toInstant()
            } catch (e: IllegalArgumentException) {
                throw IllegalStateException("Failed to read Diff createdAt", e)
            }

            val forFileName = readUtf8Line()
                ?.substringAfter(PREFIX_FOR_FILE_NAME)
                ?: throw IllegalStateException("Failed to read Diff forFileNamed")

            val forFileHash = readUtf8Line()
                ?.substringAfter(PREFIX_FOR_FILE_HASH)
                ?: throw IllegalStateException("Failed to read Diff fileHash")

            val postApplyHash = readUtf8Line()
                ?.substringAfter(PREFIX_POST_APPLY_HASH)
                ?: throw IllegalStateException("Failed to read Diff postApplyHash")

            return Header(schema, createdAt, forFileName, forFileHash, postApplyHash)
        }
    }
}
