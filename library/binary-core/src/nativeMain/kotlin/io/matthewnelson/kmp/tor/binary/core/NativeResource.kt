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
package io.matthewnelson.kmp.tor.binary.core

import io.matthewnelson.encoding.base16.Base16
import io.matthewnelson.encoding.base64.Base64
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArray
import io.matthewnelson.encoding.core.Encoder.Companion.encodeToString
import org.kotlincrypto.hash.sha2.SHA256

/**
 * Runtime abstraction for resources that have been transformed for
 * Kotlin Multi-Platform native
 *
 * See [resource-cli](https://github.com/05nelsonm/kmp-tor-binary/tree/master/tools/resource-cli)
 *
 * @param [version] A number that mitigates breaking things for
 *   resources that have already been transformed.
 * @param [name] The original file name of the transformed resource (e.g. test.txt)
 * @param [size] The byte size
 * @param [sha256] The pre-computed sha256 value of the transformed resource
 * @param [chunks] The number of [Chunk]s that the transformed resource has
 * @see [get]
 * @see [read]
 * */
@InternalKmpTorBinaryApi
public abstract class NativeResource protected constructor(
    public val version: Int,
    public val name: String,
    public val size: Long,
    public val sha256: String,
    public val chunks: Long,
) {

    /**
     * Representation of a chunk of 4096 bytes (max) for the given [NativeResource].
     *
     * [data] must be base64 encoded with a line break interval of 64 characters.
     *
     * e.g.
     *
     *     internal object resource_test: NativeResource(...) {
     *
     *         // ...
     *
     *         private const val _0 =
     *     """lwoX38K5t3XfmghnUwlp/P90llztH3XiN5HgnX+spuJU/hOHVa74hSXxjOHYRFRb
     *     1uK1+Whh5OLpaebxdC8YWuQSUBk9x8yO6diF/esZ3/3Wy45P2IkaZKFAte89YER/
     *     CWrxl6zZA+TWugTn8+SH3zrI11dgfFPxEdr/J4PHRqQXwDUc1oPLu0suUzb4nJ6v
     *     E6CltGjxTVTk4Hjn0g65pHmqy6V0ydVD2pv+5yoEOLDE8YnRGlvvtjYw5m4audjI
     *     LSaly0TABnXJr8WMW2gmAYQocMLm2c9KGO5ibS5re6VDdh1mhkx7qZSc0f2A8xkc
     *     38KyW/5AoKUOdtolSMQTrmFSjKuA7frgaS3lRDOMSgk16SI02yqcLZATBgCDxkjU
     *     QmWggfyZJlCqqGvzNEZNnjpU/N62HXpljIx3RGDP4b4yGxv3xaz2Tm+CkdbjYvfq
     *     dIP/sBDAi6Pie3pqQiAJ2JTAx/It4Vn7Ad/Q0cjhcNLBQyzRKh0AxOd71q3y9iax
     *     XPDHJYpfB+1g5ZusDKHYsM6F1iqN+Xmy4yC8mRs21wCWUo84tdnVJ7/hDsv9DRzY
     *     oIQPFIQ3bDuTw2bVzp27zd2qKPuJtykSAjjUHI5l5KAPPJ0gnUvucacbPcYAlCwv
     *     5DaeGmpTTJVHJqQI1IS7xkYv+qYswfoaRap6igh9Cms="""
     *
     *     }
     *
     * @see [toChunk]
     * @see [decode]
     * */
    public value class Chunk
    @Throws(IllegalStateException::class)
    internal constructor(public val data: String) {

        init {
            check(data.length <= 5549) {
                "data must be of length less than or equal to 5549 " +
                "(Maximum of 4096 bytes Base64 encoded with a line break interval of 64)"
            }
        }

        /**
         * Decodes the base64 encoded chunk of [data]
         *
         * @throws [RuntimeException] if there was a decoding error
         * */
        @Throws(RuntimeException::class)
        public fun decode(): ByteArray = data.decodeToByteArray(Base64.Default)
    }

    protected companion object {

        @Throws(IllegalStateException::class)
        protected fun String.toChunk(): Chunk = Chunk(this)
    }

    /**
     * Returns a [Chunk] at the provided [index]
     *
     * @see [Chunk]
     * @throws [IllegalStateException] if the data provided to [Chunk] is
     *   improperly formatted
     * @throws [IndexOutOfBoundsException] if [index] is negative or exceeds [chunks]
     * */
    @Throws(IllegalStateException::class, IndexOutOfBoundsException::class)
    public abstract operator fun get(index: Long): Chunk

    /**
     * Reads the entire [NativeResource] with validation of the [size]
     * and [sha256] values upon completion.
     *
     * e.g.
     *
     *     myFile.outputStream().use { oStream ->
     *         resource_test_txt.read { buffer, len ->
     *             oStream.write(buffer, 0, len)
     *         }
     *     }
     *
     * @param [block] a firehose of bytes to redirect how you wish.
     * @throws [RuntimeException] if:
     *   - [version] is unknown
     *   - [Chunk.data] was improperly formatted
     *   - A decoding failure occured
     *   - [size] did not match the number of decoded bytes
     *   - [sha256] did not match the decoded output
     * */
    @Throws(RuntimeException::class)
    public fun read(block: (buffer: ByteArray, len: Int) -> Unit) {
        // TODO: Implement version handling (after bumping to 2)
        check(version == 1) { "Unknown version[$version]" }
        if (chunks < 1) return

        val buf = ByteArray(4096)

        var b = 0
        val feed = Base64.Default.newDecoderFeed { byte -> buf[b++] = byte }

        try {
            val digest = SHA256()

            var i = 0L
            var decodedSize = 0L
            while (i < chunks) {
                this[i++].data.forEach { char -> feed.consume(char) }
                feed.flush()
                decodedSize += b
                if (b == 0) continue
                digest.update(buf, 0, b)
                block(buf, b)
                b = 0
            }

            // Validate provided size
            check(decodedSize == size) {
                "decodedSize[$decodedSize] did not match size[$size]"
            }

            // Validate sha256
            val decodedSha256 = digest.digest().encodeToString(Base16)
            check(decodedSha256.equals(sha256, ignoreCase = true)) {
                "decodedSha256[${decodedSha256.lowercase()}] did not match sha256[${sha256.lowercase()}]"
            }
        } finally {
            buf.fill(0)
            feed.close()
        }
    }

    final override fun equals(other: Any?): Boolean {
        return  other is NativeResource
                && other.sha256.equals(sha256, ignoreCase = true)
    }

    final override fun hashCode(): Int {
        var result = 17
        result = result * 42 + sha256.lowercase().hashCode()
        return result
    }

    final override fun toString(): String = buildString {
        appendLine("NativeResource: [")
        append("    version: ")
        appendLine(version)
        append("    name: ")
        appendLine(name)
        append("    size: ")
        appendLine(size)
        append("    sha256: ")
        appendLine(sha256.lowercase())
        append("    chunks: ")
        appendLine(chunks)
        append(']')
    }
}
