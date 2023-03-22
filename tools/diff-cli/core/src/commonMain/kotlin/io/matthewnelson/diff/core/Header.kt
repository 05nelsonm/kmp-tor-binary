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
    @JvmField public val createdForName: String,
    @JvmField public val createdForHash: String,
    @JvmField public val createdFromHash: String,
): ValueClazz(schema.toString() + createdAtInstant + createdForName + createdForHash + createdFromHash) {

    init {
        check(createdForHash.matches(REGEX)) { "createdForHash invalid sha256[$createdForHash]" }
        check(createdFromHash.matches(REGEX)) { "createdFromHash invalid sha256[$createdFromHash]" }
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

            writeUtf8(PREFIX_CREATED_FOR_NAME)
            writeUtf8(createdForName)
            writeNewLine()

            writeUtf8(PREFIX_CREATED_FOR_HASH)
            writeUtf8(createdForHash)
            writeNewLine()

            writeUtf8(PREFIX_CREATED_FROM_HASH)
            writeUtf8(createdFromHash)
            writeNewLine()
        }
    }

    override fun toString(): String {
        return """
            DiffHeader [
                schema: $schema
                createdAt: $createdAtInstant
                createdForName: $createdForName
                createdForHash: $createdForHash
                createdFromHash: $createdFromHash
            ]
        """.trimIndent()
    }

    internal companion object {
        private const val PREFIX_SCHEMA_VERSION: String = "$LINE_BREAK Diff Schema: " /* +Version */
        private const val PREFIX_CREATED_AT: String = "$LINE_BREAK Created At: "
        private const val PREFIX_CREATED_FOR_NAME: String = "$LINE_BREAK Created For Name: "
        private const val PREFIX_CREATED_FOR_HASH: String = "$LINE_BREAK Created For Hash: "
        private const val PREFIX_CREATED_FROM_HASH: String = "$LINE_BREAK Created From Hash: "

        private val REGEX = "[a-f0-9]{64}".toRegex()

        @JvmSynthetic
        @Throws(IllegalStateException::class)
        internal fun BufferedSource.readDiffHeader(): Header {
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
                ?.substringAfter(PREFIX_CREATED_FOR_NAME)
                ?: throw IllegalStateException("Failed to read Diff createdForName")

            val forFileHash = readUtf8Line()
                ?.substringAfter(PREFIX_CREATED_FOR_HASH)
                ?: throw IllegalStateException("Failed to read Diff createdForHash")

            val postApplyHash = readUtf8Line()
                ?.substringAfter(PREFIX_CREATED_FROM_HASH)
                ?: throw IllegalStateException("Failed to read Diff createdFromHash")

            return Header(schema, createdAt, forFileName, forFileHash, postApplyHash)
        }
    }
}
